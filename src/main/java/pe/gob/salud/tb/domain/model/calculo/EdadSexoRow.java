package pe.gob.salud.tb.domain.model.calculo;

public record EdadSexoRow(
    String grupoEdad,   // p.ej. "0-4", "5-14", ...
    String sexo,        // "M"/"F"/"ND"
    long confirmados,
    long noconfirmados
) {}
