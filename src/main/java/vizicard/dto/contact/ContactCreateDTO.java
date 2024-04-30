package vizicard.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ContactEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactCreateDTO extends ContactRequest {
    private ContactEnum type;
}
