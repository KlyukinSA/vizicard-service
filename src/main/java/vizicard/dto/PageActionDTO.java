package vizicard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import vizicard.model.ActionType;

import java.time.Instant;

@Data
@AllArgsConstructor
public class PageActionDTO {

    private Integer vizits;
    private Integer saves;
    private Integer clicks;

}
