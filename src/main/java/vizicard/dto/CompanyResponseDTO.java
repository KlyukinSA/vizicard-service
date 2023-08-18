package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.CloudFile;

@Data
@NoArgsConstructor
public class CompanyResponseDTO {
    private String name;
    private String title;
    private CloudFile avatar;
}
