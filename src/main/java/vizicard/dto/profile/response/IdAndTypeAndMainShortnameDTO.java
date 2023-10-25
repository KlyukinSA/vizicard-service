package vizicard.dto.profile.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import vizicard.model.CardType;

@Data
@AllArgsConstructor
public class IdAndTypeAndMainShortnameDTO {
    private Integer id;
    private CardType type;
    private String mainShortname;
}
