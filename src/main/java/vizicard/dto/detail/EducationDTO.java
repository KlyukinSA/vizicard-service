package vizicard.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {
    private String stage;
    private String institution;
    private String specialization;
    private Date graduationAt;
}