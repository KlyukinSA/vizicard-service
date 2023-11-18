package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.*;
import vizicard.dto.contact.ContactListResponse;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.dto.profile.response.CardResponse;
import vizicard.dto.profile.response.CompanyResponse;
import vizicard.dto.profile.response.ParamCardResponse;
import vizicard.dto.tab.TabResponseDTO;
import vizicard.model.*;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.model.detail.Skill;
import vizicard.repository.ContactRepository;
import vizicard.repository.RelationRepository;
import vizicard.repository.TabRepository;
import vizicard.repository.TabTypeRepository;
import vizicard.service.*;
import vizicard.utils.ProfileProvider;

import java.util.*;
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

    private final TabRepository tabRepository; // TODO TabMapper
    private final TabTypeRepository tabTypeRepository;
    private final AlbumService albumService;

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
        List<Tab> tabs = card.getTabs();
        card.setTabs(null);
        CardResponse res = modelMapper.map(card, CardResponse.class); // TODO combine with mapToCompanyResponse
        card.setTabs(tabs);
        Optional<Card> overlay = findOverlay(card);
        if (overlay.isPresent()) {
            Integer id = res.getId();
            Date createAt = res.getCreateAt();
            modelMapper.map(overlay.get(), res);
            res.setId(id);
            res.setCreateAt(createAt);
        }
        res.setContacts(getContacts(card, overlay));
        res.setResume(getResume(card, overlay));
        res.setRelation(getPossibleRelation(card));
        res.setMainShortname(shortnameService.getMainShortname(card));
        finishCompany(res, card, overlay);
        res.setAvatar(getAvatar(card, overlay));
        res.setBackground(getBackground(card, overlay));
        res.setLastVizit(getLastVizit(card));
        res.setTabs(getTabs(card));
        return res;
    }

    private CloudFileDTO getBackground(Card card, Optional<Card> overlay) {
        return getCloudFileDTOByFileId(overlay.filter(c -> c.getBackgroundId() != null).orElse(card).getBackgroundId());
    }

    private CloudFileDTO getCloudFileDTOByFileId(Integer id) {
        if (id != null) {
            CloudFile cloudFile = cloudFileService.findById(id);
            if (cloudFile == null || !cloudFile.isStatus()) {
                return null;
            }
            return new CloudFileDTO(cloudFile.getId(), cloudFile.getUrl(), cloudFile.getAlbum().getId(), cloudFile.getDescription());
        }
        return null;
    }

    private List<TabResponseDTO> getTabs(Card card) {
        List<TabResponseDTO> res = new ArrayList<>();
        List<Tab> tabs = tabRepository.findAllByCardOwner(card);
        HashSet<TabTypeEnum> usedTypes = new HashSet<>();
        Account user = profileProvider.getUserFromAuth();
        boolean isCurrentCard = user != null && user.getCurrentCard().getId().equals(card.getId());
        for (Tab tab : tabs) {
            if (isCurrentCard || !tab.isHidden()) {
                res.add(new TabResponseDTO(tab.getType().getWriting(), tab.getOrder()));
                usedTypes.add(tab.getType().getType());
            }
        }
        int i = tabs.stream().mapToInt(Tab::getOrder).max().orElse(0);
        finishTabType(usedTypes, TabTypeEnum.CONTACTS, isCurrentCard, !contactRepository.findAllByCardOwner(card).isEmpty(), res, i);
        i++;

        ProfileDetailStruct detailStruct = card.getDetailStruct();
        finishTabType(usedTypes, TabTypeEnum.RESUME, isCurrentCard, isResumeNotEmpty(detailStruct), res, i);
        i++;

        Integer albumId = card.getAlbum().getId();
        List<CloudFile> allFiles = albumService.getAllFiles(albumId, CloudFileType.FILE);
        finishTabType(usedTypes, TabTypeEnum.FILE, isCurrentCard, !allFiles.isEmpty(), res, i);
        i++;
        List<CloudFile> allMedia = albumService.getAllFiles(albumId, CloudFileType.MEDIA);
        finishTabType(usedTypes, TabTypeEnum.MEDIA, isCurrentCard, !allMedia.isEmpty(), res, i);
        return res;
    }

    private void finishTabType(HashSet<TabTypeEnum> usedTypes, TabTypeEnum type, boolean isCurrentCard, boolean cardHasOfThisType, List<TabResponseDTO> res, int i) {
        if (!usedTypes.contains(type) && (isCurrentCard || cardHasOfThisType)) {
            res.add(new TabResponseDTO(tabTypeRepository.findByType(type).getWriting(), i));
        }
    }

    private static boolean isResumeNotEmpty(ProfileDetailStruct detailStruct) {
        return detailStruct != null && detailStruct.getEducation().size() +
                detailStruct.getExperience().size() + detailStruct.getSkills().size() > 0;
    }

    private CloudFileDTO getAvatar(Card card, Optional<Card> overlay) {
        return getCloudFileDTOByFileId(overlay.filter(c -> c.getAvatarId() != null).orElse(card).getAvatarId());
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
            res.setCompany(mapToBrief(company));
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

    private ContactListResponse getContacts(Card card, Optional<Card> overlay) {
        List<Contact> list = contactRepository.findAllByCardOwner(card);
        if (overlay.isPresent()) {
            List<Contact> list1 = contactRepository.findAllByCardOwner(overlay.get());
            if (!list1.isEmpty()) {
                list = list1;
            }
        }
        if (list.isEmpty()) {
            return null;
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
        if (!isResumeNotEmpty(detailStruct)) {
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
        res.setContacts(getContacts(company, overlay));
        res.setMainShortname(shortnameService.getMainShortname(company));
        res.setAvatar(getAvatar(company, overlay));
        res.setBackground(getBackground(company, overlay));
        res.setLastVizit(getLastVizit(company));
        return res;
    }

    private Date getLastVizit(Card card) {
        return card.getAccount().getLastVizit();
    }

    public ParamCardResponse mapToParamResponse(Card card) {
        ParamCardResponse dto = modelMapper.map(mapToBrief(card), ParamCardResponse.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("city", card.getCity());
        if (card.getType().getType() == CardTypeEnum.PERSON) {
            Card company = companyService.getCompanyOf(card);
            Object res = null;
            if (company != null) {
                res = mapToBrief(company);
            }
            map.put("company", res);
        }
        map.put("cardName", card.getCardName());
        dto.setParams(map);
        return dto;
    }
}
