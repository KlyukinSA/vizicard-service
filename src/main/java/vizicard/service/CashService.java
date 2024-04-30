package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class CashService {

	private final AccountRepository accountRepository;

	public boolean isPro(Account account) {
		return accountRepository.findById(account.getId()).get().getCash() > 0; // findById is for UserDetailsService
	}

	public void giveBonus(Account account, float amount, Card card) {
		account.setReferralBonus(account.getReferralBonus() + amount);
		accountRepository.save(account);
	}

}
