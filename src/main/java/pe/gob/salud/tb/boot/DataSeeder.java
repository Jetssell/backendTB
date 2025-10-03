package pe.gob.salud.tb.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.salud.tb.infrastructure.entity.RoleEntity;
import pe.gob.salud.tb.infrastructure.entity.UserEntity;
import pe.gob.salud.tb.infrastructure.repository.RoleRepository;
import pe.gob.salud.tb.infrastructure.repository.UserRepository;

import java.time.Instant;
import java.util.UUID;

@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seed(RoleRepository roles,
                         UserRepository users,
                         PasswordEncoder encoder,
                         ObjectMapper om) {
    return args -> seedData(roles, users, encoder, om);
  }

  @Transactional
  void seedData(RoleRepository roles,
                UserRepository users,
                PasswordEncoder encoder,
                ObjectMapper om) {

    RoleEntity admin = roles.findByNombreIgnoreCase("ADMIN")
        .orElseGet(() -> { var r = new RoleEntity(); r.setNombre("ADMIN"); return roles.save(r); });

    roles.findByNombreIgnoreCase("ANALISTA")
        .orElseGet(() -> { var r = new RoleEntity(); r.setNombre("ANALISTA"); return roles.save(r); });

    roles.findByNombreIgnoreCase("SUPERVISOR")
        .orElseGet(() -> { var r = new RoleEntity(); r.setNombre("SUPERVISOR"); return roles.save(r); });

    if (users.findByUsernameIgnoreCase("admin").isEmpty()) {
      UserEntity u = new UserEntity();
      u.setId(UUID.randomUUID());
      u.setUsername("admin");
      u.setNombre("Admin del Sistema");              // <--- nombre por defecto
      u.setCorreo("admin@tb.local");
      u.setContrasenaHash(encoder.encode("Admin#2025"));
      u.setRol(admin);

      ObjectNode scope = om.createObjectNode().put("diris", "Lima Este");
      u.setScope(scope);

      u.setCreatedAt(Instant.now());
      u.setUpdatedAt(Instant.now());
      users.save(u);
    }
  }
}
