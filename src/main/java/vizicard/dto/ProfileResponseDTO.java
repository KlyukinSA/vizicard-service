package vizicard.dto;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vizicard.dto.detail.EducationResponseDTO;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.model.CloudFile;
import vizicard.model.ProfileType;

@Data
public class ProfileResponseDTO {

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
  private List<ContactDTO> contacts;

  @ApiModelProperty(position = 8)
  private CloudFile avatar;

  @ApiModelProperty(position = 10)
  private Date createAt;

  @ApiModelProperty(position = 11)
  private ProfileType type;
  @ApiModelProperty(position = 13)
  private BriefResponseDTO company;

  @ApiModelProperty(position = 14)
  private Date lastVizit;

  @ApiModelProperty(position = 15)
  private ProfileDetailStructResponseDTO about;

  @ApiModelProperty(position = 12)
  private Integer ownerId;

}
