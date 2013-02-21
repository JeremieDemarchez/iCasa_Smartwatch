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
/**
 *
 */
package fr.liglab.adele.icasa.simulator.remote.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.remote.impl.DeviceJSON;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.remote.impl.util.IcasaSimulatorJSONUtil;

/**
 * @author Thomas Leveque
 *
 */
@Component(name="simulator-remote-rest-device")
@Instantiate(name="simulator-remote-rest-device-0")
@Provides(specifications={DeviceREST.class}, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value="/devices/")
public class DeviceREST extends AbstractREST {

    @Requires
    private SimulationManager _simulationMgr;
   

    private LocatedDevice findDevice(String deviceId) {
        return _simulationMgr.getDevice(deviceId);
    }

  

    /**
     * Create a new device.
     *
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/device/")
    public Response createDevice(String content) {

        DeviceJSON deviceJSON = DeviceJSON.fromString(content);

        String deviceType = deviceJSON.getType();

        LocatedDevice newDevice = null;
        if (deviceType != null) {

            String deviceId = deviceJSON.getId();
            
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);

            try {
                _simulationMgr.createDevice(deviceType, deviceId, properties);
            } catch (Exception e) {
                e.printStackTrace();
                newDevice = null;
            }

            newDevice = _simulationMgr.getDevice(deviceId);
        }

        if (newDevice == null)
            return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));

        JSONObject newDeviceJSON = IcasaSimulatorJSONUtil.getDeviceJSON(newDevice, _simulationMgr);

        return makeCORS(Response.ok(newDeviceJSON.toString()));
    }

    /**
     * Delete specified device.
     *
     * @param deviceId device id
     * @return true if chain is successful deleted, false if it does not exist.
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/device/{deviceId}")
    public Response deleteDevice(@PathParam("deviceId") String deviceId) {

        LocatedDevice foundDevice = findDevice(deviceId);
        if (foundDevice == null)
            return Response.status(404).build();
        try {
            _simulationMgr.removeDevice(deviceId);
        } catch (Exception e) {
            return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));
        }

        return makeCORS(Response.ok());
    }
    
  
}
