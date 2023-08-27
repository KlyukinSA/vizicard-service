package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vizicard.exception.CustomException;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;

@Component
@RequiredArgsConstructor
public class RelationValidator {

    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;

    public void stopNotOwnerOf(Profile target) {
        Profile user = profileProvider.getUserFromAuth();
        Relation relation = relationRepository.findByOwnerAndProfile(user, target);
        if (relation == null || relation.getType() != RelationType.OWNER) {
            throw new CustomException("You are not the owner of this profile", HttpStatus.FORBIDDEN);
        }
    }

}
