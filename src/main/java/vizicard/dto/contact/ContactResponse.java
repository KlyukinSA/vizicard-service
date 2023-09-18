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
    private String contact;
    private String title;
    private String description;
    private Integer order;

    private String logoUrl;
}
