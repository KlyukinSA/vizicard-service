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

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RelationValidator { // TODO move to Service?

    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;

    public void stopNotOwnerOf(Profile target) {
        Profile user = profileProvider.getUserFromAuth();
        if (Objects.equals(user.getId(), target.getId())) {
            return;
        }

        Relation relation = relationRepository.findByOwnerAndProfile(user, target);
        if (relation == null || !relation.isStatus() || relation.getType() != RelationType.OWNER) {
            throw new CustomException("You are not the owner of this profile", HttpStatus.FORBIDDEN);
        }
    }

}
