package vizicard.dto.publication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.BriefProfileResponseDTO;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationResponse {
    private BriefProfileResponseDTO profile;
    private String title;
    private String description;
    private Date date;
}
