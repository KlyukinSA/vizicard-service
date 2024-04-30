package vizicard.dto.profile.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.CardTypeDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdAndTypeAndMainShortnameDTO {
    private Integer id;
    private CardTypeDTO type;
    private String mainShortname;
}
