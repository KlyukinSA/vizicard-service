package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.TabType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TabTypeDTO {
    private TabType type;
    private String writing;
    private Integer id;
}
