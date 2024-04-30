package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.AccountRepository;
import vizicard.repository.CardTypeRepository;
import vizicard.repository.RelationRepository;
import vizicard.utils.ProfileProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final ProfileProvider profileProvider;
    private final RelationRepository relationRepository;
    private final AuthService authService;
    private final CardService cardService;
    private final CardTypeRepository cardTypeRepository;
    private final AccountRepository accountRepository;

    public Card createWorker(Account account, Card card) {
        Card company = getOfCurrentCard();

        account.setEmployer(company);
        authService.signup(account, card, null, null);

        relationRepository.save(new Relation(account, card, company, RelationType.EMPLOYEE));
        return card;
    }

    private void stopNotOwnerOf(Card company, Account account) {
        if (!Objects.equals(company.getAccount().getId(), account.getId())) {
            throw new CustomException("you are not account of company", HttpStatus.FORBIDDEN);
        }
    }

    public List<Card> getAllWorkers(Card company) {
        return accountRepository.findAllByEmployer(company).stream()
                .map(Account::getCurrentCard)
                .filter(Card::isStatus)
                .collect(Collectors.toList());
    }

    public List<Card> getAllWorkers() {
        return getAllWorkers(getOfCurrentCard());
    }

    public Card prepareToCreateOrUpdate(Card company) {
        Card possibleCompany = getOfCurrentCard();
        Account user = profileProvider.getUserFromAuth();
        if (possibleCompany != null) {
            stopNotOwnerOf(possibleCompany, user);
            return possibleCompany;
        }
        company.setType(cardTypeRepository.findByType(CardTypeEnum.COMPANY));
        company.setCustom(false);
        company.setAccount(user);
        cardService.create(company);
        relationRepository.save(new Relation(user, user.getCurrentCard(), company, RelationType.EMPLOYEE));
        return company;
    }

    public Card getOfCurrentCard() {
        return getCompanyOf(profileProvider.getUserFromAuth().getCurrentCard());
    }

    public Card getCompanyOf(Card card) {
        Relation relation = getRelation(card);
        if (relation != null) {
            return relation.getCard();
        }
        return null;
    }

    private Relation getRelation(Card card) {
        return relationRepository.findByCardOwnerAndCardTypeTypeAndType(
                card,
                CardTypeEnum.COMPANY,
                RelationType.EMPLOYEE);
    }

    public void setFor(Card card, Card company) {
        Relation relation = getRelation(card);
        if (relation != null) {
            relation.setStatus(true);
            relation.setCard(company);
        } else {
            relation = new Relation(profileProvider.getUserFromAuth(), card, company, RelationType.EMPLOYEE);
        }
        relationRepository.save(relation);
    }

    public void unsetFor(Card card) {
        Relation relation = getRelation(card);
        if (relation != null && relation.isStatus()) {
            relation.setStatus(false);
            relationRepository.save(relation);
        }
    }

}
