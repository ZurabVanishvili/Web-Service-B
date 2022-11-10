package ge.ufc.webservices.ws;

import com.google.gson.Gson;
import ge.ufc.webservices.*;
import ge.ufc.webservices.dao.DatabaseException;
import ge.ufc.webservices.dao.DatabaseManager;
import ge.ufc.webservices.jobs.PaymentsRetry;
import ge.ufc.webservices.model.*;
import ge.ufc.webservices.model.User;

import ge.ufc.webservices.util.Utilities;
import jakarta.xml.ws.WebServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.SQLException;


public class AgentServiceImpl implements AgentService {
    private static final Logger lgg = LogManager.getLogger();

    static {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            lgg.error(e.getMessage(), e);
//        }
        }
    }


    @Override
    public Response userByID(int id) {
        try {
            lgg.info("Searching for user with id:" + id);

            UserService u = new UserServiceImpl();
            User user = u.getUser((id));

            lgg.trace("Found user: " + user);
            return Response.status(Response.Status.OK).entity(user).build();
        } catch (AgentAccessDenied_Exception | AgentAuthFailed_Exception | DatabaseException_Exception e) {
            lgg.error("Internal Error");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal error").build();
        } catch (UserNotFound_Exception e) {
            lgg.error("User not found");
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        } catch (WebServiceException e) {
            lgg.error("Timeout exception occurred");
            return Response.status(Response.Status.REQUEST_TIMEOUT).entity("Request time out").build();

        }
    }

    @Override
    public Response fillBalance(String jsonString) throws SQLException, AgentAccessDenied_Exception, UserNotFound_Exception, AgentAuthFailed_Exception, AmountNotPositive_Exception, DatabaseException_Exception, DatabaseException, TransactionNotFound_Exception {
        lgg.info("Getting fields to fill balance");

        UserService userService = new UserServiceImpl();
        Connection connection;
        Insert insert = null;
        ReturnJsonClass json = new ReturnJsonClass();

        JSONObject jsonObject = new JSONObject(jsonString);
        String trans_id = null;
        int user_id = 0;
        double amount = 0;
        int sys_id = 0;
        int code;
        try {
            lgg.info("Deserializing Json String");
            connection = DatabaseManager.getDatabaseConnection();
            insert = new Insert(connection);
            trans_id = jsonObject.getString("agent_transaction_id");
            user_id = jsonObject.getInt("user_id");
            amount = jsonObject.getDouble("amount");

            try {
                //თუ აგენტმა გამოიძახა ეს მეთოდი უკვე არსებული pay_id-ით ვამოწმებ
                //ველებს და ამის მიხედვით ვაბრუნებ შესაბამის კოდს
                int check = Utilities.checkPayment(trans_id, user_id, amount);
                if (check > 0) {
                    json.setSys_id(check);
                    return Response.status(check).entity((json)).build();
                }
            } catch (TransactionNotFound_Exception ignored) {
            }

            // აქ უკვე ახალი ტრანზაქციაა და ვამატებ როგორც transaction ისე payments ცხრილებში, თუ
            //ყველა ველი სწორადაა გადმოცემული.

            sys_id = userService.fillBalance(trans_id, user_id, amount);
            lgg.info("Filling balance");
            //ამ მეთოდში მაქვს payments-ში insert-ი.
            insert.fill(trans_id, user_id, amount, sys_id, 200, 0);

            json.setSys_id(sys_id);

            //ამ მეთოდით response_time-ს ვააფდეითებ
            insert.updateResponseTime(trans_id);

            lgg.info("Transaction ended successfully.Returning transaction id");
            lgg.trace(convertUsingGson(json));


            return Response.status(Response.Status.OK).entity(convertUsingGson(json)).build();


            //ქვემოთ კი ყველა exception-ზე შესაბამის insert-ს ვაკეთებ payments-ში
            //და ვაბრუნებ code ველს.
        } catch (WebServiceException e) {
            assert insert != null;
            insert.fill(trans_id, user_id, amount, sys_id, 408, 1);
            code = Utilities.checkPayment(trans_id, user_id, amount);
            return Response.status(Response.Status.REQUEST_TIMEOUT).entity(code).build();

        } catch (InternalError_Exception |
                DatabaseException_Exception | DatabaseException e) {
            lgg.error("Connection Problem. " + e);
            assert insert != null;
            insert.fill(trans_id, user_id, amount, sys_id, 500, 1);
            code = Utilities.checkPayment(trans_id, user_id, amount);

            return Response.status(Response.Status.REQUEST_TIMEOUT).entity(code).build();

        } catch (AmountNotPositive_Exception e) {
            lgg.error("Wrong value for amount");
            insert.fill(trans_id, user_id, amount, sys_id, 400, 2);
            code = Utilities.checkPayment(trans_id, user_id, amount);
            return Response.status(Response.Status.BAD_REQUEST).entity(code).build();

        } catch (UserNotFound_Exception | AgentAccessDenied_Exception | AgentAuthFailed_Exception e) {
            lgg.error("Some access exception occurred" + e);
            insert.fill(trans_id, user_id, amount, sys_id, 404, 2);
            code = Utilities.checkPayment(trans_id, user_id, amount);

            return Response.status(Response.Status.NOT_FOUND).entity(code).build();

        } catch (DuplicateFault_Exception e) {
            lgg.error("Conflict. " + e);
            code = Utilities.checkPayment(trans_id, user_id, amount);

            return Response.status(Response.Status.CONFLICT).entity(code).build();
        }
    }


    public static String convertUsingGson(ReturnJsonClass e) {
        Gson gson = new Gson();
        return gson.toJson(e);
    }


}