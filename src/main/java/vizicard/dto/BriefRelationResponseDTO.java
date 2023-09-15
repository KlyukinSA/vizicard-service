package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.RelationType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BriefRelationResponseDTO {
    private Integer ownerId;
    private Integer profileId;
    private Date createAt;
    private RelationType type;
}
