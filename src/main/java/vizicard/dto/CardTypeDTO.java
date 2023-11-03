package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.CardTypeEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardTypeDTO {
    private CardTypeEnum type;
    private String writing;
}
