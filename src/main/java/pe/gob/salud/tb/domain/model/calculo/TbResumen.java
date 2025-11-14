package pe.gob.salud.tb.domain.model.calculo;

public record TbResumen(
    long total,
    long confirmados,
    long noConfirmados,
    double pctConfirmados,
    String topEessRenaes,
    String topEessNombre,
    long topEessCountTotal,        // ← total de registros del EESS top
    long topEessCountConfirmados   // ← confirmados del EESS top (con_tb=1)
) {}
