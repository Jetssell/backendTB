package pe.gob.salud.tb.infrastructure.entity;

import jakarta.persistence.*;

@Entity
@Table(schema = "auth", name = "roles")
public class RoleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "nombre", nullable = false, unique = true)
  private String nombre;

  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
}
