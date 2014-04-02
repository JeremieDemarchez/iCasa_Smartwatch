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

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(name="LightFollowMeApplication")
@Instantiate
public class LightFollowMeApplication implements DeviceListener {

    /** Field for binaryLights dependency */
    @RequiresDevice(id="binaryLights", type="field", optional=true)
    private BinaryLight[] binaryLights;

    /** Field for presenceSensors dependency */
    @RequiresDevice(id="presenceSensors", type="field", optional=true)
    private PresenceSensor[] presenceSensors;

    /** Field for powerSwitches dependency */
    @RequiresDevice(id="powerSwitches", type="field", optional=true)
    private PowerSwitch[] powerSwitches;


    /** Bind Method for null dependency */
    @RequiresDevice(id="powerSwitches", type="bind")
    public void bindPowerSwitch(PowerSwitch powerSwitch, Map properties) {
        System.out.println(" BIND OF A POWER SWITCH §§§");
        powerSwitch.addListener(this);
    }

    /** Unbind Method for null dependency */
    @RequiresDevice(id="powerSwitches", type="unbind")
    public void unbindPowerswitch(PowerSwitch powerSwitch, Map properties) {
        System.out.println(" UNBIND OF A POWER SWITCH §§§");
        powerSwitch.removeListener(this);
    }

    /**
     * Bind Method for binaryLights dependency.
     * This method is not mandatory and implemented for debug purpose only.
     */
    @RequiresDevice(id="binaryLights", type="bind")
    public void bindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
        binaryLight.addListener(this);
    }

    /**
     * Unbind Method for binaryLights dependency.
     * This method is not mandatory and implemented for debug purpose only.
     */
    @RequiresDevice(id="binaryLights", type="unbind")
    public void unbindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
        binaryLight.removeListener(this);
    }


    /** Bind Method for null dependency */
    @RequiresDevice(id="presenceSensors", type="bind")
    public void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
        presenceSensor.addListener(this);
    }

    /** Unbind Method for null dependency */
    @RequiresDevice(id="presenceSensors", type="unbind")
    public void unbindPrensenceSensor(PresenceSensor presenceSensor,Map properties) {
        presenceSensor.removeListener(this);
    }

    /** Component Lifecycle Method */
    @Invalidate
    public void stop() {
        /*
		 * It is extremely important to unregister the device listener.
		 * Otherwise, iCasa will continue to send notifications to the
		 * unpredictable and invalid component instance.
		 * This will also causes problem when the bundle is stopped as iCasa
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
        for(BinaryLight binaryLight  : binaryLights) {
            binaryLight.removeListener(this);
        }
    }

    /** Component Lifecycle Method */
    @Validate
    public void start() {
        // do nothing
    }

    @Override
    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
        System.out.println(" PROPERTY CHANGE  §§ " + device.getSerialNumber() + " PROPERTY " + propertyName +" VALUE NEW " +newValue);
        for(PowerSwitch powerSwitch : powerSwitches){
            System.out.println(" Power is " + powerSwitch.getSerialNumber());
        }
        if (device instanceof PowerSwitch) {
            System.out.println(" POWER SWITCH CHANGE §§ ");
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
            presencePropertyModified(changingSensor, propertyName, oldValue, newValue);

        } else if (device instanceof BinaryLight){
            BinaryLight changingBinaryLight = (BinaryLight) device;
            binaryPropertyModified(changingBinaryLight, propertyName, oldValue, newValue);
        }
    }

    public synchronized void presencePropertyModified(PresenceSensor changingSensor,String propertyName, Object oldValue, Object newValue) {
        if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
            // get the location where the sensor is:
            String detectorLocation = (String) changingSensor.getPropertyValue(PresenceSensor.LOCATION_PROPERTY_NAME);
            // if the location is known :
            if (!detectorLocation.equals(PresenceSensor.LOCATION_UNKNOWN)) {
                // get the related binary lights
                List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(detectorLocation);
                for (BinaryLight binaryLight : sameLocationLigths) {
                    // and switch them on/off depending on the sensed presence
                    binaryLight.setPowerStatus(!(Boolean) oldValue);
                }
            }
        }
    }

    public synchronized void binaryPropertyModified(BinaryLight changingBinaryLight,String propertyName, Object oldValue, Object newValue) {
        if (propertyName.equals(BinaryLight.LOCATION_PROPERTY_NAME)){
            String binaryLightLocation = (String) changingBinaryLight.getPropertyValue(BinaryLight.LOCATION_PROPERTY_NAME);
            if (!binaryLightLocation.equals(BinaryLight.LOCATION_UNKNOWN)) {
                if (presenceFromLocation((String)newValue)){
                    changingBinaryLight.turnOn();
                }else{
                    changingBinaryLight.turnOff();
                }
            }
            else{
                changingBinaryLight.turnOff();
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
    public void deviceEvent(GenericDevice device, Object data) {

    }

    @Override
    public void deviceRemoved(GenericDevice arg0) {
        // This method is not used in this tutorial but has to be implemented to
        // implement DeviceListeners
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

    private synchronized List<PresenceSensor> getPresenceSensorFromLocation(String location) {
        List<PresenceSensor> presenceSensorLocation = new ArrayList<PresenceSensor>();
        for (PresenceSensor presenceSensor : presenceSensors) {
            if (presenceSensor.getPropertyValue(PresenceSensor.LOCATION_PROPERTY_NAME).equals(location)) {
                presenceSensorLocation.add(presenceSensor);
            }
        }
        return  presenceSensorLocation;
    }

    private synchronized boolean presenceFromLocation(String location) {
        int switchOn = 0;
        int switchOff = 0;
        List<PresenceSensor> presenceSensorLocation = new ArrayList<PresenceSensor>();
        presenceSensorLocation = getPresenceSensorFromLocation(location);
        for (PresenceSensor presenceSensor : presenceSensorLocation) {
            if (presenceSensor.getSensedPresence()) {
                switchOn +=1;
            }
            else{
                switchOff +=1;
            }
        }
        if (switchOn > switchOff){
            return true;
        }
        else{
            return false;
        }
    }
}
