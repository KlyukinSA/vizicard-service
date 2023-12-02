package vizicard.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ContactEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactTypeResponse {
    private Integer id;
    private ContactEnum type;
    private String logoUrl;
    private String writing;
}
