package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.Profile;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;

    private final ProfileProvider profileProvider;
    private final RelationValidator relationValidator;

    public void unrelate(Integer ownerId, Integer profileId) {
        Profile owner;
        if (ownerId == null) {
            owner = profileProvider.getUserFromAuth();
        } else {
            owner = profileProvider.getTarget(ownerId);
            relationValidator.stopNotOwnerOf(owner);
        }
        Profile target = profileProvider.getTarget(profileId);

        Relation relation = relationRepository.findByOwnerAndProfile(owner, target);
        if (relation == null || !relation.isStatus()) {
            throw new CustomException("No such relation", HttpStatus.CONFLICT);
        }
        relation.setStatus(false);
        relationRepository.save(relation);
    }

}
