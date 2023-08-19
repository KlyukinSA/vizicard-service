package vizicard.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationUpdateDTO {
    private String stage;
    private String institution;
    private String specialization;
    private Date graduationAt;
}
