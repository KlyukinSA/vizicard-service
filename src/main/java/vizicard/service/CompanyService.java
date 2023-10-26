package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.*;
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

    public Card createWorker(Account account, Card card) {
        Card company = getOfCurrentCard();

        account.setType(AccountType.EMPLOYEE);
        authService.signup(account, card, null, null);

        relationRepository.save(new Relation(account, card, company, RelationType.MEMBER));
        return card;
    }

    private void stopNotOwnerOf(Card company, Account account) {
        if (!Objects.equals(company.getAccount().getId(), account.getId())) {
            throw new CustomException("you are not account of company", HttpStatus.FORBIDDEN);
        }
    }

    public List<Card> getAllWorkers() {
        return relationRepository.findAllByCardAndAccountOwnerType(
                getOfCurrentCard(), AccountType.EMPLOYEE).stream()
                .filter(Relation::isStatus)
                .map(Relation::getAccountOwner)
                .map(Account::getCurrentCard)
                .filter(Card::isStatus)
                .collect(Collectors.toList());
    }

    public Card prepareToCreateOrUpdate(Card company) {
        Card possibleCompany = getOfCurrentCard();
        Account user = profileProvider.getUserFromAuth();
        if (possibleCompany != null) {
            stopNotOwnerOf(possibleCompany, user);
            return possibleCompany;
        }
        company.setType(CardType.COMPANY);
        company.setCustom(false);
        company.setAccount(user);
        cardService.create(company);
        relationRepository.save(new Relation(user, user.getCurrentCard(), company, RelationType.MEMBER));
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
        return relationRepository.findByCardOwnerAndCardTypeAndType(
                card,
                CardType.COMPANY,
                RelationType.MEMBER);
    }

    public void setFor(Card card, Card company) {
        Relation relation = getRelation(card);
        if (relation != null) {
            relation.setStatus(true);
            relation.setCard(company);
        } else {
            relation = new Relation(profileProvider.getUserFromAuth(), card, company, RelationType.MEMBER);
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
