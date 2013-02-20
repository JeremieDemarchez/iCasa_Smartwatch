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
package fr.liglab.adele.icasa.remote.impl;

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

import fr.liglab.adele.icasa.context.LocatedDevice;
import fr.liglab.adele.icasa.context.Position;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.remote.impl.util.IcasaJSONUtil;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;

/**
 * @author Thomas Leveque
 *
 */
@Component(name="remote-rest-device")
@Instantiate(name="remote-rest-device-0")
@Provides(specifications={DeviceREST.class}, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value="/devices/")
public class DeviceREST extends AbstractREST {

    @Requires
    private SimulationManager _simulationMgr;
    
    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/deviceTypes/")
    public Response getDeviceTypesOptions() {
        return makeCORS(Response.ok());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/deviceTypes/")
    public Response deviceTypes() {
        return makeCORS(Response.ok(getDeviceTypes()));
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/device/")
    public Response createsDeviceOptions() {
        return makeCORS(Response.ok());
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/device/{deviceId}")
    public Response updatesDeviceOptions(@PathParam("deviceId") String deviceId) {
        return makeCORS(Response.ok());
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/devices/")
    public Response getDevicesOptions() {
        return makeCORS(Response.ok());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/devices/")
    public Response devices() {
        return makeCORS(Response.ok(getDevices()));
    }

    /**
     * Retrieve a device.
     *
     * @param deviceId The ID of the device to retrieve
     * @return The required device,
     * return <code>null<code> if the device does not exist.
     * @throws ParseException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/device/{deviceId}")
    public Response device(@PathParam("deviceId") String deviceId) {
        if (deviceId == null || deviceId.length()<1){
            return makeCORS(Response.ok(getDevices()));
        }

        LocatedDevice foundDevice = findDevice(deviceId);
        if (foundDevice == null) {
            return makeCORS(Response.status(404));
        } else {
            JSONObject foundDeviceJSON = IcasaJSONUtil.getDeviceJSON(foundDevice, _simulationMgr);

            return makeCORS(Response.ok(foundDeviceJSON.toString()));
        }
    }

    private LocatedDevice findDevice(String deviceId) {
        return _simulationMgr.getDevice(deviceId);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/device/{deviceId}")
    public Response updatesDevice(@PathParam("deviceId") String deviceId, String content) {
        if (deviceId == null || deviceId.length()<1){
            return makeCORS(Response.status(404));
        }
        LocatedDevice device = findDevice(deviceId);
        if (device == null){
            return makeCORS(Response.status(404));
        }

        DeviceJSON updatedDevice = DeviceJSON.fromString(content);
        if (updatedDevice != null) {
            updatedDevice.setId(deviceId);

            if (updatedDevice.getState() != null)
                device.setPropertyValue(GenericDevice.STATE_PROPERTY_NAME, updatedDevice.getState());
            if (updatedDevice.getFault() != null)
                device.setPropertyValue(GenericDevice.FAULT_PROPERTY_NAME, updatedDevice.getFault());
            if ((updatedDevice.getPositionX() != null) || (updatedDevice.getPositionY() != null)) {
                Position position = _simulationMgr.getDevicePosition(deviceId);
                int newPosX = position.x;
                int newPosY = position.y;
                if (updatedDevice.getPositionX() != null)
                    newPosX = updatedDevice.getPositionX();
                if (updatedDevice.getPositionY() != null)
                    newPosY = updatedDevice.getPositionY();
                _simulationMgr.setDevicePosition(deviceId, new Position(newPosX, newPosY));
            } else if (updatedDevice.getLocation() != null)
                _simulationMgr.getDevice(device.getSerialNumber()).setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, updatedDevice.getLocation());
        }

        JSONObject deviceJSON = IcasaJSONUtil.getDeviceJSON(device, _simulationMgr);

        return makeCORS(Response.ok(deviceJSON.toString()));
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

        JSONObject newDeviceJSON = IcasaJSONUtil.getDeviceJSON(newDevice, _simulationMgr);

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
    
    
    /**
     * Returns a JSON array containing all devices.
     *
     * @return a JSON array containing all devices.
     */
    private String getDeviceTypes() {
        JSONArray currentDevices = new JSONArray();
        for (String deviceTypeStr : _simulationMgr.getDeviceTypes()) {
            JSONObject deviceType = IcasaJSONUtil.getDeviceTypeJSON(deviceTypeStr);
            if (deviceType == null)
                continue;

            currentDevices.put(deviceType);
        }

        return currentDevices.toString();
    }
    
    /**
     * Returns a JSON array containing all devices.
     *
     * @return a JSON array containing all devices.
     */
    private String getDevices() {
        //boolean atLeastOne = false;
        JSONArray currentDevices = new JSONArray();
        for (LocatedDevice device : _simulationMgr.getDevices()) {
            JSONObject deviceJSON =  IcasaJSONUtil.getDeviceJSON(device, _simulationMgr);
            if (deviceJSON == null)
                continue;

            currentDevices.put(deviceJSON);
        }

        return currentDevices.toString();
    }

}
