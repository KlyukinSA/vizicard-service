package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkerCreateDTO extends PhotoCreateUpdateBase {
    private String username;
    private String password;
}
