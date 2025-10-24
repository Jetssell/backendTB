package pe.gob.salud.tb.domain.model.catalogo;

import java.util.List;

public record Ambitos(
    List<String> diris,
    List<String> provincias,
    List<String> distritos
) {}
