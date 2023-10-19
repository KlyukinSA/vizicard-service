package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vizicard.exception.CustomException;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.model.Relation;
import vizicard.model.RelationType;
import vizicard.repository.RelationRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RelationValidator { // TODO move to Service?

    private final RelationRepository relationRepository;
    private final ProfileProvider profileProvider;

    public void stopNotOwnerOf(Card target) {
        Account user = profileProvider.getUserFromAuth();
        if (Objects.equals(user.getId(), target.getAccount().getId())) {
            return;
        }

        Relation relation = relationRepository.findByOwnerAndCard(user, target);
        if (relation == null || !relation.isStatus() || relation.getType() != RelationType.OWNER) {
            throw new CustomException("You are not the owner of this card (id " + target.getId() + ")", HttpStatus.FORBIDDEN);
        }
    }

}
