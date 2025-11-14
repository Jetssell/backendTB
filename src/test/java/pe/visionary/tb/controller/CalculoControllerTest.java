package pe.visionary.tb.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración a nivel de controlador usando MockMvc.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CalculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/calculos/tb/edad-sexo retorna agregados")
    void testApiEdadSexo() throws Exception {
        mockMvc.perform(get("/api/calculos/tb/edad-sexo")
                .param("desde", "2024-01-01")
                .param("hasta", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
                // .andExpect(jsonPath("$.confirmados").isNumber())
                // .andExpect(jsonPath("$.noConfirmados").isNumber());
    }
}
