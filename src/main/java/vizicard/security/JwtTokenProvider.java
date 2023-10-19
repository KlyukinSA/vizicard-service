package vizicard.security;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import vizicard.exception.CustomException;
import vizicard.model.Account;
import vizicard.model.Card;
import vizicard.utils.TokenClaimsFiller;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  /**
   * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here. Ideally, in a
   * microservices environment, this key would be kept on a config-server.
   */
  @Value("${security.jwt.token.secret-key:secret-key}")
  private String secretKey;

  private final MyUserDetails myUserDetails;
  private final TokenClaimsFiller tokenClaimsFiller;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createToken(Account account) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("accountId", String.valueOf(account.getId())); // TODO integer
    claims.put("cardId", String.valueOf(account.getCurrentCard().getId())); //
    tokenClaimsFiller.fillAdditional(claims, account);
    return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = myUserDetails.loadUserByUsername(getId(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private String getId(String token) {
    return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("accountId");
  }

  public String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      throw new CustomException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
