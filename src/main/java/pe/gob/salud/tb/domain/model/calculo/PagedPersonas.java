package pe.gob.salud.tb.domain.model.calculo;

import java.util.List;

public record PagedPersonas(
    List<PersonaRow> items,
    long total,
    int page,
    int size
) {}
