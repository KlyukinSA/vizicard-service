package vizicard.dto.publication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.BriefCardResponse;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationResponse {
//    private BriefCardResponse card;
    private String title;
    private String description;
    private Date date;
}
