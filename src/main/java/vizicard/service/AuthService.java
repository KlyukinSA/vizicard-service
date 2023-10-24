package vizicard.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.*;
import vizicard.utils.ProfileProvider;
import vizicard.utils.RelationValidator;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;

    private final CardService cardService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final ProfileProvider profileProvider;
    private final RelationValidator relationValidator;

    private final RelationRepository relationRepository;
    private final ShortnameRepository shortnameRepository;
    private final CloudFileService cloudFileService;
    private final CardRepository cardRepository;

    public Account signin(String username, String password) {
        Account account = accountRepository.findByUsername(username);
        if (!account.isStatus()) {
            throw new CustomException("you dont exist", HttpStatus.FORBIDDEN);
        }
        try {
            String id = String.valueOf(account.getId());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(id, password));
        } catch (Exception e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return account;
    }

    private void refer(Card card, String sn, Integer referrerId) {
        Card referrer;
        if (referrerId != null) {
            referrer = profileProvider.getTarget(referrerId);
        } else if (sn != null) {
            Shortname shortname = shortnameRepository.findByShortname(sn);
            if (shortname == null) {
                return;
            }
            if (shortname.getOwner() != null) {
                referrer = shortname.getOwner().getMainCard(); // shortname should be bound to main card (use assignToMainCard())
            } else if (shortname.getReferrer() != null) {
                referrer = shortname.getReferrer();
                if (shortname.getType() == ShortnameType.DEVICE) {
                    shortname.setOwner(card.getAccount());
                    shortnameRepository.save(shortname);
                }
            } else {
                return;
            }
        } else {
            return;
        }
        relationRepository.save(new Relation(referrer.getAccount(), referrer, card, RelationType.REFERRER));
        relationRepository.save(new Relation(card.getAccount(), card, referrer, RelationType.REFERRAL));
    }

    public Account signinOrSignupWithGoogle(GoogleIdToken.Payload payload, String shortname, Integer referrerId) {
        String password = payload.getSubject(); // userId
        String username = payload.getEmail();
        Account account = accountRepository.findByUsername(username);
        if (account != null) {
            return signin(username, password); // TODO pass account or dont search here
        }
        account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        Card card = new Card();
        card.setName((String) payload.get("name"));

        signup(account, card, shortname, referrerId);

        CloudFile picture = cloudFileService.saveExternal((String) payload.get("picture"), card.getAlbum());
        card.setAvatarId(picture.getId());
        cardRepository.save(card);
        return account;
    }

    public Account signup(Account account, Card card, String shortname, Integer referrerId) {
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        card.setType(ProfileType.USER);
        cardService.create(card);

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        account.setCurrentCard(card);
        account.setMainCard(card);
        accountRepository.save(account);

        card.setAccount(account);
        cardRepository.save(card);

        refer(card, shortname, referrerId);
        return account;
    }

    public Account changeCard(Integer id) {
        Card card = profileProvider.getTarget(id);
        if (!card.isStatus()) {
            throw new CustomException("card deleted", HttpStatus.FORBIDDEN);
        }
        relationValidator.stopNotOwnerOf(card);
        Account user = profileProvider.getUserFromAuth();
        user.setCurrentCard(card);
        return accountRepository.save(user);
    }

    public void deleteMe() {
        Account user = profileProvider.getUserFromAuth();
        user.setStatus(false);
        accountRepository.save(user);
    }

    public void changePassword(Card card, String password) {
        card.getAccount().setPassword(passwordEncoder.encode(password));
        accountRepository.save(card.getAccount());
    }

}
