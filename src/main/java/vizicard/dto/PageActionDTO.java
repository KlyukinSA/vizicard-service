package vizicard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import vizicard.model.ActionType;

import java.time.Instant;

@Data
@AllArgsConstructor
public class PageActionDTO {

    @ApiModelProperty(position = 0)
    private Integer id;
    @ApiModelProperty(position = 1)
    private Integer actorId;
    @ApiModelProperty(position = 2)
    private Instant createAt;
    @ApiModelProperty(position = 3)
    private ActionType type;

}
