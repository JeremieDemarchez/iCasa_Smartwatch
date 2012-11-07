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
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.chameleon.json.JSONService;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.text.ParseException;

import org.json.*;

/**
 * @author Thomas Leveque
 *
 */
@Component(name="remote-rest-device")
@Instantiate(name="remote-rest-device-0")
@Provides(specifications={DeviceREST.class})
@Path(value="/device/{deviceId}")
public class DeviceREST {

    @Requires(optional=true)
    GenericDevice[] _devices;

    @Requires(optional=true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.environment.SimulatedDevice)")
    private Factory[] _deviceFactories;

    /**
     * Returns a JSON array containing all devices.
     *
     * @return a JSON array containing all devices.
     */
    public String getDeviceIds() {
        boolean atLeastOne = false;
        JSONArray currentDevices = new JSONArray();
        for (GenericDevice device : _devices) {
            JSONObject deviceJSON = getDeviceJSON(device);
            if (currentDevices == null)
                continue;

            currentDevices.put(deviceJSON);
        }

        return currentDevices.toString();
    }

    private JSONObject getDeviceJSON(GenericDevice device) {
        JSONObject deviceJSON = null;
        try {
            deviceJSON = new JSONObject();
            deviceJSON.putOnce("id", device.getSerialNumber());
            deviceJSON.putOnce("name", device.getSerialNumber());
            deviceJSON.put("fault", device.getFault());
            deviceJSON.put("location", device.getLocation());
            deviceJSON.put("state", device.getState());
        } catch (JSONException e) {
            e.printStackTrace();
            deviceJSON = null;
        }

        return deviceJSON;
    }

    @GET
    @Produces("text/plain")
    public String get() {
        return "<html><body>toto</body></html>";
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
    public Response device(@PathParam("deviceId") String deviceId) {
        if (deviceId == null || deviceId.length()<1){
            return Response.ok(getDeviceIds()).build();
        }

        GenericDevice foundDevice = findDevice(deviceId);
        if (foundDevice == null) {
            return Response.status(404).build();
        } else {
            JSONObject foundDeviceJSON = getDeviceJSON(foundDevice);

            return Response.ok(foundDeviceJSON).build();
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        JSONObject newDeviceJSON = getDeviceJSON(newDevice);

        return Response.ok(newDevice.toString()).build(); //TODO check that newDevice must be included in the response body
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
    public Response deleteDevice(@PathParam("deviceId") String deviceId) {

        GenericDevice foundDevice = findDevice(deviceId);
        if (foundDevice == null)
            return Response.status(404).build();
        if (foundDevice instanceof Pojo) {
            Pojo pojo = (Pojo) foundDevice;
            pojo.getComponentInstance().dispose();
        } else
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        return Response.ok().build();
    }

}
