package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.model.Account;
import vizicard.service.CashService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenClaimsFiller {

    private final CashService cashService;

    public void fill(Map<String, Object> claims, Account account) {
        claims.put("accountId", String.valueOf(account.getId())); // TODO integer
        claims.put("cardId", String.valueOf(account.getCurrentCard().getId())); //
        claims.put("type", account.getEmployer() == null ? "USUAL" : "EMPLOYEE");
        claims.put("status", account.getCurrentCard().isStatus()); // TODO acc
        claims.put("pro", cashService.isPro(account));
    }

}
