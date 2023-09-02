package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.BriefResponseDTO;
import vizicard.dto.ContactDTO;
import vizicard.dto.ProfileResponseDTO;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.model.Profile;
import vizicard.model.ShortnameType;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.ProfileDetailStruct;
import vizicard.model.detail.Skill;
import vizicard.repository.ShortnameRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    private final ShortnameRepository shortnameRepository;

    private final ModelMapper modelMapper;

    public BriefResponseDTO mapToBrief(Profile profile) {
        BriefResponseDTO res = modelMapper.map(profile, BriefResponseDTO.class);
        res.setMainShortname(getMainShortname(profile));
        return res;
    }

    private String getMainShortname(Profile profile) {
        return shortnameRepository.findByOwnerAndType(profile, ShortnameType.MAIN).getShortname();
    }

    public ProfileResponseDTO mapToResponse(Profile profile) {
        ProfileResponseDTO res = modelMapper.map(profile, ProfileResponseDTO.class); // TODO map except company and contacts and about
        if (profile.getCompany() == null || !profile.getCompany().isStatus()) { // TODO same checks
            res.setCompany(null);
        } else {
            res.getCompany().setMainShortname(getMainShortname(profile.getCompany()));
        }
        res.setContacts(getContactDTOs(profile));
        res.setAbout(getAbout(profile));
        res.setMainShortname(getMainShortname(profile));
        return res;
    }

    private List<ContactDTO> getContactDTOs(Profile profile) {
        if (profile.getContacts() == null) {
            return new ArrayList<>();
        }
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
                        .map(Skill::getSkill)
                        .collect(Collectors.toList())
        );
    }

}
