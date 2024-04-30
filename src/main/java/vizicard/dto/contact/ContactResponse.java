package vizicard.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ContactEnum;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {
    private Integer id;
    private ContactEnum type;
    private String value;
    private String title;
    private String logoUrl;
}
