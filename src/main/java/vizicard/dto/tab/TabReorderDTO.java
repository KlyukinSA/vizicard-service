package vizicard.dto.tab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.TabTypeEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TabReorderDTO {
    private TabTypeEnum type;
    private Integer order;
}
