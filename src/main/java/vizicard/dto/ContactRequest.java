package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ContactEnum;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {
    private ContactEnum type;
    private String contact;
}
