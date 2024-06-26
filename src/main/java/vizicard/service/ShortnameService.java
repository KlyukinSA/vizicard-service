package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.model.Shortname;
import vizicard.model.ShortnameType;
import vizicard.repository.CardRepository;
import vizicard.repository.ShortnameRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShortnameService {

    private final ShortnameRepository shortnameRepository;
    private final ProfileProvider profileProvider;
    private final CardRepository cardRepository;

    public void stopUsed(String sn) {
        if (null != shortnameRepository.findByShortname(sn)) {
            throw new CustomException("shortname already in use", HttpStatus.FORBIDDEN);
        }
    }

    public String getMainShortname(Card card) {
        Shortname shortname = shortnameRepository.findByCardAndType(card, ShortnameType.MAIN);
        if (shortname == null) {
            return null;
        }
        return shortname.getShortname();
    }

    public Shortname assignToMainCard(Integer id) {
        Shortname shortname = shortnameRepository.findById(id).get();
        Account user = profileProvider.getUserFromAuth();
        shortname.setCard(user.getMainCard());
        shortname.setAccount(null);
        return shortnameRepository.save(shortname);
    }

    public Shortname assignToCardByIdOrMyAccount(Integer id, Integer cardId) {
        Shortname shortname = shortnameRepository.findById(id).get();
        if (cardId != null) {
            Card card = cardRepository.findById(cardId).get();
            shortname.setCard(card);
            shortname.setAccount(null);
        } else {
            shortname.setCard(null);
            shortname.setAccount(profileProvider.getUserFromAuth());
        }
        return shortnameRepository.save(shortname);
    }

    public List<Shortname> getAllMyDevices() {
        Account account = profileProvider.getUserFromAuth();
        return shortnameRepository.findAllByAccountOrCard(account, account.getCurrentCard()).stream()
                .filter(shortname -> shortname.getType() == ShortnameType.DEVICE)
                .collect(Collectors.toList());
    }

    public Shortname put(String sn, ShortnameType type) {
        Card card = profileProvider.getUserFromAuth().getCurrentCard();
        Shortname bySn = shortnameRepository.findByShortname(sn);
        Shortname res;
        if (bySn != null) {
            stopNotOwnerOf(bySn, card);
            res = bySn;
            res.setType(type);
        } else {
            res = new Shortname(card, sn, type);
        }
        if (type == ShortnameType.MAIN) {
            Shortname oldMain = shortnameRepository.findByCardAndType(card, ShortnameType.MAIN);
            if (oldMain != null) {
                oldMain.setType(ShortnameType.USUAL);
                shortnameRepository.save(oldMain);
            }
        }
        return shortnameRepository.save(res);
    }

    private void stopNotOwnerOf(Shortname shortname, Card card) {
        if (!Objects.equals(shortname.getCard().getId(), card.getId())) {
            throw new CustomException("you are not card of this existing sn", HttpStatus.FORBIDDEN);
        }
    }
}
