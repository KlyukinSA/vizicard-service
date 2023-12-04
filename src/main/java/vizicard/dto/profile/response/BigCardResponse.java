package vizicard.dto.profile.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.DoingDTO;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class BigCardResponse extends BriefCardResponse {
    private String description;
    private Date createAt;
    private Date lastVizit;
    private Integer albumId;
    private String backgroundUrl;
    private List<DoingDTO> doings;
}
