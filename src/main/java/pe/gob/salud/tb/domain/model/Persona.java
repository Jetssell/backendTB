package pe.gob.salud.tb.domain.model;

import java.util.UUID;

public record Persona(UUID id, String sexo, String ubigeoRes, String centroPoblado) {}
