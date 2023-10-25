package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.CardType;

@Data
@NoArgsConstructor
public class ProfileCreateDTO extends CreateUpdateBase {
    private Integer avatarId;
    private Integer companyId;

    private CardType type;
}
