package pe.gob.salud.tb.application.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pe.gob.salud.tb.domain.model.User;
import pe.gob.salud.tb.infrastructure.entity.UserEntity;

import java.util.List;
import java.util.Map;

public final class UserMapper {
  private static final ObjectMapper OM = new ObjectMapper();
  private UserMapper(){}

  public static User toDomain(UserEntity e){
    // rol único en la tabla usuarios -> roles de dominio
    List<String> roles = List.of(
        (e.getRol()!=null && e.getRol().getNombre()!=null) ? e.getRol().getNombre() : "USER"
    );

    Map<String,String> scope = Map.of();
    if (e.getScope()!=null && !e.getScope().isNull()) {
      scope = OM.convertValue(e.getScope(), new TypeReference<Map<String,String>>() {});
    }

    String name = (e.getNombre()!=null && !e.getNombre().isBlank()) ? e.getNombre() : e.getUsername();

    // enabled: si no tienes columna, asumimos true
    return new User(e.getId().toString(), e.getUsername(), name, true, roles, scope);
  }
}
