package pe.gob.salud.tb.api.controller;

import org.springframework.web.bind.annotation.*;
import pe.gob.salud.tb.application.service.CatalogosService;
import pe.gob.salud.tb.domain.model.catalogo.Ambitos;
import pe.gob.salud.tb.domain.model.catalogo.Eess;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogosController {

  private final CatalogosService svc;

  public CatalogosController(CatalogosService svc) {
    this.svc = svc;
  }

 

  /** Precarga: devuelve todas las listas (diris, provincias, distritos) */
  @GetMapping("/ambitos")
  public Ambitos ambitos() {
    return svc.ambitos();
  }

  /** Lista DIRIS */
  @GetMapping("/diris")
  public List<String> diris() {
    return svc.diris();
  }

  /** Provincias (opcionalmente filtradas por DIRIS) */
  @GetMapping("/provincias")
  public List<String> provincias(@RequestParam(required = false) String diris) {
    return svc.provincias(diris);
  }

  /** Distritos (opcionalmente por DIRIS y/o Provincia) */
  @GetMapping("/distritos")
  public List<String> distritos(@RequestParam(required = false) String diris,
                                @RequestParam(required = false, name = "provincia") String provincia) {
    return svc.distritos(diris, provincia);
  }

  /**
   * Autocomplete EESS
   * Ej: /api/catalogos/eess?diris=LIMA%20SUR&prov=VILLA%20EL%20SALVADOR&q=posta&limit=20
   */
  @GetMapping("/eess")
  public Map<String, Object> eess(@RequestParam(required = false) String diris,
                                  @RequestParam(required = false, name = "prov") String provincia,
                                  @RequestParam(required = false, name = "dist") String distrito,
                                  @RequestParam(required = false, name = "q") String query,
                                  @RequestParam(required = false, defaultValue = "20") int limit) {
    List<Eess> data = svc.eess(diris, provincia, distrito, query, limit);
    return Map.of("items", data, "total", data.size());
  }

 

  /** Todas las DIRIS */
  @GetMapping("/diris/all")
  public List<String> allDiris() {
    return svc.allDiris();
  }

  /** Todas las provincias (sin filtrar) */
  @GetMapping("/provincias/all")
  public List<String> allProvincias() {
    return svc.allProvincias();
  }

  /** Todos los distritos (sin filtrar) */
  @GetMapping("/distritos/all")
  public List<String> allDistritos() {
    return svc.allDistritos();
  }

  /** Todos los EESS (con búsqueda opcional y límite) */
  @GetMapping("/eess/all")
  public Map<String, Object> allEess(@RequestParam(required = false, name = "q") String query,
                                     @RequestParam(required = false, defaultValue = "200") int limit) {
    List<Eess> data = svc.allEess(query, limit);
    return Map.of("items", data, "total", data.size());
  }
}
