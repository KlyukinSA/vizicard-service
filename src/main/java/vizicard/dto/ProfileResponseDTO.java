package vizicard.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;

@Data
public class ProfileResponseDTO extends BriefProfileResponseDTO {
  private String description;
  private String city;
  private List<ContactResponse> contacts;

  private Date createAt;

  private BriefProfileResponseDTO company;

  private Date lastVizit;

  private ProfileDetailStructResponseDTO about;

  private BriefRelationResponseDTO relation;
}
