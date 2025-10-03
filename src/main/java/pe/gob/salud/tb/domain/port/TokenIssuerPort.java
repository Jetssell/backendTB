package pe.gob.salud.tb.domain.port;
import pe.gob.salud.tb.domain.model.User;
public interface TokenIssuerPort {
  String issueFor(User user);
}
