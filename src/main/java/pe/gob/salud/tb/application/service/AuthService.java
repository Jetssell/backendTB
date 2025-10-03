package pe.gob.salud.tb.application.service;

import org.springframework.stereotype.Service;
import pe.gob.salud.tb.api.dto.auth.LoginResponse;
import pe.gob.salud.tb.domain.model.User;
import pe.gob.salud.tb.domain.port.PasswordHasherPort;
import pe.gob.salud.tb.domain.port.UserReaderPort;
import pe.gob.salud.tb.middleware.JwtService;
import pe.gob.salud.tb.shared.error.ApiException;
import pe.gob.salud.tb.shared.error.ErrorCode;

import java.util.List;
import java.util.Map;

@Service
public class AuthService {

  private final UserReaderPort users;       // puerto (no repo/entity)
  private final PasswordHasherPort hasher;  // puerto de hashing
  private final JwtService jwt;

  public AuthService(UserReaderPort users, PasswordHasherPort hasher, JwtService jwt) {
    this.users  = users;
    this.hasher = hasher;
    this.jwt    = jwt;
  }

  public LoginResponse login(String username, String rawPassword) {
    // 1) validar credenciales sólo con el hash (no toco entidades)
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
    // si ya añadiste name() al modelo, usa: String name = u.name();
    String name = u.username(); // fallback
    List<String> roles = (u.roles() == null) ? List.of() : u.roles();
    String role = roles.isEmpty() ? "USER" : roles.get(0);

    // JWT mínimo
    String token = jwt.issue(uid, Map.of("uid", uid, "role", role));

    return new LoginResponse(uid, name, role, token);
  }
}
