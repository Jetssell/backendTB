package pe.gob.salud.tb.middleware;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {
  @Value("${security.jwt.secret}") private String secret;
  @Value("${security.jwt.issuer:tb.vc}") private String issuer;
  @Value("${security.jwt.access-minutes:60}") private long accessMinutes;

  private SecretKey key() { return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }

  /** Emite un JWT con subject=uid y claims mínimos (uid, role). */
  public String issue(String uid, Map<String,Object> claims) {
    long expMs = accessMinutes * 60_000L;
    Instant now = Instant.now();
    return Jwts.builder()
        .issuer(issuer)
        .subject(uid)                         // <-- subject = uid
        .claims(claims)                       // <-- { uid, role }
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusMillis(expMs)))
        .signWith(key(), Jwts.SIG.HS384)
        .compact();
  }

  /** Compatibilidad si en algún lado aún llaman generate(claims). */
  public String generate(Map<String,Object> claims){
    Object s = claims.getOrDefault("sub", claims.get("uid"));
    return issue(s != null ? s.toString() : null, claims);
  }

  public Map<String,Object> parseClaims(String jwt){
    return Jwts.parser().verifyWith(key()).build()
        .parseSignedClaims(jwt).getPayload();
  }
}
