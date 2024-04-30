package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeirdCardResponseDetailDTO {
    private String icon;
    private String type;
    private String value;
}
