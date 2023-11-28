package vizicard.dto.profile.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.CardTypeDTO;
import vizicard.model.CardTypeEnum;

@Data
@NoArgsConstructor
public class BriefCardResponse {
    private Integer id;
    private String name;
    private String title;
    private String avatarUrl;
    private String mainShortname;
    private CardTypeEnum type;
    private boolean custom;
}
