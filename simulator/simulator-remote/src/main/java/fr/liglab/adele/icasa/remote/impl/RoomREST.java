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

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulatedEnvironment;
import fr.liglab.adele.icasa.environment.SimulationManager.Zone;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;
import org.apache.felix.ipojo.Factory;
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
@Component(name="remote-rest-room")
@Instantiate(name="remote-rest-room-0")
@Provides(specifications={RoomREST.class})
@Path(value="/rooms/")
public class RoomREST {

    @Requires
    SimulationManager _simulationMgr;

    /*
     * Methods to manage cross domain requests
     */
    private String _corsHeaders;

    private Response makeCORS(Response.ResponseBuilder req, String returnMethod) {
        Response.ResponseBuilder rb = req
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        if (!"".equals(returnMethod)) {
            rb.header("Access-Control-Allow-Headers", returnMethod);
        }

        return rb.build();
    }

    private Response makeCORS(Response.ResponseBuilder req) {
        return makeCORS(req, _corsHeaders);
    }

    private JSONObject getRoomJSON(String roomId, Zone zone) {
        JSONObject roomJSON = null;
        try {
            roomJSON = new JSONObject();
            roomJSON.putOnce("id", roomId);
            roomJSON.putOnce("name", roomId);
            roomJSON.put("leftX", zone.leftX);
            roomJSON.put("topY", zone.topY);
            roomJSON.put("rightX", zone.rightX);
            roomJSON.put("bottomY", zone.bottomY);
        } catch (JSONException e) {
            e.printStackTrace();
            roomJSON = null;
        }

        return roomJSON;
    }

    @GET
    @Produces("application/json")
    @Path(value="/rooms/")
    public Response devices() {
        return makeCORS(Response.ok(getRooms()));
    }

    /**
     * Returns a JSON array containing all devices.
     *
     * @return a JSON array containing all devices.
     */
    public String getRooms() {
        boolean atLeastOne = false;
        JSONArray currentRooms = new JSONArray();
        for (String envId : _simulationMgr.getEnvironments()) {
            Zone zone = _simulationMgr.getEnvironmentZone(envId);
            if (zone == null)
                continue;

            JSONObject roomJSON = getRoomJSON(envId, zone);
            if (roomJSON == null)
                continue;

            currentRooms.put(roomJSON);
        }

        return currentRooms.toString();
    }

}
