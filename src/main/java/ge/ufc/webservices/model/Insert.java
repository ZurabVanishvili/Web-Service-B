package ge.ufc.webservices.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Insert implements InsertDao {
    private Connection connection;

    public Insert(Connection connection) {
        this.connection = connection;
    }


    @Override
    public int fill(String pay_id, int user_id, double amount, int trans_id, int code, int status)  {
        String sql = "insert into payments(pay_id,user_id,amount,transaction_id,request_time,response_time,code,status)" +
                "values(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, pay_id);
            ps.setInt(2, user_id);
            ps.setDouble(3, amount);
            ps.setInt(4, trans_id);
            ps.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
            ps.setTimestamp(6, new Timestamp(new java.util.Date().getTime()));
            ps.setInt(7, code);
            ps.setInt(8, status);

            ps.execute();

            return 1;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
}
