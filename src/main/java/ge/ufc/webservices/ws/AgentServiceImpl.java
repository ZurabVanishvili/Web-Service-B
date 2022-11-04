package ge.ufc.webservices.ws;

import com.google.gson.Gson;
import ge.ufc.webservices.*;
import ge.ufc.webservices.dao.DatabaseException;
import ge.ufc.webservices.dao.DatabaseManager;
import ge.ufc.webservices.model.*;
import ge.ufc.webservices.model.User;

import ge.ufc.webservices.util.Utilities;
import jakarta.xml.ws.WebServiceException;

import org.json.JSONObject;

import javax.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.SQLException;


public class AgentServiceImpl implements AgentService {
    @Override
    public Response userByID(int id) {
        try {

            UserService u = new UserServiceImpl();
            User user = u.getUser((id));

            return Response.status(Response.Status.OK).entity(user).build();
        } catch (AgentAccessDenied_Exception | AgentAuthFailed_Exception | DatabaseException_Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal error").build();
        } catch (UserNotFound_Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        } catch (WebServiceException e) {
            return Response.status(Response.Status.REQUEST_TIMEOUT).entity("Request time out").build();

        }
    }

    @Override
    public Response fillBalance(String jsonString) throws SQLException, AgentAccessDenied_Exception, UserNotFound_Exception, DuplicateFault_Exception, AgentAuthFailed_Exception, AmountNotPositive_Exception, DatabaseException_Exception, DatabaseException, TransactionNotFound_Exception {

        UserService u = new UserServiceImpl();
        Connection connection;
        InsertDao insert = null;
        ReturnJsonClass json = new ReturnJsonClass();

        JSONObject jsonObject = new JSONObject(jsonString);
        String trans_id = null;
        int user_id = 0;
        double amount = 0;
        int sys_id = 0;
        int code;
        try {
            connection = DatabaseManager.getDatabaseConnection();
            insert = new Insert(connection);
            trans_id = jsonObject.getString("agent_transaction_id");
            user_id = jsonObject.getInt("user_id");
            amount = jsonObject.getDouble("amount");
            try {
                int a = Utilities.checkPayment(trans_id, user_id, amount);
                if (a > 0) {
                    json.setSys_id(a);
                    return Response.status(Response.Status.OK).entity(convertUsingGson(json)).build();
                }
            } catch (TransactionNotFound_Exception e) {

            }
            sys_id = u.fillBalance(trans_id, user_id, amount);
            insert.fill(trans_id, user_id, amount, sys_id, 200, 0);
            json.setSys_id(sys_id);

            return Response.status(Response.Status.OK).entity(convertUsingGson(json)).build();


        } catch (InternalError_Exception |
                WebServiceException | DatabaseException_Exception | DatabaseException e) {
            assert insert != null;
            insert.fill(trans_id, user_id, 0, sys_id, 500, 1);
            code = Utilities.checkPayment(trans_id, user_id, amount);

            return Response.status(Response.Status.REQUEST_TIMEOUT).entity(code).build();

        } catch (AmountNotPositive_Exception e) {
            insert.fill(trans_id, user_id, amount, sys_id, 400, 2);
            code = Utilities.checkPayment(trans_id, user_id, amount);
            return Response.status(Response.Status.BAD_REQUEST).entity(code).build();

        } catch (UserNotFound_Exception | AgentAccessDenied_Exception | AgentAuthFailed_Exception e) {
            insert.fill(trans_id, user_id, amount, sys_id, 404, 2);
            code = Utilities.checkPayment(trans_id, user_id, amount);

            return Response.status(Response.Status.NOT_FOUND).entity(code).build();

        }
    }
    public static String convertUsingGson(ReturnJsonClass e)
    {
        Gson gson = new Gson();
        return gson.toJson(e);
    }


}
