package pe.gob.salud.tb.domain.model.calculo;

public record IncidenciaK(
    long confirmados,      // numerador
    long poblacion,        // denominador
    double tasaX100k       // (confirmados / poblacion) * 100000
) {}
