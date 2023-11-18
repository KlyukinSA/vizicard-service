package vizicard.dto.tab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TabResponseDTO {
    private String typeWriting;
    private int order;
}
