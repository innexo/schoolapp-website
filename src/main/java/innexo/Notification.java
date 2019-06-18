package innexo;

import java.sql.Timestamp;

public class Notification {
	public int id;
	int senderId;
	public Timestamp date;
	public String text;

  // For jackson only
  public User sender;
}
