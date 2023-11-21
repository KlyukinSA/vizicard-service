package vizicard.dto.profile.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.CloudFileDTO;
import vizicard.dto.contact.ContactListResponse;
import vizicard.dto.contact.FullContactResponse;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class BigCardResponse extends BriefCardResponse {
    private String description;
    private String city;

    private Date createAt;
    private Date lastVizit;
    private Integer albumId;

    private CloudFileDTO background;
}
