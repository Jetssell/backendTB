package pe.gob.salud.tb.domain.port;
public interface PasswordHasherPort {
  boolean matches(String raw, String hash);
  String hash(String raw);
}
