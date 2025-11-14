// RepeticionRow.java
package pe.gob.salud.tb.domain.model.calculo;

public class RepeticionRow {
  public final String idPersona;
  public final long veces;

  public RepeticionRow(String idPersona, long veces) {
    this.idPersona = idPersona;
    this.veces = veces;
  }
}
