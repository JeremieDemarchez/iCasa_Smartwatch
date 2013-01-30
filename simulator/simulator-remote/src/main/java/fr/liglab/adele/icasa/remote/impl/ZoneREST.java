/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.remote.impl;

import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.Position;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.Zone;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Thomas Leveque
 */
@Component(name="remote-rest-zone")
@Instantiate(name="remote-rest-zone-0")
@Provides(specifications={ZoneREST.class})
@Path(value="/zones/")
public class ZoneREST {

    @Requires
    private SimulationManager _simulationMgr;

    /*
     * Methods to manage cross domain requests
     */
    private String _corsHeaders;

    private Response makeCORS(Response.ResponseBuilder req, String returnMethod) {
        Response.ResponseBuilder rb = req
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Expose-Headers", "X-Cache-Date, X-Atmosphere-tracking-id")
                .header("Access-Control-Allow-Headers","Origin, Content-Type, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-Tracking-id, X-Atmosphere-Transport")
                .header("Access-Control-Max-Age", "-1")
                .header("Pragma", "no-cache");

        return rb.build();
    }

    private Response makeCORS(Response.ResponseBuilder req) {
        return makeCORS(req, _corsHeaders);
    }

    public JSONObject getZoneJSON(Zone zone) {
        JSONObject zoneJSON = null;
        try {
      	   String zoneId = zone.getId(); 
            zoneJSON = new JSONObject();
            zoneJSON.putOnce("id", zoneId);
            zoneJSON.putOnce("name", zoneId);
            zoneJSON.put("leftX", zone.getLeftTopAbsolutePosition().x);
            zoneJSON.put("topY", zone.getLeftTopAbsolutePosition().y);
            zoneJSON.put("rightX", zone.getRightBottomAbsolutePosition().x);
            zoneJSON.put("bottomY", zone.getRightBottomAbsolutePosition().y);
            zoneJSON.put("isRoom", true); //TODO change it when Zone API will be improved
            
            JSONObject propObject = new JSONObject();
            for (String variable : zone.getVariableNames()) {
            	propObject.put(variable, zone.getVariableValue(variable));
            }
            zoneJSON.put("variables", propObject);
            
        } catch (JSONException e) {
            e.printStackTrace();
            zoneJSON = null;
        }

        return zoneJSON;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/zones/")
    public Response zones() {
        return makeCORS(Response.ok(getZones()));
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/zone/{zoneId}")
    public Response updatesZoneOptions(@PathParam("zoneId") String zoneId) {
        return makeCORS(Response.ok());
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/zone/")
    public Response createsZoneOptions() {
        return makeCORS(Response.ok());
    }

    /**
     * Retrieves a zone.
     *
     * @param zoneId The ID of the zone to retrieve
     * @return The required zone
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/zone/{zoneId}")
    public Response getZone(@PathParam("zoneId") String zoneId) {
        if (zoneId == null || zoneId.length()<1){
            return makeCORS(Response.ok(getZones()));
        }

        Zone zoneFound = _simulationMgr.getZone(zoneId);
        if (zoneFound == null) {
            return makeCORS(Response.status(404));
        } else {
            JSONObject zoneJSON = getZoneJSON(zoneFound);

            return makeCORS(Response.ok(zoneJSON.toString()));
        }
    }

    /**
     * Returns a JSON array containing all devices.
     *
     * @return a JSON array containing all devices.
     */
    public String getZones() {
        boolean atLeastOne = false;
        JSONArray currentZones = new JSONArray();
        for (String envId : _simulationMgr.getZoneIds()) {
            Zone zone = _simulationMgr.getZone(envId);
            if (zone == null)
                continue;

            JSONObject zoneJSON = getZoneJSON(zone);
            if (zoneJSON == null)
                continue;

            currentZones.put(zoneJSON);
        }

        return currentZones.toString();
    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/zone/{zoneId}")
    public Response updatesZone(@PathParam("zoneId") String zoneId, String content) {
       if (zoneId == null || zoneId.length()<1){
          return makeCORS(Response.ok(getZones()));
      }

      Zone zoneFound = _simulationMgr.getZone(zoneId);
      if (zoneFound == null) {
          return makeCORS(Response.status(404));
      } else {
          JSONObject zoneJSON = getZoneJSON(zoneFound);
          return makeCORS(Response.ok(zoneJSON.toString()));
      }
    }
    
 
    
}
