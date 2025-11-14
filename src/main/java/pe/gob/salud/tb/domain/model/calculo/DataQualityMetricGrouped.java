package pe.gob.salud.tb.domain.model.calculo;

/** Métrica de calidad agrupada por un ámbito (diris | provincia | distrito | renaes). */
public record DataQualityMetricGrouped(
    String group,     // valor del grupo (p.ej. "LIMA DIRIS SUR" o un RENAES)
    String variable,  // token de la variable (p.ej. "con_tb")
    long total,
    long nulls,
    long zeros,
    long ones,
    long invalids
) {}
