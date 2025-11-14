package pe.gob.salud.tb.application.service;

import org.springframework.stereotype.Service;
import pe.gob.salud.tb.domain.model.calculo.*;
import pe.gob.salud.tb.domain.port.CalculosPort;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalculosService {

  private final CalculosPort port;
  public CalculosService(CalculosPort port) { this.port = port; }

  // ===== Calidad
  public List<DataQualityMetric> calidadFactores(List<String> variables,
                                                 String diris, String provincia, String distrito, String renaes,
                                                 LocalDate desde, LocalDate hasta, String clasif) {
    return port.dataQualityFactores(variables, diris, provincia, distrito, renaes, desde, hasta, clasif);
  }

  public List<DataQualityMetricGrouped> calidadFactoresGrouped(List<String> variables, String groupBy,
                                                               String diris, String provincia, String distrito, String renaes,
                                                               Integer limit, LocalDate desde, LocalDate hasta, String clasif) {
    return port.dataQualityFactoresGrouped(variables, groupBy, diris, provincia, distrito, renaes, limit, desde, hasta, clasif);
  }

  public List<QualityIssueRow> dataQualityProblemas(List<String> variables,
                                                    String diris, String provincia, String distrito, String renaes,
                                                    LocalDate desde, LocalDate hasta, String clasif, Integer limit) {
    return port.dataQualityProblemas(variables, diris, provincia, distrito, renaes, desde, hasta, clasif, limit);
  }

  // ===== Resumen / Top
  public TbResumen tbResumen(String diris, String provincia, String distrito, String renaes,
                             LocalDate desde, LocalDate hasta, String clasif) {
    return port.tbResumen(diris, provincia, distrito, renaes, desde, hasta, clasif);
  }

  public List<TbTopEess> tbTopEess(String diris, String provincia, String distrito, String renaes,
                                   int limit, LocalDate desde, LocalDate hasta, String clasif) {
    return port.tbTopEess(diris, provincia, distrito, renaes, limit, desde, hasta, clasif);
  }

  // ===== Serie
  public List<SerieRow> tbSerie(String granularidad,
                                String diris, String provincia, String distrito, String renaes,
                                LocalDate desde, LocalDate hasta, String clasif) {
    return port.tbSerie(granularidad, diris, provincia, distrito, renaes, desde, hasta, clasif);
  }

  // ===== Personas
  public PagedPersonas personasBuscar(
    Integer page, Integer size,
    String diris, String provincia, String distrito, String renaes,
    LocalDate desde, LocalDate hasta, String clasif
) {
  return port.personasBuscar(page, size, diris, provincia, distrito, renaes, desde, hasta, clasif);
}

  public List<PersonaVarResumen> personaResumen(String idPersona, List<String> variables,
                                                LocalDate desde, LocalDate hasta) {
    return port.personaResumen(idPersona, variables, desde, hasta);
  }

  // ===== Mapas
  public List<MapPointEess> mapaEess(String diris, String provincia, String distrito,
                                     LocalDate desde, LocalDate hasta) {
    return port.mapaEess(diris, provincia, distrito, desde, hasta);
  }

  public List<MapChoroplethDistrito> mapaDistritos(String diris, LocalDate desde, LocalDate hasta) {
    return port.mapaDistritos(diris, desde, hasta);
  }

  // ===== Admin
  public IncidenciaK tbIncidencia(String diris, String provincia, String distrito, String renaes,
                                  LocalDate desde, LocalDate hasta, String clasif) {
    return port.tbIncidencia(diris, provincia, distrito, renaes, desde, hasta, clasif);
  }

  public List<EdadSexoRow> tbPorEdadSexo(String diris, String provincia, String distrito, String renaes,
                                         LocalDate desde, LocalDate hasta, String clasif) {
    return port.tbPorEdadSexo(diris, provincia, distrito, renaes, desde, hasta, clasif);
  }

  public RepeticionesResumen tbRepeticiones(String diris, String provincia, String distrito, String renaes,
                                            LocalDate desde, LocalDate hasta, Integer limit, String clasif) {
    return port.tbRepeticiones(diris, provincia, distrito, renaes, desde, hasta, limit, clasif);
  }
}
