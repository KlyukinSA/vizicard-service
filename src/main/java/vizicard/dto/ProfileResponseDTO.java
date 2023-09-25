package vizicard.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vizicard.dto.contact.ContactResponse;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;

@Data
public class ProfileResponseDTO extends BriefProfileResponseDTO {
  private String description;
  private String city;
  private List<ContactResponse> contacts;

  private Date createAt;

  private BriefProfileResponseDTO company;

  private Date lastVizit;

  private ProfileDetailStructResponseDTO resume;

  private BriefRelationResponseDTO relation;

  private Integer albumId;
}
