package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ContactRequest {
    private ContactDTO[] contacts;
}
