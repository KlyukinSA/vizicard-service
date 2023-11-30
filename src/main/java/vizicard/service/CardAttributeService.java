package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.ShortnameRepository;
import vizicard.repository.TabRepository;
import vizicard.utils.ProfileProvider;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardAttributeService {

    private final ProfileProvider profileProvider; // TODO remove
    private final ShortnameRepository shortnameRepository;
    private final TabRepository tabRepository;

    public Card getCardByIdOrElseShortname(String idOrSn) {
        if (idOrSn.startsWith("id")) {
            Integer id = Integer.parseInt(idOrSn.substring(2));
            return profileProvider.getTarget(id);
        } else {
            Shortname shortname = shortnameRepository.findByShortname(idOrSn);
            Card card = shortname.getCard();
            if (card == null) {
                return shortname.getAccount().getMainCard();
            }
            return card;
        }
    }

    public void stopAccessToHiddenTab(TabTypeEnum tabType, Card card) { // TODO move to TabService
        Account user = profileProvider.getUserFromAuth();
        boolean isCurrentCard = user != null && user.getCurrentCard().getId().equals(card.getId()); // TODO same in CardMapper
        Optional<Tab> optionalTab = tabRepository.findByTypeTypeAndCardOwner(tabType, card);
        if (!isCurrentCard && optionalTab.isPresent() && optionalTab.get().isHidden()) { // TODO same check
            throw new CustomException("tab is hidden", HttpStatus.FORBIDDEN);
        }
    }

}
