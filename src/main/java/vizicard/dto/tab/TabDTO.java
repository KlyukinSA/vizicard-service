package vizicard.dto.tab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.TabTypeEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TabDTO {
//    private Integer id;
//    private Integer typeId;
    private TabTypeEnum type;
    private boolean hidden;
    private int order;
}
