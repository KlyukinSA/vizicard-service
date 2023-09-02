package vizicard.dto;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.model.ProfileType;

@Data
public class ProfileResponseDTO extends BriefProfileResponseDTO {
  private String description;
  private String city;
  private List<ContactDTO> contacts;

  private Date createAt;

  private BriefProfileResponseDTO company;

  private Date lastVizit;

  private ProfileDetailStructResponseDTO about;

  private BriefRelationResponseDTO relation;
}
