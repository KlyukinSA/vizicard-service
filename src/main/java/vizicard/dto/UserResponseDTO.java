package vizicard.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vizicard.model.CloudFile;
import vizicard.model.ProfileType;

@Data
public class UserResponseDTO {

  @ApiModelProperty(position = 0)
  private Integer id;
  @ApiModelProperty(position = 2)
  private String name;
  @ApiModelProperty(position = 3)
  private String title;
  @ApiModelProperty(position = 4)
  private String description;
  @ApiModelProperty(position = 6)
  private String city;

  @ApiModelProperty(position = 7)
  private ContactDTO[] contacts;

  @ApiModelProperty(position = 8)
  private CloudFile avatar;
  @ApiModelProperty(position = 9)
  private CloudFile background;

  @ApiModelProperty(position = 10)
  private Date createAt;

  @ApiModelProperty(position = 11)
  private ProfileType profileType;
  @ApiModelProperty(position = 12)
  private CompanyResponseDTO company;

  @ApiModelProperty(position = 13)
  private Date lastVizit;

}
