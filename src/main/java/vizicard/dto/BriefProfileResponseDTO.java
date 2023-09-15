package vizicard.dto;

import com.amazonaws.services.sagemaker.model.ProblemType;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ProfileType;

@Data
@NoArgsConstructor
public class BriefProfileResponseDTO {
    private Integer id;
    private String name;
    private String title;
    private CloudFileDTO avatar;
    private String mainShortname;
    private ProfileType type;
}
