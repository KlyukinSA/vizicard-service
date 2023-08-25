package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForInstant;
import org.springframework.stereotype.Service;
import vizicard.model.Action;
import vizicard.model.ActionType;
import vizicard.model.Profile;
import vizicard.repository.ActionRepository;
import vizicard.utils.ProfileProvider;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    private final ProfileProvider profileProvider;

    public void vizit(Profile page) {
        Profile actor = profileProvider.getUserFromAuth();
        if (actor != null && Objects.equals(actor.getId(), page.getId())) {
            return;
        }
        actionRepository.save(new Action(actor, page, ActionType.VIZIT));
    }

}
