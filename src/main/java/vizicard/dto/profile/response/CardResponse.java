package vizicard.dto.profile.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.BriefRelationResponseDTO;
import vizicard.dto.WeirdCardResponseDetailDTO;
import vizicard.dto.tab.TabResponseDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse extends BigCardResponse {
  private BriefCardResponse company;
  private BriefRelationResponseDTO relation;
  private String cardName;
  private List<TabResponseDTO> tabs;
  private List<WeirdCardResponseDetailDTO> details;
}
