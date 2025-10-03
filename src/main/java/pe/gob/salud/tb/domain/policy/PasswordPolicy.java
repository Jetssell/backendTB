package pe.gob.salud.tb.domain.policy;
public final class PasswordPolicy {
  private PasswordPolicy(){}
  public static boolean strong(String plain){
    if(plain==null || plain.length()<8) return false;
    boolean up=false, low=false, num=false, sp=false;
    for(char c: plain.toCharArray()){
      if(Character.isUpperCase(c)) up=true;
      else if(Character.isLowerCase(c)) low=true;
      else if(Character.isDigit(c)) num=true;
      else sp=true;
    }
    return up && low && num && sp;
  }
}
