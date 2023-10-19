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

    public void fillAdditional(Map<String, Object> claims, Account account) {
        claims.put("type", account.getCurrentCard().getType());
        claims.put("status", account.getCurrentCard().isStatus()); // TODO acc
        claims.put("pro", cashService.isPro(account));
    }

}
