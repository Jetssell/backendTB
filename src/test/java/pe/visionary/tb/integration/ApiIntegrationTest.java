package pe.visionary.tb.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Smoke tests de integración (API principal).
 * Útiles para evidencias de ejecución temprana local.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Auth: /api/auth/login responde 200/401 según credenciales (depende de tu implementación)")
    void testAuthLogin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"admin\", \"password\":\"12345\"}"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Catálogos: /api/catalogos/diris responde 200 OK")
    void testCatalogosDiris() throws Exception {
        mockMvc.perform(get("/api/catalogos/diris"))
                .andExpect(status().is2xxSuccessful());
    }
}
