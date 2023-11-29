package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhotoCreateUpdateBase extends CreateUpdateBase {
    private String avatarUrl;
    private String backgroundUrl;
}
