package pe.visionary.tb.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ⚠️ Plantilla de prueba unitaria para calidad de datos.
 */
@SpringBootTest
class DataQualityServiceTest {

    // @Autowired
    // private DataQualityService dataQualityService;

    @Test
    @Disabled("TODO: vincular con tu servicio real y habilitar")
    @DisplayName("Obtener métricas de calidad de datos y lista de errores")
    void testObtenerFactoresRiesgo() {
        // var res = dataQualityService.getCalidad("2024-01-01", "2024-12-31");
        // assertNotNull(res);
        // assertTrue(res.getMissingValues().size() >= 0);
    }
}
