package vizicard.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vizicard.model.AppUserRole;
import vizicard.model.CloudFile;

@Data
public class UserResponseDTO {

  @ApiModelProperty(position = 0)
  private Integer id;
  @ApiModelProperty(position = 2)
  private String name;
  @ApiModelProperty(position = 3)
  private String position;
  @ApiModelProperty(position = 4)
  private String description;
  @ApiModelProperty(position = 5)
  private String company;
  @ApiModelProperty(position = 6)
  private String city;

  @ApiModelProperty(position = 7)
  private ContactDTO[] contacts;

  @ApiModelProperty(position = 8)
  private CloudFile avatar;
  @ApiModelProperty(position = 9)
  private CloudFile background;

}
