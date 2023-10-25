package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.contact.ContactInListRequest;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateUpdateBase {
    private String name;
    private String title;
    private String description;
    private String city;
    private List<ContactInListRequest> contacts;
}
