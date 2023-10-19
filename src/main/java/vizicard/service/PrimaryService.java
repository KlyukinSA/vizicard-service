package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.model.Card;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;

@Service
@RequiredArgsConstructor
public class PrimaryService {

	private final RelationRepository relationRepository;

//	public Card getPrimary(Card secondary) {
//		Relation relation = relationRepository.findByTypeAndCard(RelationType.SECONDARY, secondary);
//		if (relation == null) {
//			return null;
//		}
//		return relation.getOwner();
//	}

//	public Card getPrimaryOrSelf(Card card) {
//		Card primary = getPrimary(card);
//		if (primary != null) {
//			return primary;
//		}
//		return card;
//	}

}
