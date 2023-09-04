package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.*;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.ShortnameType;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.model.detail.Skill;
import vizicard.repository.RelationRepository;
import vizicard.repository.ShortnameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final ShortnameRepository shortnameRepository;
    private final RelationRepository relationRepository;

    private final ModelMapper modelMapper;
    private final ProfileProvider profileProvider;

    public BriefProfileResponseDTO mapToBrief(Profile profile) {
        BriefProfileResponseDTO res = modelMapper.map(profile, BriefProfileResponseDTO.class);
        res.setMainShortname(getMainShortname(profile));
        return res;
    }

    private String getMainShortname(Profile profile) {
        return shortnameRepository.findByOwnerAndType(profile, ShortnameType.MAIN).getShortname();
    }

    public ProfileResponseDTO mapToResponse(Profile profile) {
        ProfileResponseDTO res = modelMapper.map(profile, ProfileResponseDTO.class); // TODO map except company and contacts and about
        if (profile.getCompany() == null || !profile.getCompany().isStatus()) { // TODO function for same checks
            res.setCompany(null);
        } else {
            res.getCompany().setMainShortname(getMainShortname(profile.getCompany()));
        }
        res.setContacts(getContactDTOs(profile));
        res.setAbout(getAbout(profile));
        res.setMainShortname(getMainShortname(profile));
        res.setRelation(getPossibleRelation(profile));
        return res;
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

    private List<ContactDTO> getContactDTOs(Profile profile) {
        return profile.getContacts().stream()
                .map((val) -> new ContactDTO(
                        val.getType().getType(),
                        val.getContact(),
                        val.getType().getLogo().getUrl())
                ).collect(Collectors.toList());
    }

    private ProfileDetailStructResponseDTO getAbout(Profile profile) {
        ProfileDetailStruct detailStruct = profile.getDetailStruct();
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
