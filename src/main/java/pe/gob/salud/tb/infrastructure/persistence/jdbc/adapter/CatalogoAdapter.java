package pe.gob.salud.tb.infrastructure.persistence.jdbc.adapter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.salud.tb.domain.port.CatalogoPort;

import java.util.List;

@Repository
public class CatalogoAdapter implements CatalogoPort {

    private final JdbcTemplate jdbcTemplate;

    public CatalogoAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> listarAmbitos() {
        return jdbcTemplate.query("SELECT DISTINCT diris FROM geo.geo_distritos WHERE diris IS NOT NULL",
                (rs, rowNum) -> rs.getString(1));
    }
}
