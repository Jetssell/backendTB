package pe.gob.salud.tb.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pe.gob.salud.tb.application.mapper.UserMapper;
import pe.gob.salud.tb.domain.model.User;
import pe.gob.salud.tb.domain.port.UserReaderPort;
import pe.gob.salud.tb.infrastructure.entity.UserEntity;
import pe.gob.salud.tb.infrastructure.repository.UserRepository;

@Component
public class UserJpaAdapter implements UserReaderPort {
  private final UserRepository repo;

  public UserJpaAdapter(UserRepository repo) {
    this.repo = repo;
  }

  @Override
  public User findByUsername(String username) {
    return repo.findByUsernameIgnoreCase(username)
               .map(UserMapper::toDomain)
               .orElse(null);
  }

  @Override
  public String passwordHashOf(String username) {
    return repo.findByUsernameIgnoreCase(username)
               .map(UserEntity::getContrasenaHash)
               .orElse(null);
  }
}
