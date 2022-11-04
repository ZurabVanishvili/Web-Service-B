package ge.ufc.webservices.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Date;

public class Insert implements InsertDao {
    private Connection connection;

    public Insert(Connection connection) {
        this.connection = connection;
    }


    @Override
    public int fill(String pay_id, int user_id, double amount, int trans_id, int code, int status) {
        String sql = "insert into payments(pay_id,user_id,amount,sys_transaction_id,request_time,response_time,code,status)" +
                "values(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
            ps.setTimestamp(5, timestamp);

            ps.setString(1, pay_id);
            ps.setInt(2, user_id);
            ps.setDouble(3, amount);
            ps.setInt(4, trans_id);
            ps.setInt(7, code);
            ps.setInt(8, status);
            ps.setTimestamp(6, new Timestamp(new Date().getTime()));

            ps.execute();

            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
