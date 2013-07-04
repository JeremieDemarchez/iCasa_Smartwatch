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
package fr.liglab.adele.icasa.dimmer.light.follow.me;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.light.Photometer;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.util.EmptyDeviceListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DimmerFollowMeApplication extends EmptyDeviceListener {

    /**
     * Field for dimmerLight dependency
     */
    private DimmerLight[] dimmerLights;
    /**
     * Field for presenceSensor dependency
     */
    private PresenceSensor[] presenceSensors;
    /**
     * Field for photometer dependency
     */
    private Photometer[] photometers;

    public static String LOCATION_PROPERTY_NAME = "Location";
    public static String LOCATION_UNKNOWN = "unknown";

    /**
     * Constant for illuminance *
     */
    public static double EXPECTED_MAX_ILLUMINANCE = 4000.0;
    public static double EXPECTED_MEAN_ILLUMINANCE = 2750.0;
    public static double EXPECTED_MIN_ILLUMINANCE = 1500.0;

    /**
     * Constant for each room surface.
     * Note : In the end this will be configurable !
     */

    public static double SURFACE_BATHROOM = 17.836;
    public static double SURFACE_LIVINGROOM = 16.807;
    public static double SURFACE_KITCHEN = 10.0842;

    private static boolean flag = false;

    /**
     * Bind Method for null dependency
     */
    public void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
        presenceSensor.addListener(this);
    }

    /**
     * Unbind Method for null dependency
     */
    public void unbindPresenceSensor(PresenceSensor presenceSensor,
                                     Map properties) {
        presenceSensor.removeListener(this);
    }

    /**
     * Bind Method for null dependency
     */
    public void bindPhotometer(Photometer photometer, Map properties) {
        photometer.addListener(this);
    }

    /**
     * Unbind Method for null dependency
     */
    public void unbindPhotometer(Photometer photometer, Map properties) {
        photometer.removeListener(this);
    }

    /**
     * Bind Method for null dependency
     */
    public void bindDimmerLight(DimmerLight dimmerLight, Map properties) {
        // do nothing
    }

    /**
     * Unbind Method for null dependency
     */
    public void unbindDimmerLight(DimmerLight dimmerLight, Map properties) {
        // do nothing
    }

    /**
     * Component Lifecycle Method
     */
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
        for (Photometer photometer : photometers) {
            photometer.removeListener(this);
        }
    }

    /**
     * Component Lifecycle Method
     */
    public void start() {
        // do nothing
    }

    /**
     * Method which catch all dimmerlight from a location
     *
     * @param location
     * @return
     */
    public List<DimmerLight> getDimmerLightFromLocation(String location) {
        List<DimmerLight> dimmerInLocation = new ArrayList<DimmerLight>();
        for (DimmerLight dimmer : dimmerLights) {
            if (dimmer.getPropertyValue(LOCATION_PROPERTY_NAME)
                    .equals(location)) {
                dimmerInLocation.add(dimmer);
            }
        }
        return dimmerInLocation;
    }

    /**
     * Method which catch all photometer from a location
     *
     * @param location
     * @return
     */
    public List<Photometer> getPhotometerFromLocation(String location) {
        List<Photometer> photometersLocation = new ArrayList<Photometer>();
        for (Photometer photometer : photometers) {
            if (photometer.getPropertyValue(Photometer.LOCATION_PROPERTY_NAME).equals(location)) {
                photometersLocation.add(photometer);
            }
        }
        return photometersLocation;
    }

    /**
     * Method which compute the mean value of a list of photometers
     *
     * @param photometers collection of photometer devices
     * @return
     */
    public double meanPhotometer(Collection<Photometer> photometers) {
        double mean = 0, sum = 0;

        for (Photometer photometer : photometers) {
            sum = sum + photometer.getIlluminance();
        }
        mean = sum / (photometers.size());
        return mean;
    }

    /**
     * Method which allows to light a list of dimmerLight at a wanted value
     *
     * @param dimmer
     * @param meanIlluminanceValue
     * @param profilIlluminance
     * @param roomSurface
     */
    public void setDimmerPowerLevelOnValue(List<DimmerLight> dimmer, double meanIlluminanceValue, int profilIlluminance, double roomSurface) {

        double diffIllumiance = 0;
        double diffPowerLevel = 0;
        double powerLevel = 0;

        switch (profilIlluminance) {

            //Profil 1 : soft lighting
            case 1: {
                diffIllumiance = (EXPECTED_MIN_ILLUMINANCE - meanIlluminanceValue);
            }
            break;

            //Profil 2 : atmosphere to watch a film
            case 2: {
                diffIllumiance = (EXPECTED_MEAN_ILLUMINANCE - meanIlluminanceValue);
            }
            break;

            //Profil 3 : Full lighting
            case 3: {
                diffIllumiance = (EXPECTED_MAX_ILLUMINANCE - meanIlluminanceValue);
            }
            break;

            default:
                break;

        }

        //Compute the difference of powerLevel to have the expected illuminance
        diffPowerLevel = (diffIllumiance * roomSurface) / 68000.0;

        //Set the powerLevel to have the expected illuminance
        for (DimmerLight dimmerlight : dimmer) {

            double currentPowerLevel = dimmerlight.getPowerLevel();
            powerLevel = currentPowerLevel + diffPowerLevel;

            //Clipping function
            if (powerLevel > 1.0) powerLevel = 1.0;
            else if (powerLevel < 0.0) powerLevel = 0.0;
            else ;

            dimmerlight.setPowerLevel(powerLevel);
        }
    }

    /**
     * Method which allows to light off a list of dimmerLight
     *
     * @param dimmer
     */
    public void setDimmerOff(List<DimmerLight> dimmer) {
        for (DimmerLight dimmerlight : dimmer) {
            dimmerlight.setPowerLevel(0);
        }
    }

    @Override
    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
        if (device instanceof Photometer) {
            Photometer photometerActiv = (Photometer) device;
            if (photometerActiv != null && propertyName.equals(Photometer.PHOTOMETER_CURRENT_ILLUMINANCE)) {
                String detectorLocation = (String) photometerActiv.getPropertyValue(LOCATION_PROPERTY_NAME);
                double meanPhotometerValue = 0;

                if (!detectorLocation.equals(LOCATION_UNKNOWN)) {

                    List<DimmerLight> dimmerInLocation = getDimmerLightFromLocation(detectorLocation);
                    List<Photometer> photometerInLocation = getPhotometerFromLocation(detectorLocation);
                    double surface = 0;
                    if ("livingroom".equals(detectorLocation)) {
                        surface = SURFACE_LIVINGROOM;
                    }
                    if ("bathroom".equals(detectorLocation)) {
                        surface = SURFACE_BATHROOM;
                    }
                    if ("kitchen".equals(detectorLocation)) {
                        surface = SURFACE_KITCHEN;
                    }

                    if (dimmerInLocation.isEmpty() != true && photometerInLocation.isEmpty() != true && flag == true) {
                        meanPhotometerValue = 0.0;
                        meanPhotometerValue = meanPhotometer(photometerInLocation);
                        setDimmerPowerLevelOnValue(dimmerInLocation, meanPhotometerValue, 2, surface);
                    }
                }
            }
        } else if (device instanceof PresenceSensor) {
            PresenceSensor sensorActiv = (PresenceSensor) device;
            if (sensorActiv != null && propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
                String detectorLocation = (String) sensorActiv.getPropertyValue(LOCATION_PROPERTY_NAME);

                double meanPhotometer = 0;

                if (!detectorLocation.equals(LOCATION_UNKNOWN)) {

                    List<DimmerLight> dimmerInLocation = getDimmerLightFromLocation(detectorLocation);
                    List<Photometer> photometerInLocation = getPhotometerFromLocation(detectorLocation);
                    double surface = 0;
                    if ("livingroom".equals(detectorLocation)) {
                        surface = SURFACE_LIVINGROOM;
                    }
                    if ("bathroom".equals(detectorLocation)) {
                        surface = SURFACE_BATHROOM;
                    }
                    if ("kitchen".equals(detectorLocation)) {
                        surface = SURFACE_KITCHEN;
                    }
                    if (dimmerInLocation.isEmpty() != true && photometerInLocation.isEmpty() != true && (Boolean) oldValue == false) {
                        meanPhotometer = meanPhotometer(photometerInLocation);
                        setDimmerPowerLevelOnValue(dimmerInLocation, meanPhotometer, 2, surface);
                        flag = true;
                    }
                    if ((Boolean) oldValue == true) {
                        flag = false;
                        setDimmerOff(dimmerInLocation);
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
