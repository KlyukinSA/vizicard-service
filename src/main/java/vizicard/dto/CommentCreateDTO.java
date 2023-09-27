package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentCreateDTO extends PublicationCreateDTO {
    private Float rating;
}
