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

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulationManagerNew;
import fr.liglab.adele.icasa.script.executor.ScriptExecutor;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.chameleon.json.JSONService;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.text.ParseException;

import org.json.*;

/**
 * @author Thomas Leveque
 *
 */
@Component(name="remote-rest-device")
@Instantiate(name="remote-rest-device-0")
@Provides(specifications={DeviceREST.class})
@Path(value="/devices/")
public class DeviceREST {

    @Requires(optional=true, proxy = false)
    GenericDevice[] _devices;

    @Requires(optional=true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.environment.SimulatedDevice)")
    private Factory[] _deviceFactories;

    @Requires
    private SimulationManager _simulationMgr;

    /*
     * Methods to manage cross domain requests
     */
    private String _corsHeaders;

    private Response makeCORS(ResponseBuilder req, String returnMethod) {
        ResponseBuilder rb = req
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Expose-Headers", "X-Cache-Date, X-Atmosphere-tracking-id")
                .header("Access-Control-Allow-Headers","Origin, Content-Type, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-Tracking-id, X-Atmosphere-Transport")
                .header("Access-Control-Max-Age", "-1")
                .header("Pragma", "no-cache");

        return rb.build();
    }

    private Response makeCORS(ResponseBuilder req) {
        return makeCORS(req, _corsHeaders);
    }

    /**
     * Returns a JSON array containing all devices.
     *
     * @return a JSON array containing all devices.
     */
    public String getDevices() {
        boolean atLeastOne = false;
        JSONArray currentDevices = new JSONArray();
        for (GenericDevice device : _devices) {
            JSONObject deviceJSON = getDeviceJSON(device);
            if (deviceJSON == null)
                continue;

            currentDevices.put(deviceJSON);
        }

        return currentDevices.toString();
    }

    private JSONObject getDeviceJSON(GenericDevice device) {
        String deviceType = "undefined";
        if (device instanceof Pojo) {
            try {
                deviceType = ((Pojo) device).getComponentInstance().getFactory().getFactoryName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Position devicePosition = _simulationMgr.getDevicePosition(device.getSerialNumber());

        JSONObject deviceJSON = null;
        try {
            deviceJSON = new JSONObject();
            deviceJSON.putOnce("id", device.getSerialNumber());
            deviceJSON.putOnce("name", device.getSerialNumber());
            deviceJSON.put("fault", device.getFault());
            deviceJSON.put("location", device.getPropertyValue(SimulationManagerNew.LOCATION_PROP_NAME));
            deviceJSON.put("state", device.getState());
            deviceJSON.put("type", deviceType);
            if (devicePosition != null) {
                deviceJSON.put("positionX", devicePosition.x);
                deviceJSON.put("positionY", devicePosition.y);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            deviceJSON = null;
        }

        return deviceJSON;
    }

    private JSONObject getDeviceTypeJSON(Factory deviceFactory) {
        String deviceTypeName = deviceFactory.getName();

        JSONObject deviceTypeJSON = null;
        try {
            deviceTypeJSON = new JSONObject();
            deviceTypeJSON.putOnce("id", deviceTypeName);
            deviceTypeJSON.putOnce("name", deviceTypeName);
        } catch (JSONException e) {
            e.printStackTrace();
            deviceTypeJSON = null;
        }

        return deviceTypeJSON;
    }

    /**
     * Returns a JSON array containing all devices.
     *
     * @return a JSON array containing all devices.
     */
    public String getDeviceTypes() {
        boolean atLeastOne = false;
        JSONArray currentDevices = new JSONArray();
        for (Factory deviceFactory : _deviceFactories) {
            JSONObject deviceType = getDeviceTypeJSON(deviceFactory);
            if (deviceType == null)
                continue;

            currentDevices.put(deviceType);
        }

        return currentDevices.toString();
    }

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

        GenericDevice foundDevice = findDevice(deviceId);
        if (foundDevice == null) {
            return makeCORS(Response.status(404));
        } else {
            JSONObject foundDeviceJSON = getDeviceJSON(foundDevice);

            return makeCORS(Response.ok(foundDeviceJSON.toString()));
        }
    }

    private GenericDevice findDevice(String deviceId) {
        GenericDevice foundDevice = null;
        for (GenericDevice device : _devices) {
            if (device.getSerialNumber().equals(deviceId)) {
                foundDevice = device;
                break;
            }
        }
        return foundDevice;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/device/{deviceId}")
    public Response updatesDevice(@PathParam("deviceId") String deviceId, String content) {
        if (deviceId == null || deviceId.length()<1){
            return makeCORS(Response.status(404));
        }
        GenericDevice device = findDevice(deviceId);
        if (device == null){
            return makeCORS(Response.status(404));
        }

        DeviceJSON updatedDevice = DeviceJSON.fromString(content);
        if (updatedDevice != null) {
            updatedDevice.setId(deviceId);

            if (updatedDevice.getState() != null)
                device.setState(updatedDevice.getState());
            if (updatedDevice.getFault() != null)
                device.setFault(updatedDevice.getFault());
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
                _simulationMgr.setDeviceLocation(deviceId, updatedDevice.getLocation());
        }

        JSONObject deviceJSON = getDeviceJSON(device);

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

        Factory deviceFactory = getDeviceFactory(deviceJSON.getType());

        GenericDevice newDevice = null;
        if (deviceFactory != null) {
            // Generate a serial number
            Random m_random = new Random();
            String serialNumber = Long.toString(m_random.nextLong(), 16);
            // Create the device
            Dictionary<String, String> properties = new Hashtable<String, String>();
            properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceJSON.getId());
            properties.put(GenericDevice.STATE_PROPERTY_NAME, GenericDevice.STATE_ACTIVATED);
            properties.put(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_NO);
            //properties.put(Constants.SERVICE_DESCRIPTION, description);
            properties.put("instance.name", deviceFactory.getName() + "-" + serialNumber);
            try {
                newDevice = (GenericDevice) deviceFactory.createComponentInstance(properties);
            } catch (UnacceptableConfiguration e) {
                e.printStackTrace();
                newDevice = null;
            } catch (MissingHandlerException e) {
                e.printStackTrace();
                newDevice = null;
            } catch (ConfigurationException e) {
                e.printStackTrace();
                newDevice = null;
            }
        }

        if (newDevice == null)
            return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));

        JSONObject newDeviceJSON = getDeviceJSON(newDevice);

        return makeCORS(Response.ok(newDevice.toString())); //TODO check that newDevice must be included in the response body
    }

    private Factory getDeviceFactory(String deviceType) {
        Factory deviceFactory = null;
        for (Factory factory : _deviceFactories) {
            if (factory.getName().equals(deviceType))
                deviceFactory = factory;
        }

        return deviceFactory;
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

        GenericDevice foundDevice = findDevice(deviceId);
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
