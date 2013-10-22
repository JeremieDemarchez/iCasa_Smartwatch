package fr.liglab.adele.icasa.frontend.services.rest;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import fr.liglab.adele.icasa.frontend.services.MapService;
import fr.liglab.adele.icasa.frontend.services.utils.ICasaMap;
import fr.liglab.adele.icasa.frontend.services.utils.JSONMap;
import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.remote.impl.iCasaREST;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Random;

/**
 * Service implementation to retrieve available maps in the frontend service.
 * User: garciai@imag.fr
 * Date: 10/21/13
 * Time: 1:09 PM
 */
@Component
@Instantiate
@Provides(properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value = "/frontend/maps")
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
    public Response getMaps() {
        return makeCORS(Response.ok(getMapsInString()));
    }

    public String getMapsInString(){
        // boolean atLeastOne = false;
        JSONArray currentMaps = new JSONArray();
        for (ICasaMap map : mapService.getMaps()) {


            JSONObject mapJSON = null;
            try {
                mapJSON = JSONMap.toJSON(map);
            } catch (JSONException e) {
                mapJSON = null;
                e.printStackTrace();
            }
            if (mapJSON == null)
                continue;
            currentMaps.put(mapJSON);
        }

        return currentMaps.toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/image")
    public Response installApplication(
            @FormDataParam("name") String name,
            @FormDataParam("description") String description,
            @FormDataParam("gatewayURL") String gatewayURL,
            @FormDataParam("libs") String libs,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        return null;
    }

    private String generateId(String name){
        String mapId = name.replaceAll("[^A-Za-z0-9]", "");//remove non-alphanumeric char
        Random random = new Random();
        while (mapService.contains(mapId)){   //Synchronization issue, map could be aggregated meanwhile.
            mapId = mapId + random.nextInt(); //add a number.
        }
        return mapId;
    }

}
