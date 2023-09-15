package vizicard.dto.profile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.ContactRequest;
import vizicard.model.ProfileType;

@Data
@NoArgsConstructor
public class ProfileUpdateDTO extends CreateUpdateBase {
    private Integer avatarId;
    private Integer companyId;

    private String password;
}
