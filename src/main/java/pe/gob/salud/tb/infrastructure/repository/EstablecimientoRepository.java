package pe.gob.salud.tb.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.gob.salud.tb.infrastructure.entity.Establecimiento;

import java.util.List;

@Repository
public interface EstablecimientoRepository extends JpaRepository<Establecimiento, String> {

  // Proyección para el autocomplete EESS
  interface EessRow {
    String getRenaes();
    String getEess();
    Double getLat();
    Double getLon();
  }

  // ---- Catálogos base ----
  @Query(value = """
      SELECT DISTINCT e.diris
      FROM clinica.establecimientos e
      WHERE e.diris IS NOT NULL
      ORDER BY 1
      """, nativeQuery = true)
  List<String> findAllDiris();

  @Query(value = """
      SELECT DISTINCT e.prove_eess
      FROM clinica.establecimientos e
      WHERE e.prove_eess IS NOT NULL
      ORDER BY 1
      """, nativeQuery = true)
  List<String> findAllProvincias();

  @Query(value = """
      SELECT DISTINCT e.dist_eess
      FROM clinica.establecimientos e
      WHERE e.dist_eess IS NOT NULL
      ORDER BY 1
      """, nativeQuery = true)
  List<String> findAllDistritos();

  // ---- Filtros en cascada ----
  @Query(value = """
      SELECT DISTINCT e.prove_eess
      FROM clinica.establecimientos e
      WHERE (:diris IS NULL OR e.diris = :diris)
        AND e.prove_eess IS NOT NULL
      ORDER BY 1
      """, nativeQuery = true)
  List<String> findProvinciasByDiris(@Param("diris") String diris);

  @Query(value = """
      SELECT DISTINCT e.dist_eess
      FROM clinica.establecimientos e
      WHERE (:diris IS NULL OR e.diris = :diris)
        AND (:prov  IS NULL OR e.prove_eess = :prov)
        AND e.dist_eess IS NOT NULL
      ORDER BY 1
      """, nativeQuery = true)
  List<String> findDistritosBy(@Param("diris") String diris,
                               @Param("prov")  String provincia);

  // ---- Autocomplete EESS (con lat/lon) ----
  @Query(value = """
      SELECT
          e.renaes  AS renaes,
          e.eess    AS eess,
          e.lat     AS lat,
          e.lon     AS lon
      FROM clinica.establecimientos e
      WHERE (:diris IS NULL OR e.diris = :diris)
        AND (:prov  IS NULL OR e.prove_eess = :prov)
        AND (:dist  IS NULL OR e.dist_eess  = :dist)
        AND (:q     IS NULL OR LOWER(e.eess) LIKE LOWER(CONCAT('%', :q, '%')))
      ORDER BY e.eess
      """, nativeQuery = true)
  List<EessRow> findEess(@Param("diris") String diris,
                         @Param("prov")  String prov,
                         @Param("dist")  String dist,
                         @Param("q")     String q);
}
