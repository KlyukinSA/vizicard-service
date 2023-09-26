package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.model.Profile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationOnPageResponse {
    private Integer ownerId;
    private String title;
    private String description;
    private Date date;
}
