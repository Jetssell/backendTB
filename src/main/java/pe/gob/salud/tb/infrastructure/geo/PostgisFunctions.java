package pe.gob.salud.tb.infrastructure.geo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostgisFunctions {
    private final JdbcTemplate jdbc;

    public PostgisFunctions(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String distritoAsGeoJson(String ubigeo) {
        return jdbc.queryForObject(
            "SELECT ST_AsGeoJSON(geom_pg) FROM geo.geo_distritos WHERE ubigeo = ?",
            String.class, ubigeo);
    }
}
