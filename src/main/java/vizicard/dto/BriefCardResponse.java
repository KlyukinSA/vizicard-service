package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.CardType;

@Data
@NoArgsConstructor
public class BriefCardResponse {
    private Integer id;
    private String name;
    private String title;
    private CloudFileDTO avatar;
    private String mainShortname;
    private CardType type;
    private boolean custom;
}
