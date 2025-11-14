package pe.gob.salud.tb.domain.model.calculo;

public class SerieRow {
  private String periodo;          // YYYY-MM (mensual) o IYYY-IW (semanal)
  private long confirmados;
  private long noConfirmados;

  public SerieRow() {}

  public SerieRow(String periodo, long confirmados, long noConfirmados) {
    this.periodo = periodo;
    this.confirmados = confirmados;
    this.noConfirmados = noConfirmados;
  }

  public String getPeriodo() { return periodo; }
  public void setPeriodo(String periodo) { this.periodo = periodo; }

  public long getConfirmados() { return confirmados; }
  public void setConfirmados(long confirmados) { this.confirmados = confirmados; }

  public long getNoConfirmados() { return noConfirmados; }
  public void setNoConfirmados(long noConfirmados) { this.noConfirmados = noConfirmados; }
}
