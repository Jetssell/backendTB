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
  public CatalogosController(CatalogosService svc) { this.svc = svc; }

  // ===== Precarga =====
  @GetMapping("/ambitos")
  public Ambitos ambitos() { return svc.ambitos(); }

  // ===== Listas simples =====
  @GetMapping("/diris")
  public List<String> diris() { return svc.diris(); }

  @GetMapping("/provincias")
  public List<String> provincias(@RequestParam(required = false) String diris) {
    return svc.provincias(diris);
  }

  @GetMapping("/distritos")
  public List<String> distritos(
      @RequestParam(required = false) String diris,
      // acepta "prov" o "provincia"
      @RequestParam(required = false) String prov,
      @RequestParam(required = false, name = "provincia") String provincia
  ) {
    String provFinal = (prov != null && !prov.isBlank()) ? prov : provincia;
    return svc.distritos(diris, provFinal);
  }

  // ===== Autocomplete EESS =====
  @GetMapping("/eess")
  public Map<String, Object> eess(
      @RequestParam(required = false) String diris,
      // acepta "prov" o "provincia"
      @RequestParam(required = false) String prov,
      @RequestParam(required = false, name = "provincia") String provincia,
      // acepta "dist" o "distrito"
      @RequestParam(required = false) String dist,
      @RequestParam(required = false, name = "distrito") String distrito,
      @RequestParam(required = false, name = "q") String query,
      @RequestParam(required = false, defaultValue = "100") int limit
  ) {
    String provFinal = (prov != null && !prov.isBlank()) ? prov : provincia;
    String distFinal = (dist != null && !dist.isBlank()) ? dist : distrito;

    List<Eess> data = svc.eess(diris, provFinal, distFinal, query, limit);
    return Map.of("items", data, "total", data.size());
  }

  // ===== Listas "all" (sin filtros) =====
  @GetMapping("/diris/all")       public List<String> allDiris()       { return svc.allDiris(); }
  @GetMapping("/provincias/all")  public List<String> allProvincias()  { return svc.allProvincias(); }
  @GetMapping("/distritos/all")   public List<String> allDistritos()   { return svc.allDistritos(); }

  @GetMapping("/eess/all")
  public Map<String, Object> allEess(
      @RequestParam(required = false, name = "q") String query,
      @RequestParam(required = false, defaultValue = "200") int limit
  ) {
    List<Eess> data = svc.allEess(query, limit);
    return Map.of("items", data, "total", data.size());
  }
}
