package vizicard.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactListResponse {
    private List<FullContactResponse> full;
    private List<ContactResponse> brief;
}
