package pe.gob.salud.tb.domain.model.calculo;

import java.util.List;

public class RepeticionesResumen {
  private long total;
  private List<RepeticionRow> items;

  public RepeticionesResumen() {}

  public RepeticionesResumen(long total, List<RepeticionRow> items) {
    this.total = total;
    this.items = items;
  }

  public long getTotal() { return total; }
  public void setTotal(long total) { this.total = total; }

  public List<RepeticionRow> getItems() { return items; }
  public void setItems(List<RepeticionRow> items) { this.items = items; }
}
