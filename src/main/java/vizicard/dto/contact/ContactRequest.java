package vizicard.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ContactEnum;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {
    private String contact;
    private String title;
    private String description;
}
