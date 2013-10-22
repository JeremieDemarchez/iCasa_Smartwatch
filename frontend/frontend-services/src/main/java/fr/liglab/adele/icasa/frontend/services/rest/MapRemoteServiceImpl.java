package fr.liglab.adele.icasa.frontend.services.rest;

import fr.liglab.adele.icasa.frontend.services.MapService;
import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.remote.impl.iCasaREST;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONException;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Service implementation to retrieve available maps in the frontend service.
 * User: garciai@imag.fr
 * Date: 10/21/13
 * Time: 1:09 PM
 */
@Component(name = "remote-frontend-icasa")
@Instantiate(name = "remote-frontend-icasa-0")
@Provides(specifications = { iCasaREST.class }, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value = "/frontend")
public class MapRemoteServiceImpl extends AbstractREST {

     @Requires
     MapService mapService;

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    public Response versionOptions() {
        return makeCORS(Response.ok());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/maps")
    public Response getMaps() {
        String info = null;

        return makeCORS(Response.ok(info));
    }



}
