package pe.gob.salud.tb.domain.port;

import pe.gob.salud.tb.domain.model.calculo.*;

import java.time.LocalDate;
import java.util.List;

public interface CalculosPort {

  // ===== Calidad
  List<DataQualityMetric> dataQualityFactores(
      List<String> variables,
      String diris, String provincia, String distrito, String renaes,
      LocalDate desde, LocalDate hasta,
      String clasif
  );

  List<DataQualityMetricGrouped> dataQualityFactoresGrouped(
      List<String> variables, String groupBy,
      String diris, String provincia, String distrito, String renaes,
      Integer limit, LocalDate desde, LocalDate hasta,
      String clasif
  );

  List<QualityIssueRow> dataQualityProblemas(
      List<String> variables,
      String diris, String provincia, String distrito, String renaes,
      LocalDate desde, LocalDate hasta, String clasif,
      Integer limit
  );

  // ===== Resumen / Top
  TbResumen tbResumen(
      String diris, String provincia, String distrito, String renaes,
      LocalDate desde, LocalDate hasta, String clasif
  );

  List<TbTopEess> tbTopEess(
      String diris, String provincia, String distrito, String renaes,
      int limit, LocalDate desde, LocalDate hasta, String clasif
  );

  // ===== Serie
  List<SerieRow> tbSerie(
      String granularidad,
      String diris, String provincia, String distrito, String renaes,
      LocalDate desde, LocalDate hasta, String clasif
  );

  // ===== Personas
 PagedPersonas personasBuscar(
    Integer page, Integer size,
    String diris, String provincia, String distrito, String renaes,
    LocalDate desde, LocalDate hasta, String clasif
);


  List<PersonaVarResumen> personaResumen(
      String idPersona, List<String> variables,
      LocalDate desde, LocalDate hasta
  );

  // ===== Mapas
  List<MapPointEess> mapaEess(
      String diris, String provincia, String distrito,
      LocalDate desde, LocalDate hasta
  );

  List<MapChoroplethDistrito> mapaDistritos(
      String diris, LocalDate desde, LocalDate hasta
  );

  // ===== Admin
  IncidenciaK tbIncidencia(
      String diris, String provincia, String distrito, String renaes,
      LocalDate desde, LocalDate hasta, String clasif
  );

  List<EdadSexoRow> tbPorEdadSexo(
      String diris, String provincia, String distrito, String renaes,
      LocalDate desde, LocalDate hasta, String clasif
  );

  RepeticionesResumen tbRepeticiones(
      String diris, String provincia, String distrito, String renaes,
      LocalDate desde, LocalDate hasta, Integer limit, String clasif
  );
}
