package vizicard.dto.publication;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentResponse extends PublicationResponse {
    private Float rating;
}
