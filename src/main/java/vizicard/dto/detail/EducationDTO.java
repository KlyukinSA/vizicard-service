package vizicard.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.detail.EducationLevel;
import vizicard.model.detail.EducationType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {
    private String institution;
    private String specialization;
    private Short graduationAt;
    private Integer typeId;
}
