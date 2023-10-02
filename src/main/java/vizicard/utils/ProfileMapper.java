package vizicard.utils;

import com.amazonaws.services.apigateway.model.GatewayResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vizicard.dto.*;
import vizicard.dto.contact.ContactResponse;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.model.Contact;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.model.detail.Skill;
import vizicard.repository.RelationRepository;
import vizicard.service.CashService;
import vizicard.service.ShortnameService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final ShortnameService shortnameService;
    private final RelationRepository relationRepository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;
    private final CashService cashService;

    public BriefProfileResponseDTO mapToBrief(Profile profile) {
        BriefProfileResponseDTO res = modelMapper.map(profile, BriefProfileResponseDTO.class);
        res.setMainShortname(shortnameService.getMainShortname(profile));
        removeDeletedAvatar(res, profile);
        return res;
    }

    public ProfileResponseDTO mapToResponse(Profile profile) {
        ProfileResponseDTO res = modelMapper.map(profile, ProfileResponseDTO.class); // TODO map except company and contacts and about
        res.setContacts(getContactDTOs(profile));
        res.setResume(getResume(profile));
        res.setRelation(getPossibleRelation(profile));
        removeDeletedAvatar(res, profile);
        finishCompany(res, profile);
        return res;
    }

    private void finishCompany(ProfileResponseDTO res, Profile profile) {
        if (profile.getCompany() == null || !profile.getCompany().isStatus()) { // TODO function for same checks
            res.setCompany(null);
        } else if (!cashService.isPro(profile)) {
            BriefProfileResponseDTO dto = new BriefProfileResponseDTO();
            dto.setName(profile.getCompany().getName());
            res.setCompany(dto);
        } else {
            res.getCompany().setMainShortname(shortnameService.getMainShortname(profile.getCompany()));
        }
    }

    private void removeDeletedAvatar(BriefProfileResponseDTO dto, Profile profile) {
        if (profile.getAvatar() != null && !profile.getAvatar().isStatus()) {
            dto.setAvatar(null);
        }
    }

    private BriefRelationResponseDTO getPossibleRelation(Profile profile) {
        Profile user = profileProvider.getUserFromAuth();
        Relation relation = relationRepository.findByOwnerAndProfile(user, profile);
        if (relation == null) {
            relation = relationRepository.findByOwnerAndProfile(profile, user);
            if (relation == null) {
                return null;
            }
        }
        if (Objects.equals(relation.getProfile().getId(), relation.getOwner().getId())) {
            return null;
        }
        return modelMapper.map(relation, BriefRelationResponseDTO.class);
    }

    private List<ContactResponse> getContactDTOs(Profile profile) {
        return profile.getContacts().stream()
                .filter(Contact::isStatus)
                .map((val) -> new ContactResponse(
                        val.getId(),
                        val.getType().getType(),
                        val.getContact(),
                        val.getTitle(),
                        val.getDescription(),
                        val.getOrder(),
                        val.getType().getLogo().getUrl()))
                .collect(Collectors.toList());
    }

    private ProfileDetailStructResponseDTO getResume(Profile profile) {
        ProfileDetailStruct detailStruct = profile.getDetailStruct();
        if (detailStruct == null || !cashService.isPro(profile)) {
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
