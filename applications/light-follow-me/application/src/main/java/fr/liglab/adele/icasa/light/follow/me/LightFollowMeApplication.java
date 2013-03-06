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
package fr.liglab.adele.icasa.light.follow.me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;

public class LightFollowMeApplication implements DeviceListener {

    public static String LOCATION_PROPERTY_NAME = "Location";
    public static String LOCATION_UNKNOWN = "unknown";

    private Map<String, PresenceSensor> detectors;
    private List<BinaryLight> lights;
    private Map<String, PowerSwitch> switchs;

    /** Field for binaryLight dependency */
    private BinaryLight[] binaryLight;
    /** Field for presenceSensor dependency */
    private PresenceSensor[] presenceSensor;
    /** Field for powerswitch dependency */
    private PowerSwitch[] powerswitch;



    /** Bind Method for null dependency */
    public void bindPowerSwitch(PowerSwitch powerSwitch, Map properties) {
        powerSwitch.addListener(this);
        switchs.put(powerSwitch.getSerialNumber(), powerSwitch);
    }

    /** Unbind Method for null dependency */
    public void unbindPowerswitch(PowerSwitch powerSwitch, Map properties) {
        powerSwitch.removeListener(this);
        switchs.remove(powerSwitch.getSerialNumber());
    }

    /** Bind Method for null dependency */
    public void bindBinary(BinaryLight binaryLight, Map properties) {
        lights.add(binaryLight);
    }

    /** Unbind Method for null dependency */
    public void unbindBinary(BinaryLight binaryLight, Map properties) {
        lights.remove(binaryLight);
    }

    /** Bind Method for null dependency */
    public void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
        presenceSensor.addListener(this);
        detectors.put(presenceSensor.getSerialNumber(), presenceSensor);
    }

    /** Unbind Method for null dependency */
    public void unbindPrensenceSensor(PresenceSensor presenceSensor,Map properties) {
        presenceSensor.removeListener(this);
        detectors.remove(presenceSensor.getSerialNumber());
    }

    /** Component Lifecycle Method */
    public void stop() {
        detectors = null;
        lights = null;
        switchs = null;
    }

    /** Component Lifecycle Method */
    public void start() {
        detectors = new HashMap<String, PresenceSensor>();
        switchs = new HashMap<String, PowerSwitch>();
        lights = new ArrayList<BinaryLight>();
    }

    /**
     * Method which catch all BinaryLight from a room
     * @param location
     * @return List de BinaryLight
     */
    private List<BinaryLight> getBinaryLightFromLocation(String location) {
        List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
        for (BinaryLight binLight : lights) {
            if (binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
                binaryLightsLocation.add(binLight);
            }
        }
        return binaryLightsLocation;
    }

    @Override
    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue) {

        if (device instanceof PowerSwitch) {

            PowerSwitch switchActiv = switchs.get(device.getSerialNumber());
            if (switchActiv != null && propertyName.equals(PowerSwitch.POWERSWITCH_CURRENT_STATUS)) {
                String switchLocation = (String) switchActiv.getPropertyValue(LOCATION_PROPERTY_NAME);
                if (!switchLocation.equals(LOCATION_UNKNOWN)) {
                    List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(switchLocation);
                    for (BinaryLight binaryLight : sameLocationLigths) {
                        binaryLight.setPowerStatus(!(Boolean) oldValue);
                    }
                }
            }
        }
        else{

            PresenceSensor activSensor = detectors.get(device.getSerialNumber());
            if (activSensor != null	&& propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
                String detectorLocation = (String) activSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
                if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
                    List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(detectorLocation);
                    for (BinaryLight binaryLight : sameLocationLigths) {
                        binaryLight.setPowerStatus(!(Boolean) oldValue);
                    }
                }
            }
        }
    }

    @Override
    public void deviceAdded(GenericDevice device) {
        // TODO Auto-generated method stub
    }

    @Override
    public void devicePropertyAdded(GenericDevice arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deviceRemoved(GenericDevice arg0) {
        // TODO Auto-generated method stub

    }
}
