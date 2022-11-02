package ge.ufc.webservices.model;

import javax.ws.rs.Path;
import java.sql.SQLException;


public interface InsertDao {

    int fill(String pay_id, int user_id, double amount, int trans_id, int code, int status)throws SQLException;
}
