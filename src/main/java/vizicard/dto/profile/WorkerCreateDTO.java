package vizicard.dto.profile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkerCreateDTO extends CreateUpdateBase {
    private Integer avatarId;

    private String username;
    private String password;
}
