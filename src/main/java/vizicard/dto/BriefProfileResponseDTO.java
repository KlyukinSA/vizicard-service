package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BriefProfileResponseDTO {
    private Integer id;
    private String name;
    private String title;
    private CloudFileDTO avatar;
    private String mainShortname;
}
