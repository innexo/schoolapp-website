package innexo;

public class Utils {
  public static String sanitize(String str) {
    return str == null ? null : str.replaceAll("[^a-zA-Z0-9]", "");
  }

  public static boolean isValid(String str) {
    return str != null && str.trim().length() > 0;
  }
}
