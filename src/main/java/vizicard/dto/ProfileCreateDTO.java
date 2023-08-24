package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ProfileType;

@Data
@NoArgsConstructor
public class ProfileCreateDTO extends ProfileUpdateDTO {
    private ProfileType profileType;
}
