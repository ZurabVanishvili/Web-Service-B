package ge.ufc.webservices.ws;

import ge.ufc.webservices.*;
import ge.ufc.webservices.dao.DatabaseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("users")
public interface AgentService {

    @GET
    @Path("/getUser/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    Response userByID(@PathParam("id") int id);

    @POST
    @Path("/fillBalance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response fillBalance(String jsonString) throws SQLException, AgentAccessDenied_Exception, UserNotFound_Exception, DuplicateFault_Exception, AgentAuthFailed_Exception, AmountNotPositive_Exception, DatabaseException_Exception, DatabaseException, TransactionNotFound_Exception;
//
//
//    @POST
//    @Path("/fill")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    Response fill(String jsonString);

    @GET
    @Path("/quartz")
    Response quartz();

}
