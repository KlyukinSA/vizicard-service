package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.model.ShortnameType;

@Data
@NoArgsConstructor
public class ShortnameResponse {
    private Integer id;
    private String shortname;
    private ShortnameType type;

    private Integer accountId;
    private Integer cardId;
    private Integer referrerId;
}
