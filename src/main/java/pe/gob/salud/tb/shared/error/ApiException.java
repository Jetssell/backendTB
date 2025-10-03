package pe.gob.salud.tb.shared.error;

public class ApiException extends RuntimeException {
  private final ErrorCode code;

  public ApiException(ErrorCode code, String message) {
    super(message);
    this.code = code;
  }

  public ErrorCode code() { return code; }
}
