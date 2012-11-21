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
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.chameleon.json.JSONService;

import java.util.*;
import javax.ws.rs.*;
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
                .header("Pragma", "no-cache");

        if (!"".equals(returnMethod)) {
            rb.header("Access-Control-Allow-Headers", returnMethod);
        }

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
            deviceJSON.put("location", device.getLocation());
            deviceJSON.put("state", device.getState());
            deviceJSON.put("type", deviceType);
            deviceJSON.put("positionX", devicePosition.x);
            deviceJSON.put("positionY", devicePosition.y);
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

    @GET
    @Produces("application/json")
    @Path(value="/deviceTypes/")
    public Response deviceTypes() {
        return makeCORS(Response.ok(getDeviceTypes()));
    }

    @GET
    @Produces("application/json")
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
    @Produces("application/json")
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

    /**
     * Create a new device.
     *
     * @param deviceId
     * @param deviceName
     * @param fault
     * @param location
     * @param state
     * @return
     */
    @POST
    @Produces("application/json")
    @Path(value="/device/{deviceId}")
    public Response createDevice(@PathParam("deviceId") String deviceId, @FormParam("type") String type,
                             @FormParam("name") String deviceName, @FormParam("fault") String fault,
                             @FormParam("location") String location, @FormParam("state") String state) {

        Factory deviceFactory = getDeviceFactory(type);

        GenericDevice newDevice = null;
        if (deviceFactory != null) {
            // Generate a serial number
            Random m_random = new Random();
            String serialNumber = Long.toString(m_random.nextLong(), 16);
            // Create the device
            Dictionary<String, String> properties = new Hashtable<String, String>();
            properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);
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
    @Produces("application/json")
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
