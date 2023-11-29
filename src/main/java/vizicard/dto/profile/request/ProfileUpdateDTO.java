package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileUpdateDTO extends PhotoCreateUpdateBase {
    private String companyName;

    private String password;
}
