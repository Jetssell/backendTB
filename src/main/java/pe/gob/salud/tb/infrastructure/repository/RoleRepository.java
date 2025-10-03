package pe.gob.salud.tb.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.gob.salud.tb.infrastructure.entity.RoleEntity;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
  Optional<RoleEntity> findByNombreIgnoreCase(String nombre);
}
