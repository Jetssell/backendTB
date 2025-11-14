package pe.visionary.tb.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ⚠️ Plantilla de prueba unitaria de autenticación/JWT.
 */
@SpringBootTest
class AuthServiceTest {

    // @Autowired
    // private AuthService authService;

    @Test
    @Disabled("TODO: vincular con tu servicio real y habilitar")
    @DisplayName("Generar token JWT válido para credenciales correctas")
    void testGenerarTokenJWT() {
        // var token = authService.login("admin", "12345");
        // assertNotNull(token);
        // assertFalse(token.isBlank());
    }
}
