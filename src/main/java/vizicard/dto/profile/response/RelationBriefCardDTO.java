package vizicard.dto.profile.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class RelationBriefCardDTO extends BriefCardResponse {
    private Map<String, Object> params;
}
