package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.profile.response.ParamCardResponse;
import vizicard.model.RelationType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationResponseDTO {
    private ParamCardResponse card;
    private Date createAt;
    private RelationType type;
    private Integer accountOwnerId;
    private Integer cardOwnerId;
}
