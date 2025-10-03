package pe.gob.salud.tb.domain.port;
import pe.gob.salud.tb.domain.model.User;
public interface UserReaderPort {
  /** Devuelve el usuario y su set de roles + scope, o null si no existe */
  User findByUsername(String username);
  /** Devuelve el hash de password del usuario (para no filtrar hash fuera del puerto) */
  String passwordHashOf(String username);
}
