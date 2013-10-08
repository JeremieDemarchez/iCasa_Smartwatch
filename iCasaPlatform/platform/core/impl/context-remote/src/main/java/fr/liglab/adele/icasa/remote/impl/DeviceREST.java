/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
import javax.ws.rs.core.Response.Status;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.remote.SimulatedDeviceManager;
import fr.liglab.adele.icasa.remote.util.DeviceJSON;
import fr.liglab.adele.icasa.remote.util.IcasaJSONUtil;

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
    private ContextManager _contextMgr;
    
    @Requires (optional = true) 
    SimulatedDeviceManager simulator;
    
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
    @Path(value="/simulatedDeviceTypes/")
    public Response getSimulatedDeviceTypesOptions() {
        return makeCORS(Response.ok());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/simulatedDeviceTypes/")
    public Response simulatedDeviceTypes() {
    	if(simulator == null) {
    		return makeCORS(Response.status(Status.SERVICE_UNAVAILABLE));
    	}
        return makeCORS(Response.ok(getSimulatedDeviceTypes()));
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
            JSONObject foundDeviceJSON = IcasaJSONUtil.getDeviceJSON(foundDevice, _contextMgr);

            return makeCORS(Response.ok(foundDeviceJSON.toString()));
        }
    }

    private LocatedDevice findDevice(String deviceId) {
        return _contextMgr.getDevice(deviceId);
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
                Position position = _contextMgr.getDevicePosition(deviceId);
                int newPosX = position.x;
                int newPosY = position.y;
                if (updatedDevice.getPositionX() != null)
                    newPosX = updatedDevice.getPositionX();
                if (updatedDevice.getPositionY() != null)
                    newPosY = updatedDevice.getPositionY();
                _contextMgr.setDevicePosition(deviceId, new Position(newPosX, newPosY));
            } else if (updatedDevice.getLocation() != null)
                _contextMgr.getDevice(device.getSerialNumber()).setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, updatedDevice.getLocation());
        }

        JSONObject deviceJSON = IcasaJSONUtil.getDeviceJSON(device, _contextMgr);

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
   
    	if(simulator == null) {
    		return makeCORS(Response.status(Status.SERVICE_UNAVAILABLE));
    	}
        DeviceJSON deviceJSON = DeviceJSON.fromString(content);

        String deviceType = deviceJSON.getType();

        LocatedDevice newDevice = null;
        if (deviceType != null) {

            String deviceId = deviceJSON.getId();
            
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);

            try {
            	simulator.createDevice(deviceType, deviceId, properties);
            } catch (Exception e) {
                e.printStackTrace();
                newDevice = null;
            }

            newDevice = _contextMgr.getDevice(deviceId);
        }

        if (newDevice == null)
            return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));

        JSONObject newDeviceJSON = IcasaJSONUtil.getDeviceJSON(newDevice, _contextMgr);

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

    	if(simulator == null) {
    		return makeCORS(Response.status(Status.SERVICE_UNAVAILABLE));
    	}
        LocatedDevice foundDevice = findDevice(deviceId);
        if (foundDevice == null)
            return Response.status(404).build();
        try {
            simulator.removeDevice(deviceId);
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
        for (String deviceTypeStr : _contextMgr.getDeviceTypes()) {
            JSONObject deviceType = IcasaJSONUtil.getDeviceTypeJSON(deviceTypeStr, _contextMgr);
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
    private String getSimulatedDeviceTypes() {
        JSONArray currentDevices = new JSONArray();
        for (String deviceTypeStr : simulator.getDeviceTypes()) {
            JSONObject deviceType = IcasaJSONUtil.getDeviceTypeJSON(deviceTypeStr, _contextMgr);
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
        for (LocatedDevice device : _contextMgr.getDevices()) {
            JSONObject deviceJSON =  IcasaJSONUtil.getDeviceJSON(device, _contextMgr);
            if (deviceJSON == null)
                continue;

            currentDevices.put(deviceJSON);
        }

        return currentDevices.toString();
    }

}
