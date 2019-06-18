
package innexo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class NotificationRowMapper implements RowMapper<Notification> {
  @Override
  public Notification mapRow(ResultSet row, int rowNum) throws SQLException {
    Notification n = new Notification();
    n.id = row.getInt("id");
    n.senderId = row.getString("sender_id");
    n.date = row.getString("date");
    n.text = row.getString("text");
    return n;
  }
}
