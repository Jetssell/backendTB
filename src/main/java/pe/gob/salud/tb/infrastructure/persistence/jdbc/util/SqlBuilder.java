package pe.gob.salud.tb.infrastructure.persistence.jdbc.util;

public class SqlBuilder {
    private final StringBuilder sb = new StringBuilder();
    public SqlBuilder append(String s){ sb.append(s); return this; }
    public String build(){ return sb.toString(); }
}
