package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.ShortnameType;

@Data
@NoArgsConstructor
public class ShortnameDTO {
    private ShortnameType type;
    private String shortname;
}
