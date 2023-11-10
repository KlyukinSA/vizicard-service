package vizicard.dto.contact;

import lombok.*;
import vizicard.model.ContactEnum;

@Data
@NoArgsConstructor
public class FullContactResponse extends ContactResponse {
    private String description;
}
