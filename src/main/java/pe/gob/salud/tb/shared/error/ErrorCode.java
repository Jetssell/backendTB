package pe.gob.salud.tb.shared.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
  AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
  AUTH_USER_NOT_FOUND(HttpStatus.NOT_FOUND),
  AUTH_USER_DISABLED(HttpStatus.LOCKED);

  private final HttpStatus status;
  ErrorCode(HttpStatus status){ this.status = status; }
  public HttpStatus status(){ return status; }
}
