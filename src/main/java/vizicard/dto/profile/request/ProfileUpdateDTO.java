package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileUpdateDTO extends CreateUpdateBase {
    private Integer avatarId;
    private Integer companyId;

    private String password;
}
