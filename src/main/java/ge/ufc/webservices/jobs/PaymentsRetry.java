package ge.ufc.webservices.jobs;

import ge.ufc.webservices.*;
import ge.ufc.webservices.dao.DatabaseException;
import ge.ufc.webservices.dao.DatabaseManager;
import ge.ufc.webservices.model.UserService;
import ge.ufc.webservices.model.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentsRetry implements Job {

    private static final Logger lgg = LogManager.getLogger();


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {


        try {
            lgg.debug("Opening connection");
            Connection connection;
            connection = DatabaseManager.getDatabaseConnection();

            String sql = "select status,pay_id,user_id,amount from payments where status = 1";
            String sql2 = "update payments set status =?,code = ?,amount = ?, sys_transaction_id = ? where pay_id = ? ";

            try (PreparedStatement ps = connection.prepareStatement(sql);
                 PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        String pay_id = rs.getString("pay_id");
                        int user_id = rs.getInt("user_id");
                        double amount = rs.getDouble("amount");


                        lgg.info("Found status with value 1");

                        UserService userService = new UserServiceImpl();
                        int sys_id = userService.fillBalance(pay_id, user_id, amount);

                        if (sys_id > 0)
                            lgg.info("Updating code and status columns");


                        ps2.setInt(1, 0);
                        ps2.setInt(2, 200);
                        ps2.setDouble(3, amount);
                        ps2.setInt(4, (sys_id));
                        ps2.setString(5, pay_id);

                        ps2.executeUpdate();
                        lgg.info("Updated");

                    }
                } catch (InternalError_Exception | AgentAccessDenied_Exception | UserNotFound_Exception |
                         DuplicateFault_Exception | AgentAuthFailed_Exception | AmountNotPositive_Exception |
                         DatabaseException_Exception e) {
                    lgg.error(e.getMessage());

                }
                lgg.info("Finished");
            }


        } catch (DatabaseException | SQLException e) {
            lgg.error(e.getMessage());

        }

    }
}
