package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.*;
import vizicard.dto.contact.ContactResponse;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.model.CloudFile;
import vizicard.model.Relation;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.model.detail.Skill;
import vizicard.repository.ContactRepository;
import vizicard.repository.RelationRepository;
import vizicard.service.CashService;
import vizicard.service.CloudFileService;
import vizicard.service.ShortnameService;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CardMapper {

    private final ShortnameService shortnameService;
    private final RelationRepository relationRepository;
    private final ContactRepository contactRepository;
    private final CloudFileService cloudFileService;

    private final ModelMapper modelMapper;
    private final ContactMapper contactMapper;
    private final ProfileProvider profileProvider;
    private final CashService cashService;

    public BriefCardResponse mapToBrief(Card card) {
        BriefCardResponse res = modelMapper.map(card, BriefCardResponse.class);
        res.setMainShortname(shortnameService.getMainShortname(card));
        res.setAvatar(getAvatar(card));
        return res;
    }

    public CardResponse mapToResponse(Card card) {
        CardResponse res = modelMapper.map(card, CardResponse.class); // TODO map except company and contacts and about
        res.setContacts(getContactDTOs(card));
        res.setResume(getResume(card));
        res.setRelation(getPossibleRelation(card));
        res.setMainShortname(shortnameService.getMainShortname(card));
        finishCompany(res, card);
        res.setAvatar(getAvatar(card));
        return res;
    }

    private CloudFileDTO getAvatar(Card card) {
        Integer avatarId = card.getAvatarId();
        if (avatarId != null) {
            CloudFile cloudFile = cloudFileService.findById(avatarId);
            if (cloudFile == null || !cloudFile.isStatus()) {
                return null;
            }
            return new CloudFileDTO(cloudFile.getId(), cloudFile.getUrl(), cloudFile.getAlbum().getId());
        }
        return null;
    }

    private void finishCompany(CardResponse res, Card card) {
        if (card.getCompany() == null || !card.getCompany().isStatus()) { // TODO function for same checks
            res.setCompany(null);
        } else if (!cashService.isPro(card.getAccount())) {
            BriefCardResponse dto = new BriefCardResponse();
            dto.setName(card.getCompany().getName());
            res.setCompany(dto);
        } else {
            res.getCompany().setMainShortname(shortnameService.getMainShortname(card.getCompany()));
        }
    }

    private BriefRelationResponseDTO getPossibleRelation(Card card) {
        Account user = profileProvider.getUserFromAuth();
        Relation relation = relationRepository.findByAccountOwnerAndCard(user, card);
        if (relation == null) {
            relation = relationRepository.findByAccountOwnerAndCard(card.getAccount(), user.getCurrentCard());
            if (relation == null) {
                return null;
            }
        }
        if (Objects.equals(relation.getCard().getAccount().getId(), relation.getAccountOwner().getId())) {
            return null;
        }
        return modelMapper.map(relation, BriefRelationResponseDTO.class);
    }

    private List<ContactResponse> getContactDTOs(Card card) {
        return contactMapper.mapList(contactRepository.findAllByOwner(card));
    }

    private ProfileDetailStructResponseDTO getResume(Card card) {
        ProfileDetailStruct detailStruct = card.getDetailStruct();
        if (detailStruct == null) {
            return null;
        }
        return new ProfileDetailStructResponseDTO(
                detailStruct.getEducation().stream()
                        .filter(Education::isStatus)
                        .map((val) -> modelMapper.map(val, EducationResponseDTO.class))
                        .collect(Collectors.toList()),
                detailStruct.getExperience().stream()
                        .filter(Experience::isStatus)
                        .map((val) -> modelMapper.map(val, ExperienceResponseDTO.class))
                        .collect(Collectors.toList()),
                detailStruct.getSkills().stream()
                        .filter(Skill::isStatus)
                        .map((val) -> new SkillResponseDTO(val.getId(), val.getSkill()))
                        .collect(Collectors.toList())
        );
    }

}
