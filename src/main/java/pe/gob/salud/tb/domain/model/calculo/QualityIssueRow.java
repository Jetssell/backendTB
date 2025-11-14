package pe.gob.salud.tb.domain.model.calculo;

public class QualityIssueRow {
  private String idCita;
  private String campo;
  private String problema;

  public QualityIssueRow() {}

  public QualityIssueRow(String idCita, String campo, String problema) {
    this.idCita = idCita;
    this.campo = campo;
    this.problema = problema;
  }

  public String getIdCita() { return idCita; }
  public void setIdCita(String idCita) { this.idCita = idCita; }

  public String getCampo() { return campo; }
  public void setCampo(String campo) { this.campo = campo; }

  public String getProblema() { return problema; }
  public void setProblema(String problema) { this.problema = problema; }
}
