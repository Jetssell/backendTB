package pe.gob.salud.tb.domain.event;
public record UserLoggedIn(String userId, long whenEpochMs) {}
