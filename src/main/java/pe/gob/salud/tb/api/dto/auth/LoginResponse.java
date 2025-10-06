package pe.gob.salud.tb.api.dto.auth;

public record LoginResponse(
    String name,
    String role
) {}
