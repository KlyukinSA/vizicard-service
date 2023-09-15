package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeadGenerationDTO {
    private String name;
    private String position;
    private String comment;
}
