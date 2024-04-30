package vizicard.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ContactGroupEnum;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactGroupResponse {
    private ContactGroupEnum type;
    private String writing;
    private List<ContactTypeResponse> contactTypes;
}
