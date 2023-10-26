package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vizicard.model.*;
import vizicard.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class CashDecreaseEngine {

    private final AccountRepository accountRepository;
    private final CompanyService companyService;

    @Scheduled(fixedRate = 1000*60*60)
    private void decreaseGlobalCash() {
        accountRepository.findAll().forEach(this::decrease);
    }

    private void decrease(Account account) {
        float cash = account.getCash();
        if (cash > 0) {
            cash -= getDecreasePrice(account);
            if (cash < 0) {
                cash = 0;
            }
            account.setCash(cash);
            accountRepository.save(account);
        }
    }

    private float getDecreasePrice(Account account) {
        float res = 1;
        Card company = companyService.getCompanyOf(account.getMainCard());
        if (company != null && company.isStatus()) {
            res += (float) (0.5 * company.getRelationsWhereCard().stream()
                    .filter(Relation::isStatus)
                    .map(Relation::getAccountOwner)
                    .filter(Account::isStatus)
                    .filter(owner -> owner.getType() == AccountType.EMPLOYEE)
                    .count());
        }
        return res;
    }

}
