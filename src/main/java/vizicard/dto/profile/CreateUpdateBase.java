package vizicard.dto.profile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUpdateBase {
    private String name;
    private String title;
    private String description;
    private String city;
//    private List<ContactRequest> contacts;
}
