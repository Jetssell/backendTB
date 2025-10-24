package pe.gob.salud.tb.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.gob.salud.tb.infrastructure.entity.EstablecimientoEntity;

import java.util.List;

public interface EstablecimientoRepository extends JpaRepository<EstablecimientoEntity, String> {

  // Proyección mínima para el autocomplete de EESS
  interface EessRow {
    String getRenaes();
    String getEess();
  }

  // Autocomplete EESS (los filtros pueden venir null)
  @Query("""
         select distinct e.renaes as renaes, e.eess as eess
         from EstablecimientoEntity e
         where (:diris is null or e.diris = :diris)
           and (:prov  is null or e.proveEess = :prov)
           and (:dist  is null or e.distEess  = :dist)
           and (:q     is null or lower(e.eess) like lower(concat('%', :q, '%')))
         order by e.eess asc
         """)
  List<EessRow> findEess(
      @Param("diris") String diris,
      @Param("prov")  String prov,
      @Param("dist")  String dist,
      @Param("q")     String q
  );

  // ===== Listas “en cascada” (ya las tenías) =====
  @Query("select distinct e.diris from EstablecimientoEntity e where e.diris is not null order by e.diris asc")
  List<String> findAllDiris();

  @Query("select distinct e.proveEess from EstablecimientoEntity e where e.proveEess is not null order by e.proveEess asc")
  List<String> findAllProvincias();

  @Query("select distinct e.distEess from EstablecimientoEntity e where e.distEess is not null order by e.distEess asc")
  List<String> findAllDistritos();

  // ===== Filtros específicos para cascada (por si los usas) =====
  @Query("select distinct e.proveEess from EstablecimientoEntity e where (:diris is null or e.diris = :diris) and e.proveEess is not null order by e.proveEess asc")
  List<String> findProvinciasByDiris(@Param("diris") String diris);

  @Query("""
         select distinct e.distEess
         from EstablecimientoEntity e
         where (:diris is null or e.diris = :diris)
           and (:prov  is null or e.proveEess = :prov)
           and e.distEess is not null
         order by e.distEess asc
         """)
  List<String> findDistritosByDirisAndProv(@Param("diris") String diris, @Param("prov") String prov);
}
