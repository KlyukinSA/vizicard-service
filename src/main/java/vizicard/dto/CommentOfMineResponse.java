package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.publication.CommentResponse;

@Data
@NoArgsConstructor
public class CommentOfMineResponse extends CommentResponse {
    private boolean moderated;
}
