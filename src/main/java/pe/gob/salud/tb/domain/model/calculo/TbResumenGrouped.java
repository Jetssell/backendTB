package pe.gob.salud.tb.domain.model.calculo;

/** Resumen TB agrupado por ámbito (diris | provincia | distrito | renaes). */
public record TbResumenGrouped(
    String group,        // valor del ámbito agrupado
    long total,          // total de registros
    long confirmados,    // con_tb = 1
    long noConfirmados,  // con_tb = 0
    double pctConfirmados
) {}
