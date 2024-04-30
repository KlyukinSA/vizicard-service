package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.CardTypeEnum;

@Data
@NoArgsConstructor
public class ProfileCreateDTO extends PhotoCreateUpdateBase {
    private CardTypeEnum type;
}
