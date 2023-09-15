package vizicard.dto.profile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeadGenDTO extends CreateUpdateBase {
    private String companyName;
}
