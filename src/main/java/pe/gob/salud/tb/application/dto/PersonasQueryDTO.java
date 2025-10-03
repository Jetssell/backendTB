package pe.gob.salud.tb.application.dto;

import java.time.LocalDate;

public record PersonasQueryDTO(LocalDate desde, LocalDate hasta, String ambito, String valor, String clasificacion) {}
