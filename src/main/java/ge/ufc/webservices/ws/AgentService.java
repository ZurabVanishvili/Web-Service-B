package ge.ufc.webservices.ws;

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
    Response fillBalance(String jsonString) throws SQLException;
//
//
//    @POST
//    @Path("/fill")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    Response fill(String jsonString);


}
