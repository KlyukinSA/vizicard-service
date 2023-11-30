package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.*;
import vizicard.dto.profile.response.BriefCardResponse;
import vizicard.dto.profile.response.CardResponse;
import vizicard.dto.profile.response.CompanyResponse;
import vizicard.dto.profile.response.ParamCardResponse;
import vizicard.dto.tab.TabResponseDTO;
import vizicard.model.*;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.repository.ContactRepository;
import vizicard.repository.RelationRepository;
import vizicard.repository.TabRepository;
import vizicard.repository.TabTypeRepository;
import vizicard.service.*;
import vizicard.utils.ProfileProvider;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CardMapper {

    private final ShortnameService shortnameService;
    private final RelationRepository relationRepository;
    private final ContactRepository contactRepository;
    private final CloudFileService cloudFileService;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;
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
        res.setAvatarUrl(getAvatarUrl(card, overlay));
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
        res.setRelation(getPossibleRelation(card));
        res.setMainShortname(shortnameService.getMainShortname(card));
        res.setAvatarUrl(getAvatarUrl(card, overlay));
        res.setBackgroundUrl(getBackgroundUrl(card, overlay));
        res.setLastVizit(getLastVizit(card));
        res.setTabs(getTabs(card));
        res.setDetails(getWeirdDetails(card));
        return res;
    }

    private List<WeirdCardResponseDetailDTO> getWeirdDetails(Card card) {
        List<WeirdCardResponseDetailDTO> res = new ArrayList<>();
        if (card.getCity() != null) {
            res.add(new WeirdCardResponseDetailDTO("https://s3.timeweb.com/2cc1de15-bc1f377d-9e5a-448f-8a1d-f117b93916d2/img_place.svg",
                    "CITY", card.getCity()));
        }
        Card company = companyService.getCompanyOf(card);
        if (company != null) {
            res.add(new WeirdCardResponseDetailDTO("https://s3.timeweb.com/2cc1de15-bc1f377d-9e5a-448f-8a1d-f117b93916d2/img_work.svg",
                    "COMPANY", company.getName()));
        }
        return res;
    }

    private String getBackgroundUrl(Card card, Optional<Card> overlay) {
        return getCloudFileUrlByFileId(overlay.filter(c -> c.getBackgroundId() != null).orElse(card).getBackgroundId());
    }

    private String getCloudFileUrlByFileId(Integer id) {
        if (id != null) {
            CloudFile cloudFile = cloudFileService.findById(id);
            if (cloudFile == null || !cloudFile.isStatus()) {
                return null;
            }
            return cloudFile.getUrl();
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
                res.add(new TabResponseDTO(tab.getType().getWriting(), tab.getOrder(), tab.getType().getType()));
                usedTypes.add(tab.getType().getType());
            }
        }
        int i = tabs.stream().mapToInt(Tab::getOrder).max().orElse(0);
        finishTabType(usedTypes, TabTypeEnum.CONTACTS, isCurrentCard, !contactRepository.findAllByCardOwner(card).isEmpty(), res, i);
        i++;

        ProfileDetailStruct detailStruct = card.getDetailStruct();
        finishTabType(usedTypes, TabTypeEnum.RESUME, isCurrentCard, isResumeNotEmpty(detailStruct), res, i);
        i++;

        List<CloudFile> allMedia = albumService.getAllFiles(card, CloudFileType.MEDIA);
        finishTabType(usedTypes, TabTypeEnum.MEDIA, isCurrentCard, !allMedia.isEmpty(), res, i);
        i++;
        List<CloudFile> allFiles = albumService.getAllFiles(card, CloudFileType.FILE);
        finishTabType(usedTypes, TabTypeEnum.FILES, isCurrentCard, !allFiles.isEmpty(), res, i);
        return res;
    }

    private void finishTabType(HashSet<TabTypeEnum> usedTypes, TabTypeEnum type, boolean isCurrentCard, boolean cardHasOfThisType, List<TabResponseDTO> res, int i) {
        if (!usedTypes.contains(type) && (isCurrentCard || cardHasOfThisType)) {
            TabType type1 = tabTypeRepository.findByType(type);
            res.add(new TabResponseDTO(type1.getWriting(), i, type1.getType()));
        }
    }

    private static boolean isResumeNotEmpty(ProfileDetailStruct detailStruct) {
        return detailStruct != null && detailStruct.getEducation().size() +
                detailStruct.getExperience().size() + detailStruct.getSkills().size() > 0;
    }

    private String getAvatarUrl(Card card, Optional<Card> overlay) {
        return getCloudFileUrlByFileId(overlay.filter(c -> c.getAvatarId() != null).orElse(card).getAvatarId());
    }

    private BriefRelationResponseDTO getPossibleRelation(Card card) {
        Account user = profileProvider.getUserFromAuth();
        if (user == null) {
            return null;
        }
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
        res.setMainShortname(shortnameService.getMainShortname(company));
        res.setAvatarUrl(getAvatarUrl(company, overlay));
        res.setBackgroundUrl(getBackgroundUrl(company, overlay));
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
