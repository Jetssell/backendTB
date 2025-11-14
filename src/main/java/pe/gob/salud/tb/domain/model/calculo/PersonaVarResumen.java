package pe.gob.salud.tb.domain.model.calculo;

public record PersonaVarResumen(
    String variable,
    long positivos,
    long negativos
) {}
