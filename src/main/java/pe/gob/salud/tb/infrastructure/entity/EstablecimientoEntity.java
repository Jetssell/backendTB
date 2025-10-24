package pe.gob.salud.tb.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Mapea la tabla clinica.establecimientos
 *
 * Columnas:
 * - renaes      (varchar(12))  -> PK
 * - eess        (text)
 * - diris       (text)
 * - prove_eess  (text)
 * - dist_eess   (text)          // <— ACTUALIZADO: era bpchar(6)
 * - lat         (float8)
 * - lon         (float8)
 * - id_distrito (int8)
 */
@Entity
@Table(name = "establecimientos", schema = "clinica")
public class EstablecimientoEntity {

  @Id
  @Column(name = "renaes", length = 12, nullable = false)
  private String renaes;

  @Column(name = "eess")
  private String eess;

  @Column(name = "diris")
  private String diris;

  @Column(name = "prove_eess")
  private String proveEess;

  // era CHAR(6). Ahora la columna es TEXT:
  @Column(name = "dist_eess", columnDefinition = "text")
  private String distEess;

  @Column(name = "lat")
  private Double lat;

  @Column(name = "lon")
  private Double lon;

  @Column(name = "id_distrito")
  private Long idDistrito;

  // Getters / Setters
  public String getRenaes() { return renaes; }
  public void setRenaes(String renaes) { this.renaes = renaes; }

  public String getEess() { return eess; }
  public void setEess(String eess) { this.eess = eess; }

  public String getDiris() { return diris; }
  public void setDiris(String diris) { this.diris = diris; }

  public String getProveEess() { return proveEess; }
  public void setProveEess(String proveEess) { this.proveEess = proveEess; }

  public String getDistEess() { return distEess; }
  public void setDistEess(String distEess) { this.distEess = distEess; }

  public Double getLat() { return lat; }
  public void setLat(Double lat) { this.lat = lat; }

  public Double getLon() { return lon; }
  public void setLon(Double lon) { this.lon = lon; }

  public Long getIdDistrito() { return idDistrito; }
  public void setIdDistrito(Long idDistrito) { this.idDistrito = idDistrito; }
}
