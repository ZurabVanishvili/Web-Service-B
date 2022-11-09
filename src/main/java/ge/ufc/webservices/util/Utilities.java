package ge.ufc.webservices.util;

import ge.ufc.webservices.*;
import ge.ufc.webservices.dao.DatabaseException;
import ge.ufc.webservices.dao.DatabaseManager;
import ge.ufc.webservices.model.UserService;
import ge.ufc.webservices.model.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class Utilities {
    private static UserService userService ;


    private static final Logger lgg = LogManager.getLogger();

    public static int checkPayment(String pay_id, int user_id, double amount) throws DatabaseException, SQLException, AgentAccessDenied_Exception, UserNotFound_Exception, AgentAuthFailed_Exception, AmountNotPositive_Exception, DatabaseException_Exception, TransactionNotFound_Exception {
        Connection connection;
        connection = DatabaseManager.getDatabaseConnection();

        //ამ მეთოდს ვიძახებ fillBalance-ში.თუ pay_id უკვე გამოყენებულია ბაზიდან ვასელექთებ იმ ველებს
        //რომლებიც მჭირდება შემოწმებისთვის.თუ user_id ან amount შეცვალა აგენტმა ვაბრუნებ 409 კოდს.ამ კოდს
        //უკვე DuplicateFault-ის catch-ში ვიღებ და ვაბრუნებ response-ს.
        String sql = "select * from payments where pay_id=?";
        String sql2 = "update payments set status =? ,code = ?, amount = ? , sys_transaction_id = ?  where pay_id = ? ";
        int payment_sys_id;
        int code;
        int id;
        int status;
        double payment_amount;
        String agent_trans_id;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement ps2 = connection.prepareStatement(sql2)) {

            ps.setString(1, pay_id);

            try (ResultSet rs = ps.executeQuery()) {
                lgg.info("Validating request");
                if (rs.next()) {

                    payment_sys_id = rs.getInt("sys_transaction_id");
                    id = rs.getInt("user_id");
                    payment_amount = rs.getDouble("amount");
                    status = rs.getInt("status");
                    agent_trans_id = rs.getString("pay_id");
                    code = rs.getInt("code");


                } else {
                    throw new TransactionNotFound_Exception("Transaction not found", new TransactionNotFound());
                }
                if (!(user_id == id && payment_amount == amount)) {
                    return 409;
                }
                //თუ დუბლიკაციის შემოწმება გაიარა request-მა უკვე გადავდივარ სტატუსზე და
                //ვაბრუნებ შესაბამის კოდებს.
                lgg.info("Validated request.Transaction found");
                switch (status) {
                    case 1:
                        userService = new UserServiceImpl();
                        int sys_id = userService.fillBalance(pay_id,user_id,amount);

                        if (sys_id > 0 ) {

                            ps2.setInt(1, 0);
                            ps2.setInt(2, 200);

                        } else {
                            ps2.setInt(1, 2);
                            ps2.setInt(2, 400);
                        }
                        ps2.setDouble(3, amount);
                        ps2.setInt(4, sys_id);
                        ps2.setString(5, agent_trans_id);
                        ps2.executeUpdate();

                        lgg.info("Updated status value");
                        lgg.trace(payment_sys_id);
                        return payment_sys_id;

                    case 2:
                    case 0: {

                        lgg.info("Code: " + code);

                        return code;
                    }

                }

            } catch (DuplicateFault_Exception | InternalError_Exception e) {
                e.printStackTrace();
            }

        }
        return -1;
    }
}