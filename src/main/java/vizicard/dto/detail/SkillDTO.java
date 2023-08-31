package vizicard.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    private List<String> add;
    private List<Integer> delete;
}
