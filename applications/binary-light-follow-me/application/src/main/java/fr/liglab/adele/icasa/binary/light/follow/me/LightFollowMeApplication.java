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
package fr.liglab.adele.icasa.binary.light.follow.me;

import java.util.ArrayList;
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

    /** Field for binaryLights dependency */
    private BinaryLight[] binaryLights;

    /** Field for presenceSensors dependency */
    private PresenceSensor[] presenceSensors;

    /** Field for powerSwitches dependency */
    private PowerSwitch[] powerSwitches;



    /** Bind Method for null dependency */
    public void bindPowerSwitch(PowerSwitch powerSwitch, Map properties) {
        powerSwitch.addListener(this);
    }

    /** Unbind Method for null dependency */
    public void unbindPowerswitch(PowerSwitch powerSwitch, Map properties) {
        powerSwitch.removeListener(this);
    }

    /** Bind Method for null dependency */
    public void bindBinaryLight(BinaryLight binaryLight, Map properties) {
        //do nothing
    }

    /** Unbind Method for null dependency */
    public void unbindBinaryLight(BinaryLight binaryLight, Map properties) {
        //do nothing
    }

    /** Bind Method for null dependency */
    public void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
        presenceSensor.addListener(this);
    }

    /** Unbind Method for null dependency */
    public void unbindPrensenceSensor(PresenceSensor presenceSensor,Map properties) {
        presenceSensor.removeListener(this);
    }

    /** Component Lifecycle Method */
    public void stop() {
        /*
		 * It is extremely important to unregister the device listener.
		 * Otherwise, iCASA will continue to send notifications to the
		 * unpredictable and invalid component instance.
		 * This will also causes problem when the bundle is stopped as iCASA
		 * will still hold a reference on the device listener object.
		 * Consequently, it (and its bundle) won't be garbage collected
		 * causing a memory issue known as stale reference.
		 */
        for (PresenceSensor presenceSensor : presenceSensors) {
            presenceSensor.removeListener(this);
        }
        for(PowerSwitch powerSwitch : powerSwitches) {
            powerSwitch.removeListener(this);
        }
    }

    /** Component Lifecycle Method */
    public void start() {
        // do nothing
    }

    /**
     * Return all BinaryLight from the given location
     *
     * @param location
     *            : the given location
     * @return the list of matching BinaryLights
     */
    private synchronized List<BinaryLight> getBinaryLightFromLocation(String location) {
        List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
        for (BinaryLight binLight : binaryLights) {
            if (binLight.getPropertyValue(BinaryLight.LOCATION_PROPERTY_NAME).equals(location)) {
                binaryLightsLocation.add(binLight);
            }
        }
        return binaryLightsLocation;
    }

    @Override
    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue) {

        if (device instanceof PowerSwitch) {

            PowerSwitch changingSensor = (PowerSwitch) device;
            // check the change is related to presence sensing
            if (propertyName.equals(PowerSwitch.POWER_SWITCH_CURRENT_STATUS)) {
                // get the location where the sensor is:
                String detectorLocation = (String) changingSensor
                        .getPropertyValue(BinaryLight.LOCATION_PROPERTY_NAME);
                // if the location is known :
                if (!detectorLocation.equals(BinaryLight.LOCATION_UNKNOWN)) {
                    // get the related binary lights
                    List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(detectorLocation);
                    for (BinaryLight binaryLight : sameLocationLigths) {

                        // and switch them on/off depending on the switch state
                        binaryLight.setPowerStatus(!(Boolean) oldValue);
                    }
                }
            }
        } else if (device instanceof PresenceSensor) {

            PresenceSensor changingSensor = (PresenceSensor) device;
            // check the change is related to presence sensing
            if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
                // get the location where the sensor is:
                String detectorLocation = (String) changingSensor
                        .getPropertyValue(BinaryLight.LOCATION_PROPERTY_NAME);
                // if the location is known :
                if (!detectorLocation.equals(BinaryLight.LOCATION_UNKNOWN)) {
                    // get the related binary lights
                    List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(detectorLocation);
                    for (BinaryLight binaryLight : sameLocationLigths) {

                        // and switch them on/off depending on the sensed presence
                        binaryLight.setPowerStatus(!(Boolean) oldValue);
                    }
                }
            }
        }
    }

    @Override
    public void deviceAdded(GenericDevice device) {
        // This method is not used in this tutorial but has to be implemented to
        // implement DeviceListeners
    }

    @Override
    public void devicePropertyAdded(GenericDevice arg0, String arg1) {
        // This method is not used in this tutorial but has to be implemented to
        // implement DeviceListeners
    }

    @Override
    public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
        // This method is not used in this tutorial but has to be implemented to
        // implement DeviceListeners
    }

    @Override
    public void deviceRemoved(GenericDevice arg0) {
        // This method is not used in this tutorial but has to be implemented to
        // implement DeviceListeners
    }
}
