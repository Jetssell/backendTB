package pe.gob.salud.tb.infrastructure.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "auth", name = "usuarios")
public class UserEntity {

  @Id @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(name = "correo", nullable = false, unique = true)
  private String correo;

  // NUEVO: nombre completo
  @Column(name = "nombre")
  private String nombre;

  @Column(name = "contrasena_hash", nullable = false)
  private String contrasenaHash;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "role_id", nullable = false)
  private RoleEntity rol;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "scope", columnDefinition = "jsonb")
  private JsonNode scope;

  @Column(name = "created_at") private Instant createdAt;
  @Column(name = "updated_at") private Instant updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getCorreo() { return correo; }
  public void setCorreo(String correo) { this.correo = correo; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getContrasenaHash() { return contrasenaHash; }
  public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

  public RoleEntity getRol() { return rol; }
  public void setRol(RoleEntity rol) { this.rol = rol; }

  public JsonNode getScope() { return scope; }
  public void setScope(JsonNode scope) { this.scope = scope; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
