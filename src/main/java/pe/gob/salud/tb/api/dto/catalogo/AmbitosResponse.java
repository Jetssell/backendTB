package pe.gob.salud.tb.api.dto.catalogo;

import java.util.List;

public record AmbitosResponse(
    List<String> diris,
    List<String> provincias,
    List<String> distritos
) {}
