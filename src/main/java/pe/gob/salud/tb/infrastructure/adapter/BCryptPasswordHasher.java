package pe.gob.salud.tb.infrastructure.adapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.gob.salud.tb.domain.port.PasswordHasherPort;

@Component
public class BCryptPasswordHasher implements PasswordHasherPort {
  private final PasswordEncoder encoder;
  public BCryptPasswordHasher(PasswordEncoder encoder){ this.encoder = encoder; }
  @Override public boolean matches(String raw, String hash){ return encoder.matches(raw, hash); }
  @Override public String hash(String raw){ return encoder.encode(raw); }
}
