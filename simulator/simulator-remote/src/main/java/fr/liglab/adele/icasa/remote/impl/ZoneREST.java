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

import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Zone;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
                .header("Pragma", "no-cache");

        if (!"".equals(returnMethod)) {
            rb.header("Access-Control-Allow-Headers", returnMethod);
        }

        return rb.build();
    }

    private Response makeCORS(Response.ResponseBuilder req) {
        return makeCORS(req, _corsHeaders);
    }

    private JSONObject getZoneJSON(String zoneId, Zone zone) {
        JSONObject zoneJSON = null;
        try {
            zoneJSON = new JSONObject();
            zoneJSON.putOnce("id", zoneId);
            zoneJSON.putOnce("name", zoneId);
            zoneJSON.put("leftX", zone.leftX);
            zoneJSON.put("topY", zone.topY);
            zoneJSON.put("rightX", zone.rightX);
            zoneJSON.put("bottomY", zone.bottomY);
            zoneJSON.put("isRoom", true); //TODO change it when Zone API will be improved
        } catch (JSONException e) {
            e.printStackTrace();
            zoneJSON = null;
        }

        return zoneJSON;
    }

    @GET
    @Produces("application/json")
    @Path(value="/zones/")
    public Response zones() {
        return makeCORS(Response.ok(getZones()));
    }

    /**
     * Returns a JSON array containing all devices.
     *
     * @return a JSON array containing all devices.
     */
    public String getZones() {
        boolean atLeastOne = false;
        JSONArray currentZones = new JSONArray();
        for (String envId : _simulationMgr.getEnvironments()) {
            Zone zone = _simulationMgr.getEnvironmentZone(envId);
            if (zone == null)
                continue;

            JSONObject zoneJSON = getZoneJSON(envId, zone);
            if (zoneJSON == null)
                continue;

            currentZones.put(zoneJSON);
        }

        return currentZones.toString();
    }

}
