package vizicard.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vizicard.model.Profile;
import vizicard.service.CashService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenClaimsFiller {

    private final CashService cashService;

    public void fillAdditional(Map<String, Object> claims, Profile profile) {
        claims.put("type", profile.getType());
        claims.put("status", profile.isStatus());
        claims.put("pro", cashService.isPro(profile));
    }

}
