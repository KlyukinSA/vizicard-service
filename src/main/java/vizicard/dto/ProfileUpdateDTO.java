package vizicard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ProfileType;

@Data
@NoArgsConstructor
public class ProfileUpdateDTO {

    @ApiModelProperty(position = 1)
    private String name;
    @ApiModelProperty(position = 2)
    private String title;
    @ApiModelProperty(position = 3)
    private String description;
    @ApiModelProperty(position = 4)
    private String city;
    @ApiModelProperty(position = 5)
    private ContactRequest[] contacts;

    @ApiModelProperty(position = 6)
    private Integer avatarId;

    @ApiModelProperty(position = 7)
    private Integer companyId;

    private String password;

}
