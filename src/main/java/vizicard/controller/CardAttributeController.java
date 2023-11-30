package vizicard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vizicard.dto.CloudFileDTO;
import vizicard.dto.contact.FullContactResponse;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.exception.CustomException;
import vizicard.mapper.ContactMapper;
import vizicard.mapper.DetailResponseMapper;
import vizicard.model.*;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.model.detail.Skill;
import vizicard.repository.RelationRepository;
import vizicard.repository.ShortnameRepository;
import vizicard.repository.TabRepository;
import vizicard.service.AlbumService;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards/{id}")
@RequiredArgsConstructor
public class CardAttributeController {

    private final TabRepository tabRepository;
    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;
    private final AlbumService albumService;
    private final ShortnameRepository shortnameRepository;

    private final ContactMapper contactMapper;
    private final DetailResponseMapper detailResponseMapper;
    private final ModelMapper modelMapper;


    @GetMapping("contacts")
    public List<FullContactResponse> getContacts(@PathVariable String id) {
        Card card = getCardByIdOrElseShortname(id);
        List<Contact> list = (List<Contact>) getListThroughOverlay(card, Card::getContacts);
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.CONTACTS, list, card);
        return contactMapper.mapList(list);
    }

    private Card getCardByIdOrElseShortname(String idOrSn) {
        if (idOrSn.startsWith("id")) {
            Integer id = Integer.parseInt(idOrSn.substring(2));
            return profileProvider.getTarget(id);
        } else {
            Shortname shortname = shortnameRepository.findByShortname(idOrSn);
            Card card = shortname.getCard();
            if (card == null) {
                return shortname.getAccount().getMainCard();
            }
            return card;
        }
    }

    private List<? extends CardAttribute> getListThroughOverlay(Card card, Function<Card, List<? extends CardAttribute>> f) {
        List<? extends CardAttribute> list = f.apply(card);
        Relation relation = relationRepository.findByAccountOwnerAndCard(profileProvider.getUserFromAuth(), card);
        if (relation != null) {
            Card overlay = relation.getOverlay();
            if (overlay != null) {
                List<? extends CardAttribute> list1 = f.apply(overlay);
                if (list1 != null) {
                    list = list1;
                }
            }
        }
        return list;
    }

    @GetMapping("education")
    public List<EducationResponseDTO> getEducation(@PathVariable String id) {
        Card card = getCardByIdOrElseShortname(id);
        List<Education> list = card.getDetailStruct().getEducation();
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.RESUME, list, card);
        return list.stream()
                .filter(Education::isStatus)
                .map(detailResponseMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("experience")
    public List<ExperienceResponseDTO> getExperience(@PathVariable String id) {
        Card card = getCardByIdOrElseShortname(id);
        List<Experience> list = card.getDetailStruct().getExperience();
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.RESUME, list, card);
        return list.stream()
                .filter(Experience::isStatus)
                .map(detailResponseMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("skills")
    public List<SkillResponseDTO> getSkills(@PathVariable String id) {
        Card card = getCardByIdOrElseShortname(id);
        List<Skill> list = card.getDetailStruct().getSkills();
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.RESUME, list, card);
        return list.stream()
                .filter(Skill::isStatus)
                .map(detailResponseMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    private void stopAccessToHiddenOrEmptyTab(TabTypeEnum tabType, List<? extends CardAttribute> list, Card card) {
        Account user = profileProvider.getUserFromAuth();
        boolean isCurrentCard = user != null && user.getCurrentCard().getId().equals(card.getId()); // TODO same in CardMapper
        Optional<Tab> optionalTab = tabRepository.findByTypeTypeAndCardOwner(tabType, card);
        if ((optionalTab.isPresent() && optionalTab.get().isHidden()
                && !isCurrentCard) || (list != null && list.isEmpty())) {
            throw new CustomException("nothing here", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("resume")
    public ProfileDetailStructResponseDTO getAllResume(@PathVariable String id) {
        Card card = getCardByIdOrElseShortname(id);
        stopAccessToHiddenOrEmptyTab(TabTypeEnum.RESUME, null, card); // Any
        ProfileDetailStruct detailStruct = card.getDetailStruct();
        return new ProfileDetailStructResponseDTO(
                detailStruct.getEducation().stream()
                        .filter(CardAttribute::isStatus)
                        .map(detailResponseMapper::mapToResponse)
                        .collect(Collectors.toList()),
                detailStruct.getExperience().stream()
                        .filter(CardAttribute::isStatus)
                        .map(detailResponseMapper::mapToResponse)
                        .collect(Collectors.toList()),
                detailStruct.getSkills().stream()
                        .filter(CardAttribute::isStatus)
                        .map(detailResponseMapper::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/files")
    List<CloudFileDTO> getUsualFiles(@PathVariable String id) {
        return albumService.getAllFiles(getTargetCardAlbumId(id), CloudFileType.FILE).stream()
                .map((val) -> modelMapper.map(val, CloudFileDTO.class))
                .collect(Collectors.toList());
    }

    private Integer getTargetCardAlbumId(String id) {
        return getCardByIdOrElseShortname(id).getAlbum().getId();
    }

    @GetMapping("/media")
    List<CloudFileDTO> getMediaFiles(@PathVariable String id) {
        return albumService.getAllFiles(getTargetCardAlbumId(id), CloudFileType.MEDIA).stream()
                .map((val) -> modelMapper.map(val, CloudFileDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/files")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addUsualFile(@RequestPart MultipartFile file, @PathVariable String id) {
        return modelMapper.map(albumService.addFile(file, getTargetCardAlbumId(id), CloudFileType.FILE), CloudFileDTO.class);
    }

    @PostMapping("/media")
    @PreAuthorize("isAuthenticated()")
    CloudFileDTO addMedia(@RequestPart MultipartFile file, @PathVariable String id) {
        return modelMapper.map(albumService.addFile(file, getTargetCardAlbumId(id), CloudFileType.MEDIA), CloudFileDTO.class);
    }

}
