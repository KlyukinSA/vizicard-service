package vizicard.utils;

import org.springframework.stereotype.Component;
import vizicard.model.Profile;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenClaimsFiller {
    public void fill(Map<String, Object> claims, Profile profile) {
        claims.put("type", profile.getType());
        claims.put("status", profile.isStatus());
        claims.put("albumId", profile.getAlbum().getId());
    }
}
