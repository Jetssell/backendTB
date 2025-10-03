package pe.gob.salud.tb.domain.value;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {
  private static final Pattern P =
      Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private final String value;

  public Email(String value) {
    if (value == null || !P.matcher(value).matches()) {
      throw new IllegalArgumentException("invalid_email");
    }
    this.value = value;
  }

  public String value() { return value; }

  @Override public String toString() { return value; }

  @Override public boolean equals(Object o) {
    return (o instanceof Email e) && Objects.equals(e.value, value);
  }

  @Override public int hashCode() { return Objects.hash(value); }
}
