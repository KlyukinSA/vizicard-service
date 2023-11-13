package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudFileDTO {
    private Integer id;
    private String url;
    private Integer albumId;
    private String description;
}
