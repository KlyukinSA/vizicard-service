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
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.dto.profile.response.CardResponse;
import vizicard.dto.profile.response.CompanyResponse;
import vizicard.model.*;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.model.detail.Skill;
import vizicard.repository.ContactRepository;
import vizicard.repository.RelationRepository;
import vizicard.service.CashService;
import vizicard.service.CloudFileService;
import vizicard.service.CompanyService;
import vizicard.service.ShortnameService;
import vizicard.utils.ProfileProvider;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private final CompanyService companyService;

    public BriefCardResponse mapToBrief(Card card) {
        BriefCardResponse res = modelMapper.map(card, BriefCardResponse.class);
        Optional<Card> overlay = findOverlay(card);
        if (overlay.isPresent()) {
            Integer id = res.getId();
            modelMapper.map(overlay.get(), res);
            res.setId(id);
        }
        res.setMainShortname(shortnameService.getMainShortname(card));
        res.setAvatar(getAvatar(card, overlay));
        return res;
    }

    private Optional<Card> findOverlay(Card card) {
        Relation relation = relationRepository.findByAccountOwnerAndCard(profileProvider.getUserFromAuth(), card);
        if (relation == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(relation.getOverlay());
    }

    public CardResponse mapToResponse(Card card) {
        CardResponse res = modelMapper.map(card, CardResponse.class); // TODO map except company and contacts and about
        Optional<Card> overlay = findOverlay(card);
        if (overlay.isPresent()) {
            Integer id = res.getId();
            Date createAt = res.getCreateAt();
            modelMapper.map(overlay.get(), res);
            res.setId(id);
            res.setCreateAt(createAt);
        }
        res.setContacts(getContactDTOs(card, overlay));
        res.setResume(getResume(card, overlay));
        res.setRelation(getPossibleRelation(card));
        res.setMainShortname(shortnameService.getMainShortname(card));
        finishCompany(res, card, overlay);
        res.setAvatar(getAvatar(card, overlay));
        res.setLastVizit(getLastVizit(card));
        return res;
    }

    private CloudFileDTO getAvatar(Card card, Optional<Card> overlay) {
        Integer avatarId = overlay.filter(c -> c.getAvatarId() != null).orElse(card).getAvatarId();
        if (avatarId != null) {
            CloudFile cloudFile = cloudFileService.findById(avatarId);
            if (cloudFile == null || !cloudFile.isStatus()) {
                return null;
            }
            return new CloudFileDTO(cloudFile.getId(), cloudFile.getUrl(), cloudFile.getAlbum().getId());
        }
        return null;
    }

    private void finishCompany(CardResponse res, Card card, Optional<Card> overlay) {
        Card company = companyService.getCompanyOf(card);
        if (overlay.isPresent()) {
            Card company1 = companyService.getCompanyOf(overlay.get());
            if (company1 != null && company1.isStatus()) {
                company = company1;
            }
        }
        if (company == null || !company.isStatus()) { // TODO function for same checks
            res.setCompany(null);
        } else if (!cashService.isPro(card.getAccount())) {
            BriefCardResponse dto = new BriefCardResponse();
            dto.setName(company.getName());
            res.setCompany(dto);
        } else {
            res.getCompany().setMainShortname(shortnameService.getMainShortname(company));
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

    private List<ContactResponse> getContactDTOs(Card card, Optional<Card> overlay) {
        List<Contact> list = contactRepository.findAllByOwner(card);
        if (overlay.isPresent()) {
            List<Contact> list1 = contactRepository.findAllByOwner(overlay.get());
            if (!list1.isEmpty()) {
                list = list1;
            }
        }
        return contactMapper.mapList(list);
    }

    private ProfileDetailStructResponseDTO getResume(Card card, Optional<Card> overlay) {
        ProfileDetailStruct detailStruct = card.getDetailStruct();
        if (overlay.isPresent()) {
            ProfileDetailStruct detailStruct1 = overlay.get().getDetailStruct();
            if (detailStruct1 != null) {
                detailStruct = detailStruct1;
            }
        }
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

    public CompanyResponse mapToCompanyResponse(Card company) {
        CompanyResponse res = modelMapper.map(company, CompanyResponse.class);
        Optional<Card> overlay = findOverlay(company);
        if (overlay.isPresent()) {
            Integer id = res.getId();
            Date createAt = res.getCreateAt();
            modelMapper.map(overlay.get(), res);
            res.setId(id);
            res.setCreateAt(createAt);
        }
        res.setContacts(getContactDTOs(company, overlay));
        res.setMainShortname(shortnameService.getMainShortname(company));
        res.setAvatar(getAvatar(company, overlay));
        res.setLastVizit(getLastVizit(company));
        return res;
    }

    private Date getLastVizit(Card card) {
        return card.getAccount().getLastVizit();
    }

}
