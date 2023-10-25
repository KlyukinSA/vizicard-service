package vizicard.dto.profile.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.CloudFileDTO;
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
