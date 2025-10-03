package pe.gob.salud.tb.domain.policy;
import java.util.Map;
public final class ScopePolicy {
  private ScopePolicy(){}
  public static boolean hasDiris(Map<String,String> scope){ return scope!=null && scope.containsKey("diris"); }
}
