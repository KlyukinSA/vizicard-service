package vizicard.dto.action;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class GraphActionResponse {
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date date;
	private int vizits;
	private int coverage; // unique visitors
}
