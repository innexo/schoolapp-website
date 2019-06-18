
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
public class NotificationService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Notification getById(int id) {
    String sql = "SELECT id, sender_id, date, text FROM notification WHERE id=?";
    RowMapper<Notification> rowMapper = new NotificationRowMapper();
    Notification notification = jdbcTemplate.queryForObject(sql, rowMapper, id);
    return notification;
  }

  public List<Notification> getAll() {
    String sql = "SELECT id, sender_id, date, text FROM notification";
    RowMapper<Notification> rowMapper = new NotificationRowMapper();
    return this.jdbcTemplate.query(sql, rowMapper);
  }	

  public void add(Notification notification) {
    //Add notification
    String sql = "INSERT INTO notification (id, sender_id, date, text) values (?, ?, ?, ?)";
    jdbcTemplate.update(sql, notification.id, notification.name, notification.senderId, notification.date, notification.text);

    //Fetch notification id
    sql = "SELECT id FROM notification WHERE sender_id=? AND date=? AND text=?";
    int id = jdbcTemplate.queryForObject(sql, Integer.class, notification.senderId, notification.date, notification.text);

    //Set notification id 
    notification.id = id;
  }

  public void update(Notification notification) {
    String sql = "UPDATE notification SET id=?, sender_id=?, date=?, text=? WHERE id=?";
    jdbcTemplate.update(sql, notification.id, notification.senderId, notification.date, notification.text, notification.id);
  }

  public void delete(int id) {
    String sql = "DELETE FROM notification WHERE id=?";
    jdbcTemplate.update(sql, id);
  }

  public boolean exists(int id) {
    String sql = "SELECT count(*) FROM notification WHERE id=?";
    int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    if(count == 0) {
      return false;
    } else {
      return true;
    }
  }
}
