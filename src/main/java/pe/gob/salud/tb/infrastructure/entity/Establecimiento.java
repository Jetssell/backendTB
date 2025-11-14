package pe.gob.salud.tb.infrastructure.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "establecimientos", schema = "clinica")
public class Establecimiento {

  @Id
  @Column(name = "renaes")
  private String renaes;

  @Column(name = "eess")
  private String eess;

  @Column(name = "diris")
  private String diris;

  @Column(name = "prove_eess")
  private String provincia;

  @Column(name = "dist_eess")
  private String distrito;

  @Column(name = "lat")
  private Double lat;

  @Column(name = "lon")
  private Double lon;

  // ===== getters/setters =====
  public String getRenaes() { return renaes; }
  public void setRenaes(String renaes) { this.renaes = renaes; }

  public String getEess() { return eess; }
  public void setEess(String eess) { this.eess = eess; }

  public String getDiris() { return diris; }
  public void setDiris(String diris) { this.diris = diris; }

  public String getProvincia() { return provincia; }
  public void setProvincia(String provincia) { this.provincia = provincia; }

  public String getDistrito() { return distrito; }
  public void setDistrito(String distrito) { this.distrito = distrito; }

  public Double getLat() { return lat; }
  public void setLat(Double lat) { this.lat = lat; }

  public Double getLon() { return lon; }
  public void setLon(Double lon) { this.lon = lon; }
}
