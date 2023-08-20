package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationResponseDTO {
    private ProfileResponseDTO profile;
    private Date createAt;
}
