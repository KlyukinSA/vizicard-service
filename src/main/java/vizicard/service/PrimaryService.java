package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;

@Service
@RequiredArgsConstructor
public class PrimaryService {

	private final RelationRepository relationRepository;

	public Profile getPrimary(Profile secondary) {
		Relation relation = relationRepository.findByTypeAndProfile(RelationType.SECONDARY, secondary);
		if (relation == null) {
			return null;
		}
		return relation.getOwner();
	}

	public Profile getPrimaryOrSelf(Profile profile) {
		Profile primary = getPrimary(profile);
		if (primary != null) {
			return primary;
		}
		return profile;
	}

}
