package pe.gob.salud.tb.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pe.gob.salud.tb.domain.model.User;
import pe.gob.salud.tb.domain.port.TokenIssuerPort;
import pe.gob.salud.tb.middleware.JwtService;

import java.util.Map;

@Component
public class JwtTokenIssuer implements TokenIssuerPort {
  private final JwtService jwt;
  public JwtTokenIssuer(JwtService jwt){ this.jwt = jwt; }

  @Override
  public String issueFor(User user) {
    String uid = user.id();
    String role = (user.roles() == null || user.roles().isEmpty())
        ? "USER" : user.roles().get(0);
    // JWT mínimo: uid + role (sin scope)
    return jwt.issue(uid, Map.of("uid", uid, "role", role));
  }
}
