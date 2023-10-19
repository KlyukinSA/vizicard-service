package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.AlbumRepository;
import vizicard.repository.CardRepository;
import vizicard.repository.ShortnameRepository;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final ShortnameRepository shortnameRepository;
    private final AlbumRepository albumRepository;

    private final ProfileProvider profileProvider;
    private final RelationValidator relationValidator;
    private final ActionService actionService;

    public Card create(Card card) {
        cardRepository.save(card);

        shortnameRepository.save(new Shortname(
                card, String.valueOf(UUID.randomUUID()), ShortnameType.MAIN));

        if (card.getType() == ProfileType.USER || card.getType() == ProfileType.COMPANY) {
            Album album = new Album(card);
            albumRepository.save(album);
            card.setAlbum(album);
        }
        return cardRepository.save(card);
    }

    public Card searchByShortname(String sn) {
        Shortname shortname = shortnameRepository.findByShortname(sn);
        Card card = shortname.getCard();
        if (card == null) {
            card = shortname.getOwner().getMainCard();
        }
        return search(card, shortname);
    }

    public Card searchById(Integer id) {
        return search(profileProvider.getTarget(id), null);
    }

    private Card search(Card card, Shortname shortname) {
        if (card.getType() == ProfileType.CUSTOM_USER || card.getType() == ProfileType.CUSTOM_COMPANY || card.getType() == ProfileType.GROUP) {
            relationValidator.stopNotOwnerOf(card);
        }
        actionService.addVisitAction(card, shortname);
        return card;
    }

    public void delete(Integer id) {
        Card card = cardRepository.findById(id).get();
        Account user = profileProvider.getUserFromAuth();
        if (user.getMainCard().getId().equals(card.getId())) {
            throw new CustomException("cant delete main card", HttpStatus.FORBIDDEN);
        } else if (user.getCurrentCard().getId().equals(card.getId())) {
            throw new CustomException("cant delete current card of account", HttpStatus.FORBIDDEN);
        } else {
            relationValidator.stopNotOwnerOf(card);

            Shortname shortname = shortnameRepository.findByCard(card);
            if (shortname != null) {
                shortname.setCard(user.getMainCard());
            }

            card.setStatus(false);
            cardRepository.save(card);
        }
    }

    public Card whoami() {
        return profileProvider.getUserFromAuth().getCurrentCard();
    }

    public List<Card> getAllMyCards() {
        return cardRepository.findAllByAccount(profileProvider.getUserFromAuth());
    }

    public Card createCustom(Card card) {
        Set<ProfileType> relationOrCompanyGroupProfileTypes = new HashSet<>(Arrays.asList(
                ProfileType.CUSTOM_USER, ProfileType.CUSTOM_COMPANY,
                ProfileType.COMPANY, ProfileType.GROUP));
        if (!relationOrCompanyGroupProfileTypes.contains(card.getType())) {
            throw new CustomException("cant create card with this type", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        card.setAccount(profileProvider.getUserFromAuth());
        return create(card);
    }

    public Card prepareToUpdate(Integer id) {
        Card target = profileProvider.getTarget(id);
        relationValidator.stopNotOwnerOf(target);
        return target;
    }

}