package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.DoingType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoingDTO {
    private DoingType type;
    private String writing;
    private boolean main;
}
