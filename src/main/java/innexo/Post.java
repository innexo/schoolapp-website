package innexo;

import java.sql.Timestamp;

public class Post {

	public int id;
	int creatorId;
	int authorizerId;
	public Timestamp creationDate;
	public Timestamp authorizationDate;
	public String title;
	public String body;
	public String imageLink;
	
	//For jackson only
	public User creator;
	public User authorizer;
}
