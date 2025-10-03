package pe.gob.salud.tb.api.dto.auth;

public record LoginResponse(
    String uid,
    String name,
    String role,
    String token
) {}
