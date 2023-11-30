package vizicard.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponseDTO {
    List<EducationResponseDTO> educations;
    List<ExperienceResponseDTO> experiences;
    List<SkillResponseDTO> skills;
}
