package ge.ufc.webservices.util;

import ge.ufc.webservices.*;
import ge.ufc.webservices.dao.DatabaseException;
import ge.ufc.webservices.dao.DatabaseManager;

import java.sql.*;

public class Utilities {

    public static int checkPayment(String pay_id, int user_id, double amount) throws DatabaseException, SQLException, AgentAccessDenied_Exception, UserNotFound_Exception, DuplicateFault_Exception, AgentAuthFailed_Exception, AmountNotPositive_Exception, DatabaseException_Exception, TransactionNotFound_Exception {
        Connection connection;
        connection = DatabaseManager.getDatabaseConnection();

        String sql = "select * from payments where pay_id=?";
        String sql2 = "update payments set status =? ,code = ? where pay_id = ? ";
        int payment_sys_id ;
        int code ;
        int id;
        int status;
        double payment_amount;
        String agent_trans_id;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement ps2 = connection.prepareStatement(sql2)) {

            ps.setString(1, pay_id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    payment_sys_id = rs.getInt("sys_transaction_id");
                    id = rs.getInt("user_id");
                    payment_amount = rs.getDouble("amount");
                    status = rs.getInt("status");
                    agent_trans_id = rs.getString("pay_id");
                    code = rs.getInt("code");


                }else{
                    throw new TransactionNotFound_Exception("transaction not found",new TransactionNotFound());
                }
                if (!(user_id == id && payment_amount == amount)) {
                    throw new DuplicateFault_Exception("Duplicate fault", new DuplicateFault());
                }
                switch (status) {
                    case 1:

                        ps2.setInt(1, 0);
                        ps2.setInt(2, 200);
                        ps2.setString(3, agent_trans_id);

                        ps2.executeUpdate();
                        return payment_sys_id;
                    case 2:
                    case 0: {
                        return code;
                    }

                }

            }

        }
        return 0;
    }
}
