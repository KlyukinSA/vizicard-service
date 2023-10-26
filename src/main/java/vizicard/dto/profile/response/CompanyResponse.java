package vizicard.dto.profile.response;

import lombok.Data;
import vizicard.dto.contact.ContactResponse;

import java.util.Date;
import java.util.List;

@Data
public class CompanyResponse extends BriefCardResponse {
    private String description;
    private String city;
    private List<ContactResponse> contacts;

    private Date createAt;
    private Date lastVizit;
    private Integer albumId;
}
