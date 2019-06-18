package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PostRowMapper implements RowMapper<Post> {

//	there are three people responsible for each requesti
//
//	creator = person who creates the request for student to go to classroom
//	user = student who is intended to go to the classroom
//	authorizer = person who must authorize the request

  @Override
  public Post mapRow(ResultSet row, int rowNum) throws SQLException {
    Post p = new Post();
    p.id = row.getInt("id");
    p.creatorId = row.getInt("creator_id");
    p.authorizerId = row.getInt("authorizer_id");
    p.creationDate = row.getTimestamp("creation_date");
    p.authorizationDate = row.getTimestamp("authorization_date");
    p.title = row.getString("title");
    p.body = row.getString("body");
    p.imageLink = row.getString("image_link");

    return p;
  }
}
