package pe.gob.salud.tb.domain.model.catalogo;

public class Eess {
  private String renaes;
  private String nombre;
  private Double lat;
  private Double lon;

  public Eess() {}

  public Eess(String renaes, String nombre, Double lat, Double lon) {
    this.renaes = renaes;
    this.nombre = nombre;
    this.lat = lat;
    this.lon = lon;
  }

  // getters y setters
  public String getRenaes() { return renaes; }
  public void setRenaes(String renaes) { this.renaes = renaes; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public Double getLat() { return lat; }
  public void setLat(Double lat) { this.lat = lat; }

  public Double getLon() { return lon; }
  public void setLon(Double lon) { this.lon = lon; }
}
