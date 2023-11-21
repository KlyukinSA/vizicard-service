package vizicard.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ExperienceResponseDTO;
import vizicard.dto.detail.SkillResponseDTO;
import vizicard.model.detail.Education;
import vizicard.model.detail.Experience;
import vizicard.model.detail.Skill;

@Component
@RequiredArgsConstructor
public class DetailResponseMapper {

    private final ModelMapper modelMapper;

    public EducationResponseDTO mapToResponse(Education detail) {
        EducationResponseDTO res = modelMapper.map(detail, EducationResponseDTO.class);
        res.setId(detail.getIndividualId());
        return res;
    }

    public ExperienceResponseDTO mapToResponse(Experience experience) {
        ExperienceResponseDTO res = modelMapper.map(experience, ExperienceResponseDTO.class);
        res.setId(experience.getIndividualId());
        return res;
    }

    public SkillResponseDTO mapToResponse(Skill detail) {
        SkillResponseDTO res = modelMapper.map(detail, SkillResponseDTO.class);
        res.setId(detail.getIndividualId());
        return res;
    }

}
