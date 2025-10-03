package pe.gob.salud.tb.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "pe.gob.salud.tb")
@EnableJpaRepositories(basePackages = "pe.gob.salud.tb.infrastructure.repository")
@EntityScan(basePackages = "pe.gob.salud.tb.infrastructure.entity")
public class TbApplication {
  public static void main(String[] args) {
    SpringApplication.run(TbApplication.class, args);
  }
}
