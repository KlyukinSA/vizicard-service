package vizicard.dto.profile.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeadGenDTO extends CreateUpdateBase {
    private String companyName;
}