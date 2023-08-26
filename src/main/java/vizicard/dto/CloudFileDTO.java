package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Profile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudFileDTO {
    private Integer id;
    private String url;
    private Integer ownerId;

}
