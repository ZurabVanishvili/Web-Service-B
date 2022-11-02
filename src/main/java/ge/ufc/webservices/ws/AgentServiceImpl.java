package ge.ufc.webservices.ws;

import ge.ufc.webservices.*;
import ge.ufc.webservices.dao.DatabaseException;
import ge.ufc.webservices.dao.DatabaseManager;
import ge.ufc.webservices.model.*;
import ge.ufc.webservices.model.User;

import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.net.SocketTimeoutException;
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
        } catch (SocketTimeoutException e) {
            return Response.status(Response.Status.REQUEST_TIMEOUT).entity("Request time out").build();

        }
    }

    @Override
    public Response fillBalance(String jsonString) {
        UserService u = new UserServiceImpl();
        Connection connection;
        try {
            connection = DatabaseManager.getDatabaseConnection();
            InsertDao insert = new Insert(connection);

            JSONObject jsonObject = new JSONObject(jsonString);
            String trans_id = jsonObject.getString("agent_transaction_id");
            int user_id = jsonObject.getInt("user_id");
            double amount = jsonObject.getDouble("amount");

            int sys_id = u.fillBalance(trans_id, user_id, amount);

//            insert.fill(trans_id,user_id,amount,sys_id,200,0);

            return Response.status(Response.Status.OK).entity(sys_id).build();
        } catch (AgentAccessDenied_Exception | AgentAuthFailed_Exception | DatabaseException_Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal error").build();
        } catch (UserNotFound_Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        } catch (DuplicateFault_Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Duplicate transaction").build();
        } catch (AmountNotPositive_Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Amount not positive").build();
        } catch (DatabaseException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Amount not positive").build();

        }
    }

    @Override
    public Response fill(String jsonString) {
        Connection connection;
        try {


            connection = DatabaseManager.getDatabaseConnection();
            InsertDao insert = new Insert(connection);
            JSONObject jsonObject = new JSONObject(jsonString);
            String pay_id = jsonObject.getString("pay_id");
            int user_id = jsonObject.getInt("user_id");
            double amo = jsonObject.getDouble("amount");
            int tr_id = jsonObject.getInt("transaction_id");
            int code = jsonObject.getInt("code");
            int status = jsonObject.getInt("status");

            return Response.status(Response.Status.OK).entity(insert.fill(pay_id, user_id, amo, tr_id, code, status)).build();
//            return Response.status(Response.Status.OK).entity(connection).build();
        } catch (DatabaseException | SQLException e) {
        return Response.status(Response.Status.OK).entity(e).build();
        }
    }

}
