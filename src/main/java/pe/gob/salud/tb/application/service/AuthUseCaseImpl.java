package pe.gob.salud.tb.application.service;
import org.springframework.stereotype.Service;
import pe.gob.salud.tb.domain.model.User;
import pe.gob.salud.tb.domain.port.PasswordHasherPort;
import pe.gob.salud.tb.domain.port.TokenIssuerPort;
import pe.gob.salud.tb.domain.port.UserReaderPort;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthUseCaseImpl implements AuthUseCase {
  private final UserReaderPort users;
  private final PasswordHasherPort hasher;
  private final TokenIssuerPort tokens;

  public AuthUseCaseImpl(UserReaderPort users, PasswordHasherPort hasher, TokenIssuerPort tokens){
    this.users = users; this.hasher = hasher; this.tokens = tokens;
  }

  @Override
  public Map<String, Object> login(String username, String password) {
    User u = users.findByUsername(username);
    if(u==null) throw new RuntimeException("bad_credentials");
    if(!u.enabled()) throw new RuntimeException("user_disabled");
    String hash = users.passwordHashOf(username);
    if(hash==null || !hasher.matches(password, hash)) throw new RuntimeException("bad_credentials");
    String token = tokens.issueFor(u);
    Map<String,Object> resp = new HashMap<>();
    resp.put("token", token); resp.put("role", u.roles()); resp.put("scope", u.scope());
    return resp;
  }
}
