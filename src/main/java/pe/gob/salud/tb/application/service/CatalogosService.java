package pe.gob.salud.tb.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.salud.tb.domain.model.catalogo.Ambitos;
import pe.gob.salud.tb.domain.model.catalogo.Eess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class CatalogosService {

  @PersistenceContext
  private EntityManager em;

  private static boolean notBlank(String s) { return s != null && !s.isBlank(); }

  // ========== Precarga ==========
  public Ambitos ambitos() {
    return new Ambitos(diris(), allProvincias(), allDistritos());
  }

  // ========= DIRIS / Provincias / Distritos =========
  public List<String> diris() {
    Query q = em.createNativeQuery("""
      SELECT DISTINCT e.diris
      FROM clinica.establecimientos e
      WHERE e.diris IS NOT NULL AND e.diris <> ''
      ORDER BY 1
    """);
    @SuppressWarnings("unchecked")
    List<String> r = q.getResultList();
    return r;
  }

  public List<String> provincias(String diris) {
    String where = notBlank(diris) ? "WHERE UPPER(e.diris) = UPPER(:diris)" : "";
    Query q = em.createNativeQuery("""
      SELECT DISTINCT e.prove_eess
      FROM clinica.establecimientos e
      %s
      ORDER BY 1
    """.formatted(where));
    if (notBlank(diris)) q.setParameter("diris", diris.trim());
    @SuppressWarnings("unchecked")
    List<String> r = q.getResultList();
    return r;
  }

  public List<String> distritos(String diris, String provincia) {
    List<String> conds = new ArrayList<>();
    if (notBlank(diris))     conds.add("UPPER(e.diris) = UPPER(:diris)");
    if (notBlank(provincia)) conds.add("UPPER(e.prove_eess) = UPPER(:prov)");
    String where = conds.isEmpty() ? "" : "WHERE " + String.join(" AND ", conds);

    Query q = em.createNativeQuery("""
      SELECT DISTINCT e.dist_eess
      FROM clinica.establecimientos e
      %s
      ORDER BY 1
    """.formatted(where));
    if (notBlank(diris))     q.setParameter("diris", diris.trim());
    if (notBlank(provincia)) q.setParameter("prov",  provincia.trim());

    @SuppressWarnings("unchecked")
    List<String> r = q.getResultList();
    return r;
  }

  // ========= EESS (autocomplete) =========
  public List<Eess> eess(String diris, String provincia, String distrito, String qText, int limit) {
    List<String> conds = new ArrayList<>();
    if (notBlank(diris))     conds.add("UPPER(e.diris) = UPPER(:diris)");
    if (notBlank(provincia)) conds.add("UPPER(e.prove_eess) = UPPER(:prov)");
    if (notBlank(distrito))  conds.add("UPPER(e.dist_eess) = UPPER(:dist)");
    if (notBlank(qText))     conds.add("(UPPER(e.eess) LIKE :q OR UPPER(e.renaes) LIKE :q)");
    String where = conds.isEmpty() ? "" : "WHERE " + String.join(" AND ", conds);

    Query q = em.createNativeQuery("""
      SELECT e.renaes, e.eess, e.lat, e.lon
      FROM clinica.establecimientos e
      %s
      ORDER BY e.eess
      LIMIT :lim
    """.formatted(where));

    if (notBlank(diris))     q.setParameter("diris", diris.trim());
    if (notBlank(provincia)) q.setParameter("prov",  provincia.trim());
    if (notBlank(distrito))  q.setParameter("dist",  distrito.trim());
    if (notBlank(qText))     q.setParameter("q", "%" + qText.trim().toUpperCase() + "%");
    q.setParameter("lim", limit <= 0 ? 20 : limit);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();
    List<Eess> out = new ArrayList<>();
    for (Object[] r : rows) {
      out.add(new Eess(
          Objects.toString(r[0], null),     // renaes
          Objects.toString(r[1], null),     // nombre
          r[2] == null ? null : ((Number) r[2]).doubleValue(), // lat
          r[3] == null ? null : ((Number) r[3]).doubleValue()  // lon
      ));
    }
    return out;
  }

  // ========= Listas "ALL" =========
  public List<String> allDiris() {
    return diris();
  }

  public List<String> allProvincias() {
    Query q = em.createNativeQuery("""
      SELECT DISTINCT e.prove_eess
      FROM clinica.establecimientos e
      WHERE e.prove_eess IS NOT NULL AND e.prove_eess <> ''
      ORDER BY 1
    """);
    @SuppressWarnings("unchecked")
    List<String> r = q.getResultList();
    return r;
  }

  public List<String> allDistritos() {
    Query q = em.createNativeQuery("""
      SELECT DISTINCT e.dist_eess
      FROM clinica.establecimientos e
      WHERE e.dist_eess IS NOT NULL AND e.dist_eess <> ''
      ORDER BY 1
    """);
    @SuppressWarnings("unchecked")
    List<String> r = q.getResultList();
    return r;
  }

  public List<Eess> allEess(String qText, int limit) {
    String where = notBlank(qText) ? "WHERE (UPPER(e.eess) LIKE :q OR UPPER(e.renaes) LIKE :q)" : "";
    Query q = em.createNativeQuery("""
      SELECT e.renaes, e.eess, e.lat, e.lon
      FROM clinica.establecimientos e
      %s
      ORDER BY e.eess
      LIMIT :lim
    """.formatted(where));
    if (notBlank(qText)) q.setParameter("q", "%" + qText.trim().toUpperCase() + "%");
    q.setParameter("lim", limit <= 0 ? 200 : limit);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();
    List<Eess> out = new ArrayList<>();
    for (Object[] r : rows) {
      out.add(new Eess(
          Objects.toString(r[0], null),
          Objects.toString(r[1], null),
          r[2] == null ? null : ((Number) r[2]).doubleValue(),
          r[3] == null ? null : ((Number) r[3]).doubleValue()
      ));
    }
    return out;
  }
}
