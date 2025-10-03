package pe.gob.salud.tb.middleware;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.gob.salud.tb.shared.error.ApiErrorPayload;
import pe.gob.salud.tb.shared.error.ApiException;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiErrorPayload> onApi(ApiException ex, HttpServletRequest req) {
    return ResponseEntity
        .status(ex.code().status())
        .body(new ApiErrorPayload(ex.code().name(), ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorPayload> onUnexpected(Exception ex, HttpServletRequest req) {
    // Opcional: loggear ex
    return ResponseEntity
        .internalServerError()
        .body(new ApiErrorPayload("UNEXPECTED_ERROR", "Ocurrió un error inesperado."));
  }
}