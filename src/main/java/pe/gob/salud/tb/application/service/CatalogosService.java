package pe.gob.salud.tb.application.service;

import org.springframework.stereotype.Service;
import pe.gob.salud.tb.domain.model.catalogo.Ambitos;
import pe.gob.salud.tb.domain.model.catalogo.Eess;
import pe.gob.salud.tb.domain.port.CatalogoReaderPort;

import java.util.List;

@Service
public class CatalogosService {

  private final CatalogoReaderPort port;

  public CatalogosService(CatalogoReaderPort port) {
    this.port = port;
  }

  // ================== EXISTENTES (cascada) ==================

  /** Precarga: devuelve todas las listas (diris, provincias, distritos) */
  public Ambitos ambitos() {
    return port.loadAmbitos();
  }

  /** Lista DIRIS (puede venir de la precarga) */
  public List<String> diris() {
    // Si quieres que salga siempre de la precarga:
    return port.loadAmbitos().diris();
    // Alternativa directa (equivalente):
    // return port.listAllDiris();
  }

  /** Provincias filtradas por DIRIS (si diris = null, devuelve todas) */
  public List<String> provincias(String diris) {
    if (diris == null || diris.isBlank()) {
      return port.listAllProvincias();
    }
    // Si tu port expone un método específico por diris, úsalo. Si no, ya te devolvemos todas.
    // return port.provinciasByDiris(diris);
    return port.listAllProvincias(); // fallback: todas
  }

  /** Distritos filtrados por DIRIS y/o provincia (si ambos null, devuelve todos) */
  public List<String> distritos(String diris, String provincia) {
    if ((diris == null || diris.isBlank()) && (provincia == null || provincia.isBlank())) {
      return port.listAllDistritos();
    }
    // Si tu port expone un método específico por diris/provincia, úsalo. Si no, devolvemos todos:
    // return port.distritosBy(diris, provincia);
    return port.listAllDistritos(); // fallback: todos
  }

  /** Autocomplete de EESS (acepta filtros opcionales) */
  public List<Eess> eess(String diris, String prov, String dist, String q, int limit) {
    return port.findEess(diris, prov, dist, q, limit);
  }

  // ================== NUEVOS (traer todo) ==================

  public List<String> allDiris()       { return port.listAllDiris(); }
  public List<String> allProvincias()  { return port.listAllProvincias(); }
  public List<String> allDistritos()   { return port.listAllDistritos(); }

  /** Lista global de EESS con búsqueda opcional y límite */
  public List<Eess> allEess(String q, int limit) {
    return port.listAllEess(q, limit);
  }
}
