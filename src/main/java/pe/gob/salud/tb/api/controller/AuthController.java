package pe.gob.salud.tb.api.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.gob.salud.tb.api.dto.auth.LoginRequest;
import pe.gob.salud.tb.api.dto.auth.LoginResponse;
import pe.gob.salud.tb.application.service.AuthService;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final long accessMinutes;
  private final boolean cookieSecure;
  private final String sameSite;

  public AuthController(
      AuthService authService,
      @Value("${security.jwt.access-minutes:60}") long accessMinutes,
      @Value("${app.cookie.secure:false}") boolean cookieSecure,
      @Value("${app.cookie.same-site:Strict}") String sameSite
  ) {
    this.authService = authService;
    this.accessMinutes = accessMinutes;
    this.cookieSecure = cookieSecure;
    this.sameSite = sameSite;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
    // 1) autenticar (retorna DTO con token y lo que quieras exponer a la UI)
    LoginResponse body = authService.login(req.username(), req.password());

    // 2) enviar el token también como Cookie HttpOnly
    ResponseCookie cookie = ResponseCookie.from("tb_token", body.token())
        .httpOnly(true)
        .secure(cookieSecure)                       // en PROD -> true (HTTPS)
        .sameSite(sameSite)                         // "Strict" o "Lax" según tu flujo
        .path("/")
        .maxAge(Duration.ofMinutes(accessMinutes))  // igual al access-minutes del JWT
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(body);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    // borra/expira la cookie en el cliente
    ResponseCookie cookie = ResponseCookie.from("tb_token", "")
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite(sameSite)
        .path("/")
        .maxAge(0)
        .build();

    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .build();
  }
}
