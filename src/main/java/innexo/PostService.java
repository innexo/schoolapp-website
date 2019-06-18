
package innexo;

import java.util.List;
import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PostService {

	@Autowired 
	private JdbcTemplate jdbcTemplate;

	public Post getById(int id) {
		String sql =
				"SELECT id, creator_id, authorizer_id, creation_date, authorization_date, title, body, image_link FROM post WHERE id=?";
		RowMapper<Post> rowMapper = new PostRowMapper();
		Post post = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return post;
	}

	public List<Post> getAll() {
		String sql =
				"SELECT id, creator_id, authorizer_id, creation_date, authorization_date, title, body, image_link FROM post";
		RowMapper<Post> rowMapper = new PostRowMapper();
		return this.jdbcTemplate.query(sql, rowMapper);
	}

	public void add(Post post) {
		// Add post
		String sql =
				"INSERT INTO post (creator_id, authorizer_id, creation_date, authorization_date, title, body, image_link) values (?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, post.creatorId, post.authorizerId, post.creationDate, post.authorizationDate, post.title, post.body, post.imageLink);

		// Fetch post id
		sql =
				"SELECT id FROM post WHERE creator_id=? AND authorizer_id=? AND creation_date=? AND authorization_date=? AND title=? AND body=? AND image_link=? ORDER BY id DESC";
		List<Integer> id = jdbcTemplate.queryForList(
				sql, Integer.class, post.creatorId, post.authorizerId, post.creationDate, post.authorizationDate, post.title, post.body, post.imageLink);

		// Set post id
		if (!id.isEmpty()) {
			post.id = id.get(0);
		}
	}

  public List<Post> query(
      Integer postId, 
      Boolean authorized,
      Integer creatorId, 
      Integer authorizerId, 
      Timestamp minDateCreation, 
      Timestamp maxDateCreation, 
      Timestamp minDateAuthorization, 
      Timestamp maxDateAuthorization, 
      Integer count) {
		String sql = "SELECT  id, creator_id, authorizer_id, creation_date, authorization_date, title, body, image_link FROM post WHERE 1=1 " + 
				(postId               == null ? "" : " AND id="+postId) +
				(authorized           == null ? "" : " AND authorization_date IS " + (authorized ? "NOT" : "") + "NULL") + // if authorized is true, look for not null
				(creatorId            == null ? "" : " AND creator_id="+creatorId) +
				(authorizerId         == null ? "" : " AND authorizer_id="+authorizerId) +
				(minDateCreation      == null ? "" : " AND creation_date >= FROM_UNIXTIME(" + minDateCreation.toInstant().getEpochSecond() + ")") +
				(maxDateCreation      == null ? "" : " AND creation_date <= FROM_UNIXTIME(" + maxDateCreation.toInstant().getEpochSecond() + ")") +
				(minDateAuthorization == null ? "" : " AND authorization_date >= FROM_UNIXTIME(" + minDateAuthorization.toInstant().getEpochSecond() + ")") +
				(maxDateAuthorization == null ? "" : " AND authorization_date <= FROM_UNIXTIME(" + maxDateAuthorization.toInstant().getEpochSecond() + ")") +
				(count                == null ? "" : " LIMIT "+count) +
				                                     " ;" ;
		RowMapper<Post> rowMapper = new PostRowMapper();
		return this.jdbcTemplate.query(sql, rowMapper);
  }

	public void update(Post post) {
		String sql = "UPDATE post SET creator_id=?, authorizer_id=?, creation_date=?, authorization_date=?, title=?, body=?, image_link=? WHERE id=?";
		jdbcTemplate.update(
				sql, post.creatorId, post.authorizerId, post.creationDate, post.authorizationDate, post.title, post.body, post.imageLink, post.id);
	}

	public void delete(int id) {
		String sql = "DELETE FROM post WHERE id=?";
		jdbcTemplate.update(sql, id);
	}

	public boolean exists(int id) {
		String sql = "SELECT count(*) FROM post WHERE id=?";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		return count != 0;
	}
}
