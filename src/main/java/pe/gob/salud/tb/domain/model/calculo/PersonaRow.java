package pe.gob.salud.tb.domain.model.calculo;

import java.time.LocalDate;

public record PersonaRow(
    String idPersona,
    LocalDate ultimaFecha,
    String diris,
    String eess,
    String distrito,
    String clasificacion,
    String localizacion
) {}
