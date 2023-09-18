package vizicard.dto.profile;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ProfileType;

@Data
@NoArgsConstructor
public class ProfileCreateDTO extends CreateUpdateBase {
    private Integer avatarId;
    private Integer companyId;

    private ProfileType type;
}
