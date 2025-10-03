package pe.gob.salud.tb.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.salud.tb.infrastructure.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
