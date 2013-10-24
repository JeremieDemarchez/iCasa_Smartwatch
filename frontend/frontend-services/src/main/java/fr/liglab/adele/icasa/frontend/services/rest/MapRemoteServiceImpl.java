package fr.liglab.adele.icasa.frontend.services.rest;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import fr.liglab.adele.icasa.frontend.services.MapService;
import fr.liglab.adele.icasa.frontend.services.utils.ICasaMap;
import fr.liglab.adele.icasa.frontend.services.utils.JSONMap;
import fr.liglab.adele.icasa.remote.AbstractREST;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

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
    public Response postMap(
            @FormDataParam("mapId") String id,
            @FormDataParam("mapName") String name,
            @FormDataParam("mapDescription") String description,
            @FormDataParam("gatewayURL") String gatewayURL,
            @FormDataParam("libs") String libs,
            @FormDataParam("picture") InputStream uploadedInputStream,
            @FormDataParam("picture") FormDataContentDisposition fileDetail,
            @HeaderParam("Referer") String refer) {

        ICasaMap map;
        System.out.println("id " + id);
        System.out.println("name " + name);
        System.out.println("description " + description);
        System.out.println("gatewayURL " + gatewayURL);
        System.out.println("libs " + libs);
        if(!id.isEmpty()){ //update map.
            map = handleUpdate(id,name,description,gatewayURL,libs);
            if(map == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }  else{ // new map.
            map = new ICasaMap(name, description, gatewayURL, libs);
            mapService.addMap(map);
        }



        if(uploadedInputStream != null){
            String fileURL = mapService.getLocation() + "/" + map.getImgFile();
            try {
                OutputStream newFileStream = new FileOutputStream(new File(fileURL));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = uploadedInputStream.read(buffer)) != -1) {
                    newFileStream.write(buffer, 0, len);
                }
                uploadedInputStream.close();
                newFileStream.close();
             } catch (IOException ioe) {
                ioe.printStackTrace();
                Response.serverError().build();
             }
        }

        System.out.println(refer);
        try {
            //we don't know if its an upload from simulator or dashboard, so we redirect to referer.
            return Response.seeOther(new URI(refer)).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return Response.serverError().build();
        }
    }

     private ICasaMap handleUpdate(String id, String name, String description, String gatewayURL, String libs){
         return mapService.updateMap(id, name, description, gatewayURL, libs);
     }


}
