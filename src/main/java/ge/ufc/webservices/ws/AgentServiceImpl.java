package ge.ufc.webservices.ws;

import ge.ufc.webservices.*;
import ge.ufc.webservices.dao.DatabaseException;
import ge.ufc.webservices.dao.DatabaseManager;
import ge.ufc.webservices.model.*;
import ge.ufc.webservices.model.User;

import jakarta.xml.ws.WebServiceException;
import org.json.JSONObject;

import javax.ws.rs.core.Response;

import java.sql.Connection;


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
    public Response fillBalance(String jsonString) {

        UserService u = new UserServiceImpl();

        Connection connection;
        InsertDao insert = null;

        JSONObject jsonObject = new JSONObject(jsonString);
        String trans_id = null;
        int user_id = 0;
        double amount = 0;

        int sys_id = 0;
        try {
            connection = DatabaseManager.getDatabaseConnection();
            insert = new Insert(connection);
            trans_id = jsonObject.getString("agent_transaction_id");
            user_id = jsonObject.getInt("user_id");
            amount = jsonObject.getDouble("amount");
            sys_id = u.fillBalance(trans_id, user_id, amount);
            insert.fill(trans_id, user_id, amount, sys_id, 200, 0);

            return Response.status(Response.Status.OK).entity(sys_id).build();


        } catch (InternalError_Exception|WebServiceException  e) {
            insert.fill(trans_id, user_id, amount, sys_id, 500, 1);
            return Response.status(Response.Status.REQUEST_TIMEOUT).entity("Internal error").build();

        } catch (DuplicateFault_Exception e) {
            insert.fill(trans_id, user_id, amount, sys_id, 409, 2);
            return Response.status(Response.Status.CONFLICT).entity("Duplicate Fault").build();

        } catch (DatabaseException | DatabaseException_Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        } catch (AmountNotPositive_Exception e) {
            insert.fill(trans_id, user_id, amount, sys_id, 400, 2);
            return Response.status(Response.Status.BAD_REQUEST).entity("Internal error").build();

        } catch (UserNotFound_Exception | AgentAccessDenied_Exception | AgentAuthFailed_Exception e) {
            insert.fill(trans_id, user_id, amount, sys_id, 404, 2);
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();

        }
    }


}
