package pe.gob.salud.tb.application.service;

import org.springframework.stereotype.Service;
import pe.gob.salud.tb.domain.model.User;
import pe.gob.salud.tb.domain.port.PasswordHasherPort;
import pe.gob.salud.tb.domain.port.UserReaderPort;
import pe.gob.salud.tb.shared.error.ApiException;
import pe.gob.salud.tb.shared.error.ErrorCode;

import java.util.List;

@Service
public class AuthService {

  private final UserReaderPort users;       // puerto (no repo/entity)
  private final PasswordHasherPort hasher;  // puerto de hashing

  public AuthService(UserReaderPort users, PasswordHasherPort hasher) {
    this.users  = users;
    this.hasher = hasher;
  }

  /** Compacto para el controlador (uid no se devuelve en body, solo en JWT/cookie) */
  public record AuthInfo(String uid, String name, String role) {}

  /** Autentica y entrega uid/name/role, sin token (el controller emite el JWT y cookie). */
  public AuthInfo authenticate(String username, String rawPassword) {
    // 1) validar credenciales solo con el hash
    String hash = users.passwordHashOf(username);
    if (hash == null || !hasher.matches(rawPassword, hash)) {
      throw new ApiException(ErrorCode.AUTH_INVALID_CREDENTIALS, "credenciales_invalidas");
    }

    // 2) traer el usuario del dominio (no JPA)
    User u = users.findByUsername(username);
    if (u == null) {
      throw new ApiException(ErrorCode.AUTH_USER_NOT_FOUND, "usuario_no_existe");
    }
    if (!u.enabled()) {
      throw new ApiException(ErrorCode.AUTH_USER_DISABLED, "usuario_deshabilitado");
    }

    String uid = u.id();
    String name = (u.name() != null && !u.name().isBlank()) ? u.name() : u.username();

    List<String> roles = (u.roles() == null) ? List.of() : u.roles();
    String role = roles.isEmpty() ? "USER" : roles.get(0);

    return new AuthInfo(uid, name, role);
  }
}
