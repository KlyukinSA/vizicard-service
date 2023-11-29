package vizicard.dto.profile.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class BigCardResponse extends BriefCardResponse {
    private String description;

    private Date createAt;
    private Date lastVizit;
    private Integer albumId;

    private String backgroundUrl;
}
