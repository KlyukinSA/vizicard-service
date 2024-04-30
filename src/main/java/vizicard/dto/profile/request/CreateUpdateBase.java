package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUpdateBase {
    private String name;
    private String title;
    private String description;
    private String city;
    private String cardName;
}
