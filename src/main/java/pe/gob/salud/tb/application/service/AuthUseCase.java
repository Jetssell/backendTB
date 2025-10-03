package pe.gob.salud.tb.application.service;
import java.util.Map;
public interface AuthUseCase {
  Map<String,Object> login(String username, String password);
}
