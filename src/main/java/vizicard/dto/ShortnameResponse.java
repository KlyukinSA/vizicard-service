package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ShortnameType;

@Data
@NoArgsConstructor
public class ShortnameResponse {
    private String shortname;
    private ShortnameType type;
    private Integer ownerId;
    private Integer referrerId;
}
