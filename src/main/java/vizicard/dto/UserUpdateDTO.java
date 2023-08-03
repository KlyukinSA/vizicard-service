package vizicard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
public class UserUpdateDTO {

    @ApiModelProperty(position = 0)
    private String name;
    @ApiModelProperty(position = 1)
    private String position;
    @ApiModelProperty(position = 2)
    private String description;
    @ApiModelProperty(position = 3)
    private String company;
    @ApiModelProperty(position = 4)
    private String city;

    @ApiModelProperty(position = 5)
    private ContactDTO[] contacts;

}
