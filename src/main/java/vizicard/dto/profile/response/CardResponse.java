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
public class CardResponse extends BigCardResponse {
  private BriefCardResponse company;
  private ProfileDetailStructResponseDTO resume;
  private BriefRelationResponseDTO relation;

  private String cardName;
  private List<TabTypeDTO> tabs;
}
