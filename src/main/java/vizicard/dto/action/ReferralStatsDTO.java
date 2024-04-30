package vizicard.dto.action;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReferralStatsDTO {
	int ref1Count; // Количество рефералов первого
	int ref2Count; // и второго уровня
	float dayBenefit;
	float weekBenefit;
	float monthBenefit;
	float totalBenefit;
	int visitsCount; // количество уникальных визитов, где владелец токена - реферрер короткого имени визпт
}
