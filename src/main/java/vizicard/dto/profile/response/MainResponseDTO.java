package vizicard.dto.profile.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.WeirdCardResponseDetailDTO;

import java.util.List;

@Data
@NoArgsConstructor
public class MainResponseDTO {
    private String name;
    private String title;
    private String description;
    private List<WeirdCardResponseDetailDTO> details;
}
