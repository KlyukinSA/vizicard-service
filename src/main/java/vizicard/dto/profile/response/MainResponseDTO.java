package vizicard.dto.profile.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MainResponseDTO {
    private String avatarUrl;
    private String backgroundUrl;
    private String name;
    private String city;
    private String companyName;
    private String title;
    private String description;
}
