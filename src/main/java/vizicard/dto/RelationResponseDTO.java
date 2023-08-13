package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class RelationResponseDTO {
    private UserResponseDTO profile;
    private Instant createAt;
}
