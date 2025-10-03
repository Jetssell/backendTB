package pe.gob.salud.tb.domain.model;

import java.util.List;
import java.util.Map;

public class User {
  private final String id;
  private final String username;
  private final String name;              // <- NUEVO
  private final boolean enabled;
  private final List<String> roles;
  private final Map<String,String> scope;

  public User(String id, String username, String name,
              boolean enabled, List<String> roles, Map<String,String> scope) {
    this.id = id; this.username = username; this.name = name;
    this.enabled = enabled; this.roles = roles; this.scope = scope;
  }
  public String id(){ return id; }
  public String username(){ return username; }
  public String name(){ return name; }     // <- getter nuevo
  public boolean enabled(){ return enabled; }
  public List<String> roles(){ return roles; }
  public Map<String,String> scope(){ return scope; }
}
