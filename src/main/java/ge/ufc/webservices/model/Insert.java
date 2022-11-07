package ge.ufc.webservices.model;

import java.sql.*;
import java.util.Date;

public class Insert implements InsertDao {
    private Connection connection;

    public Insert(Connection connection) {
        this.connection = connection;
    }


    @Override
    public String fill(String pay_id, int user_id, double amount, int trans_id, int code, int status) {
        String sql = "insert into payments(pay_id,user_id,amount,sys_transaction_id,request_time,response_time,code,status)" +
                "values(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, new String[]{"pay_id"})) {
            Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

            ps.setString(1, pay_id);
            ps.setInt(2, user_id);
            ps.setDouble(3, amount);
            ps.setInt(4, trans_id);
            ps.setTimestamp(5, timestamp);
            ps.setTimestamp(6, new Timestamp(new Date().getTime()));

            ps.setInt(7, code);
            ps.setInt(8, status);

            
            ps.execute();
            ResultSet resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateResponseTime(String pay_id) {
        String sql = "update payments set response_time = ? where pay_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
            ps.setString(2, pay_id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
