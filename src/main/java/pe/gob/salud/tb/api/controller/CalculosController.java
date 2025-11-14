package pe.gob.salud.tb.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pe.gob.salud.tb.application.service.CalculosService;
import pe.gob.salud.tb.domain.model.calculo.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class CalculosController {

  private final CalculosService svc;
  public CalculosController(CalculosService svc) { this.svc = svc; }

  // ======== CALIDAD: MÉTRICAS ========
  @GetMapping("/calculos/data-quality/factores")
  public Object dataQualityFactores(
      @RequestParam(required = false) String variables,
      @RequestParam(required = false) String groupBy,
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) String renaes,
      @RequestParam(required = false) Integer limit,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      @RequestParam(required = false) String clasif
  ) {
    log.info("➡️ [GET /calculos/data-quality/factores] variables={}, groupBy={}, diris={}, provincia={}, distrito={}, renaes={}, limit={}, desde={}, hasta={}, clasif={}",
        variables, groupBy, diris, provincia, distrito, renaes, limit, desde, hasta, clasif);

    List<String> vars = (variables == null || variables.isBlank())
        ? List.of()
        : Arrays.stream(variables.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();

    Object result;
    if (groupBy == null || groupBy.isBlank()) {
      result = svc.calidadFactores(vars, diris, provincia, distrito, renaes, desde, hasta, clasif);
    } else {
      result = svc.calidadFactoresGrouped(vars, groupBy.toLowerCase(), diris, provincia, distrito, renaes, limit, desde, hasta, clasif);
    }

    log.info("⬅️ Resultado factores: {}", resumen(result));
    return result;
  }

  // ======== CALIDAD: REGISTROS PROBLEMÁTICOS ========
  @GetMapping("/calculos/data-quality/problemas")
  public List<Map<String, String>> dataQualityProblemas(
      @RequestParam(required = false) String variables,
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) String renaes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      @RequestParam(required = false) String clasif,
      @RequestParam(required = false, defaultValue = "200") Integer limit
  ) {
    log.info("➡️ [GET /calculos/data-quality/problemas] variables={}, diris={}, provincia={}, distrito={}, renaes={}, desde={}, hasta={}, clasif={}, limit={}",
        variables, diris, provincia, distrito, renaes, desde, hasta, clasif, limit);

    List<String> vars = (variables == null || variables.isBlank())
        ? List.of()
        : Arrays.stream(variables.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();

    var result = svc.dataQualityProblemas(vars, diris, provincia, distrito, renaes, desde, hasta, clasif, limit)
        .stream()
        .map(r -> Map.of("idCita", r.getIdCita(), "campo", r.getCampo(), "problema", r.getProblema()))
        .collect(Collectors.toList());

    log.info("⬅️ Problemas encontrados: {} registros", result.size());
    return result;
  }

  // ======== RESUMEN ÚNICO ========
  @GetMapping("/calculos/tb/resumen")
  public TbResumen tbResumen(
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) String renaes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      @RequestParam(required = false) String clasif
  ) {
    log.info("➡️ [GET /calculos/tb/resumen] diris={}, provincia={}, distrito={}, renaes={}, desde={}, hasta={}, clasif={}",
        diris, provincia, distrito, renaes, desde, hasta, clasif);

    var result = svc.tbResumen(diris, provincia, distrito, renaes, desde, hasta, clasif);
    log.info("⬅️ Resultado resumen: {}", resumen(result));
    return result;
  }

  // ======== TOP EESS ========
  @GetMapping("/calculos/tb/top-eess")
  public List<TbTopEess> tbTopEess(
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) String renaes,
      @RequestParam(defaultValue = "5") int limit,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      @RequestParam(required = false) String clasif
  ) {
    log.info("➡️ [GET /calculos/tb/top-eess] diris={}, provincia={}, distrito={}, renaes={}, limit={}, desde={}, hasta={}, clasif={}",
        diris, provincia, distrito, renaes, limit, desde, hasta, clasif);

    var result = svc.tbTopEess(diris, provincia, distrito, renaes, limit, desde, hasta, clasif);
    log.info("⬅️ Resultado top-eess: {} registros", result.size());
    return result;
  }

  // ======== SERIE ========
  @GetMapping("/calculos/tb/serie")
  public List<SerieRow> tbSerie(
      @RequestParam(defaultValue = "mensual") String granularidad,
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) String renaes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      @RequestParam(required = false) String clasif
  ) {
    log.info("➡️ [GET /calculos/tb/serie] granularidad={}, diris={}, provincia={}, distrito={}, renaes={}, desde={}, hasta={}, clasif={}",
        granularidad, diris, provincia, distrito, renaes, desde, hasta, clasif);

    var result = svc.tbSerie(granularidad, diris, provincia, distrito, renaes, desde, hasta, clasif);
    log.info("⬅️ Resultado serie: {} puntos", result.size());
    return result;
  }

  // ======== PERSONAS ========
  @GetMapping("/personas")
  public PagedPersonas personas(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) String renaes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      @RequestParam(required = false, defaultValue = "all") String clasif
  ) {
    log.info("➡️ [GET /personas] page={}, size={}, diris={}, provincia={}, distrito={}, renaes={}, desde={}, hasta={}, clasif={}",
        page, size, diris, provincia, distrito, renaes, desde, hasta, clasif);

    var result = svc.personasBuscar(page, size, diris, provincia, distrito, renaes, desde, hasta, clasif);
    log.info("⬅️ Resultado personas (paginado)");
    return result;
  }

  // ======== PERSONA – resumen ========
  @GetMapping("/personas/{idPersona}/resumen")
  public List<PersonaVarResumen> personaResumen(
      @PathVariable String idPersona,
      @RequestParam String variables,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
  ) {
    log.info("➡️ [GET /personas/{}/resumen] variables={}, desde={}, hasta={}", idPersona, variables, desde, hasta);
    List<String> vars = Arrays.stream(variables.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();
    var result = svc.personaResumen(idPersona, vars, desde, hasta);
    log.info("⬅️ Resultado personaResumen: {} variables", result.size());
    return result;
  }

  // ======== MAPA ========
  @GetMapping("/mapa/eess")
  public List<MapPointEess> mapaEess(
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
  ) {
    log.info("➡️ [GET /mapa/eess] diris={}, provincia={}, distrito={}, desde={}, hasta={}", diris, provincia, distrito, desde, hasta);
    var result = svc.mapaEess(diris, provincia, distrito, desde, hasta);
    log.info("⬅️ Resultado mapaEess: {} puntos", result.size());
    return result;
  }

  @GetMapping("/mapa/distritos")
  public List<MapChoroplethDistrito> mapaDistritos(
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
  ) {
    log.info("➡️ [GET /mapa/distritos] diris={}, desde={}, hasta={}", diris, desde, hasta);
    var result = svc.mapaDistritos(diris, desde, hasta);
    log.info("⬅️ Resultado mapaDistritos: {} distritos", result.size());
    return result;
  }

  // ======== ADMIN ========
  @GetMapping("/admin/calculos/tb/incidencia")
  public IncidenciaK tbIncidencia(
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) String renaes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      @RequestParam(required = false) String clasif
  ) {
    log.info("➡️ [GET /admin/calculos/tb/incidencia] diris={}, provincia={}, distrito={}, renaes={}, desde={}, hasta={}, clasif={}",
        diris, provincia, distrito, renaes, desde, hasta, clasif);
    var result = svc.tbIncidencia(diris, provincia, distrito, renaes, desde, hasta, clasif);
    log.info("⬅️ Resultado incidencia: {}", resumen(result));
    return result;
  }

  @GetMapping("/admin/calculos/tb/edad-sexo")
  public List<EdadSexoRow> tbPorEdadSexo(
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) String renaes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      @RequestParam(required = false) String clasif
  ) {
    log.info("➡️ [GET /admin/calculos/tb/edad-sexo] diris={}, provincia={}, distrito={}, renaes={}, desde={}, hasta={}, clasif={}",
        diris, provincia, distrito, renaes, desde, hasta, clasif);
    var result = svc.tbPorEdadSexo(diris, provincia, distrito, renaes, desde, hasta, clasif);
    log.info("⬅️ Resultado edad-sexo: {} registros", result.size());
    return result;
  }

  @GetMapping("/admin/calculos/tb/repeticiones")
  public RepeticionesResumen tbRepeticiones(
      @RequestParam(required = false) String diris,
      @RequestParam(required = false) String provincia,
      @RequestParam(required = false) String distrito,
      @RequestParam(required = false) String renaes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      @RequestParam(required = false, defaultValue = "50") Integer limit,
      @RequestParam(required = false) String clasif
  ) {
    log.info("➡️ [GET /admin/calculos/tb/repeticiones] diris={}, provincia={}, distrito={}, renaes={}, desde={}, hasta={}, limit={}, clasif={}",
        diris, provincia, distrito, renaes, desde, hasta, limit, clasif);
    var result = svc.tbRepeticiones(diris, provincia, distrito, renaes, desde, hasta, limit, clasif);
    log.info("⬅️ Resultado repeticiones: {}", resumen(result));
    return result;
  }

  private String resumen(Object obj) {
    if (obj == null) return "null";
    if (obj instanceof Collection<?> c) return c.size() + " elementos";
    return obj.toString();
  }
}
