package pe.gob.salud.tb.infrastructure.adapter;
import org.springframework.stereotype.Component;
import pe.gob.salud.tb.domain.model.User;
import pe.gob.salud.tb.domain.port.TokenIssuerPort;
import pe.gob.salud.tb.middleware.JwtService;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenIssuer implements TokenIssuerPort {
  private final JwtService jwt;
  public JwtTokenIssuer(JwtService jwt){ this.jwt = jwt; }
  @Override
  public String issueFor(User user) {
    Map<String,Object> claims = new HashMap<>();
    claims.put("sub", user.username());
    claims.put("uid", user.id());
    claims.put("role", user.roles());
    claims.put("scope", user.scope());
    return jwt.generate(claims);
  }
}
