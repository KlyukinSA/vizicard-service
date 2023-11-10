package vizicard.dto.profile.response;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vizicard.dto.BriefRelationResponseDTO;
import vizicard.dto.TabTypeDTO;
import vizicard.dto.contact.ContactListResponse;
import vizicard.dto.contact.ContactResponse;
import vizicard.dto.detail.ProfileDetailStructResponseDTO;
import vizicard.dto.profile.response.BriefCardResponse;

@Data
public class CardResponse extends BriefCardResponse {
  private String description;
  private String city;
  private ContactListResponse contacts;

  private Date createAt;

  private BriefCardResponse company;

  private Date lastVizit;

  private ProfileDetailStructResponseDTO resume;

  private BriefRelationResponseDTO relation;

  private Integer albumId;

  private String cardName;
  private List<TabTypeDTO> tabs;
}
