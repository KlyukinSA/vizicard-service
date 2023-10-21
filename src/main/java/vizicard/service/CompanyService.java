package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;
import vizicard.model.*;
import vizicard.repository.CardRepository;
import vizicard.repository.RelationRepository;
import vizicard.utils.ProfileProvider;
import vizicard.utils.Relator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final ProfileProvider profileProvider;
    private final CardRepository cardRepository;
    private final RelationRepository relationRepository;
    private final Relator relator;
    private final AuthService authService;

    public Card createWorker(Account account, Card card) {
        Card company = profileProvider.getUserFromAuth().getMainCard().getCompany();
        if (company == null) {
            throw new CustomException("you dont have a company in main card", HttpStatus.BAD_REQUEST);
        }
        card.setType(ProfileType.WORKER);
        authService.signup(account, card, null, null);

        relator.relate(account, card, company, RelationType.USUAL);
        card.setCompany(company);
        return cardRepository.save(card);
    }

    public List<Card> getAllWorkers() {
        return relationRepository.findAllByCardAndAccountOwnerMainCardType(
                profileProvider.getUserFromAuth().getCurrentCard().getCompany(), ProfileType.WORKER).stream()
                .filter(Relation::isStatus)
                .map(Relation::getAccountOwner)
                .map(Account::getCurrentCard)
                .filter(Card::isStatus)
                .collect(Collectors.toList());
    }

}
