package vizicard.dto.action;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GraphActionResponse {
	private List<Integer> vizits;
	private List<Integer> saves;
	private List<Integer> clicks;
}
