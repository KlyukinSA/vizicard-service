package vizicard.dto.profile.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.CardTypeDTO;
import vizicard.dto.CloudFileDTO;

@Data
@NoArgsConstructor
public class BriefCardResponse {
    private Integer id;
    private String name;
    private String title;
    private CloudFileDTO avatar;
    private String mainShortname;
    private CardTypeDTO type;
    private boolean custom;
}
