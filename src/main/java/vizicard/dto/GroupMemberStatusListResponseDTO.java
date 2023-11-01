package vizicard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vizicard.dto.profile.response.BriefCardResponse;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberStatusListResponseDTO {
    private Integer id;
    private String name;
    private List<BriefCardResponse> members;
}
