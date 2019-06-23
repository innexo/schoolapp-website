package innexo;

public class User {
  public int id;
  public String name;
  // not public so it doesn't get serialized by jackson
  String passwordHash;
  boolean administrator;
}
