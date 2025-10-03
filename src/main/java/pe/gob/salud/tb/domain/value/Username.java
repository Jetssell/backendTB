package pe.gob.salud.tb.domain.value;
import java.util.Objects;
public final class Username {
  private final String value;
  public Username(String value){ if(value==null || value.isBlank()) throw new IllegalArgumentException("invalid_username"); this.value=value.trim(); }
  public String value(){ return value; }
  @Override public String toString(){ return value; }
  @Override public boolean equals(Object o){ return (o instanceof Username u) && Objects.equals(u.value, value); }
  @Override public int hashCode(){ return Objects.hash(value); }
}
