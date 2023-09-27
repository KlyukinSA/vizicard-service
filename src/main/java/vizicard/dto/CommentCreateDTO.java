package vizicard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentCreateDTO extends PublicationCreateDTO {
    @NotNull
    private Float rating;
}
