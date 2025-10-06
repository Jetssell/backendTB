package pe.gob.salud.tb.api.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.gob.salud.tb.api.dto.auth.LoginRequest;
import pe.gob.salud.tb.api.dto.auth.LoginResponse;
import pe.gob.salud.tb.application.service.AuthService;
import pe.gob.salud.tb.domain.port.UserReaderPort;
import pe.gob.salud.tb.middleware.JwtService;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final JwtService jwt;
  private final UserReaderPort users;

  @Value("${app.cookie.secure:false}")        boolean cookieSecure;
  @Value("${app.cookie.same-site:Lax}")       String  cookieSameSite; // None | Lax | Strict
  @Value("${security.jwt.access-minutes:60}") long    accessMinutes;

  public AuthController(AuthService authService, JwtService jwt, UserReaderPort users) {
    this.authService = authService;
    this.jwt = jwt;
    this.users = users;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req){
    // Autentica y obtiene uid/name/role (uid NO va al body)
    AuthService.AuthInfo info = authService.authenticate(req.username(), req.password());

    // Emitir JWT mínimo con uid + role y ponerlo en la cookie HttpOnly
    String token = jwt.issue(info.uid(), Map.of(
        "uid",  info.uid(),
        "role", info.role()
    ));

    ResponseCookie tokenCookie = ResponseCookie.from("tb_token", token)
        .httpOnly(true)
        .secure(cookieSecure)          // En HTTPS => true; en http local => false
        .sameSite(cookieSameSite)      // Para cross-site real: None (con Secure=true)
        .path("/")
        .maxAge(Duration.ofMinutes(accessMinutes))
        .build();

    // Body SOLO name + role
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
        .body(new LoginResponse(info.name(), info.role()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(){
    ResponseCookie tokenCookie = ResponseCookie.from("tb_token","")
        .httpOnly(true).secure(cookieSecure).sameSite(cookieSameSite)
        .path("/").maxAge(0).build();
    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
        .build();
  }

  /** Rehidrata sesión al refrescar: responde como el login (name, role). */
  @GetMapping("/me")
  public ResponseEntity<LoginResponse> me(Authentication auth) {
    if (auth == null) return ResponseEntity.status(401).build();

    String uid = (String) auth.getPrincipal(); // el filtro puso el uid como principal
    var u = users.findById(uid);
    if (u == null || !u.enabled()) return ResponseEntity.status(401).build();

    String name = (u.name()!=null && !u.name().isBlank()) ? u.name() : u.username();
    String role = auth.getAuthorities().isEmpty()
        ? "USER"
        : auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_","");

    return ResponseEntity.ok(new LoginResponse(name, role));
  }
}
