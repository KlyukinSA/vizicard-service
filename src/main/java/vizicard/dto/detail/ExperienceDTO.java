package vizicard.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO {
    private String company;
    private String position;
    private Date startAt;
    private Date finishAt;
    private String tasks;
}
