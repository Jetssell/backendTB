package pe.gob.salud.tb.domain.model.calculo;

/**
 * Métrica de calidad por variable binaria.
 * - variable : nombre "amigable" (token) de la columna evaluada
 * - total    : total de filas evaluadas (tras filtros)
 * - nulls    : valores nulos
 * - zeros    : cantidad de ceros (0)
 * - ones     : cantidad de unos (1)
 * - invalids : valores distintos de {0,1} (no nulos)
 */
public record DataQualityMetric(
    String variable,
    long total,
    long nulls,
    long zeros,
    long ones,
    long invalids
) {}
