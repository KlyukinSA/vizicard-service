package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeadGenDTO {
    private String name;
    private String title;
    private String companyName;
    private String description;
    private String phone;
    private String email;
    private String link;
}
