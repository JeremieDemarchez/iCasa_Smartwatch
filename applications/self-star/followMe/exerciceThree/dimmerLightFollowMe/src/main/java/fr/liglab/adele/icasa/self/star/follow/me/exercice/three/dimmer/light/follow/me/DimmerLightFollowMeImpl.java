package fr.liglab.adele.icasa.self.star.follow.me.exercice.three.dimmer.light.follow.me;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.apache.felix.ipojo.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */

@Component(name="DimmerLightFollowMe")
@Instantiate
@Provides(specifications = FollowMeConfiguration.class)
public class DimmerLightFollowMeImpl implements DeviceListener,FollowMeConfiguration {
    /**
     * The maximum number of lights to turn on when a user enters the room :
     **/
    private int maxLightsToTurnOnPerRoom = 2;

    /**
     * The name of the LOCATION property
     */
    public static final String LOCATION_PROPERTY_NAME = "Location";

    /**
     * The name of the location for unknown value
     */
    public static final String LOCATION_UNKNOWN = "unknown";

    /** Field for binaryLights dependency */
    @RequiresDevice(id="binaryLights", type="field", optional=true)
    private BinaryLight[] binaryLights;

    /** Field for presenceSensors dependency */
    @RequiresDevice(id="presenceSensors", type="field", optional=true)
    private PresenceSensor[] presenceSensors;

    /** Field for presenceSensors dependency */
    @RequiresDevice(id="dimmerLights", type="field", optional=true)
    private DimmerLight[] dimmerLights;

    /**
     * Bind Method for binaryLights dependency.
     * This method is not mandatory and implemented for debug purpose only.
     */
    @RequiresDevice(id="binaryLights", type="bind")
    public void bindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
        System.out.println("bind binary light " + binaryLight.getSerialNumber());
        binaryLight.addListener(this);
    }

    /**
     * Unbind Method for binaryLights dependency.
     * This method is not mandatory and implemented for debug purpose only.
     */
    @RequiresDevice(id="binaryLights", type="unbind")
    public void unbindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
        System.out.println("unbind binary light " + binaryLight.getSerialNumber());
        binaryLight.removeListener(this);
    }

    /**
     * Bind Method for PresenceSensors dependency.
     * This method will be used to manage device listener.
     */
    @RequiresDevice(id="presenceSensors", type="bind")
    public void bindPresenceSensor(PresenceSensor presenceSensor, Map<Object, Object> properties) {
        System.out.println("bind presence sensor " + presenceSensor.getSerialNumber());
        presenceSensor.addListener(this);

    }

    /**
     * Unbind Method for PresenceSensors dependency.
     * This method will be used to manage device listener.
     */
    @RequiresDevice(id="presenceSensors", type="unbind")
    public void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
        System.out.println("Unbind presence sensor "+ presenceSensor.getSerialNumber());
        presenceSensor.removeListener(this);
    }

    /**
     * Bind Method for binaryLights dependency.
     * This method is not mandatory and implemented for debug purpose only.
     */
    @RequiresDevice(id="dimmerLights", type="bind")
    public void bindDimmerLight(DimmerLight dimmerLight, Map<Object, Object> properties) {
        System.out.println("bind dimmer light " + dimmerLight.getSerialNumber());
        dimmerLight.addListener(this);
    }

    /**
     * Unbind Method for binaryLights dependency.
     * This method is not mandatory and implemented for debug purpose only.
     */
    @RequiresDevice(id="dimmerLights", type="unbind")
    public void unbindDimmerLight(DimmerLight dimmerLight, Map<Object, Object> properties) {
        System.out.println("unbind dimmer light " + dimmerLight.getSerialNumber());
        dimmerLight.removeListener(this);
    }


    /** Component Lifecycle Method */
    @Invalidate
    public void stop() {
        System.out.println("Component is stopping...");
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
        for(BinaryLight binaryLight  : binaryLights) {
            binaryLight.removeListener(this);
        }

        for(DimmerLight dimmerLight  : dimmerLights) {
            dimmerLight.removeListener(this);
        }
    }

    /** Component Lifecycle Method */
    @Validate
    public void start() {
        System.out.println("Component is starting...");
    }

    @Override
    public void deviceAdded(GenericDevice device) {
        System.out.println("Device Added");
    }

    @Override
    public void deviceRemoved(GenericDevice device) {
        System.out.println("Device Removed");
    }


    /**
     * This method is part of the DeviceListener interface and is called when a
     * subscribed device property is modified.
     *
     * @param device
     *            is the device whose property has been modified.
     * @param propertyName
     *            is the name of the modified property.
     */
    @Override
    public void devicePropertyModified(GenericDevice device,String propertyName, Object oldValue, Object newValue) {

        System.out.println("Device Property Modified");
        //we assume that we listen only to presence sensor events (otherwise there is a bug)
        if (device instanceof PresenceSensor ){
            System.out.println("PRESENCE SENSOR ");
            PresenceSensor changingSensor = (PresenceSensor) device;
            presenceModified( changingSensor, propertyName,  oldValue,  newValue);
        }

        if (device instanceof BinaryLight ){
            System.out.println("Binary Light ");
            BinaryLight changingBinaryLight = (BinaryLight) device;
            binaryLightModified( changingBinaryLight, propertyName, oldValue,newValue);
        }

        if (device instanceof DimmerLight ){
            System.out.println("Dimmer Light ");
            DimmerLight changingDimmerLight = (DimmerLight) device;
            dimmerLightModified( changingDimmerLight, propertyName, oldValue,newValue);
        }
    }



    @Override
    public void devicePropertyAdded(GenericDevice device, String propertyName) {
        System.out.println("Device Property Added");
    }

    @Override
    public void devicePropertyRemoved(GenericDevice device, String propertyName) {
        System.out.println("Device Property Removed");
    }

    @Override
    public void deviceEvent(GenericDevice device, Object data) {
        System.out.println("Device Event");
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
            if (binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
                binaryLightsLocation.add(binLight);
            }
        }
        return binaryLightsLocation;
    }

    /**
     * Return all BinaryLight from the given location
     *
     * @param location
     *            : the given location
     * @return the list of matching BinaryLights
     */
    private synchronized List<DimmerLight> getDimmerLightFromLocation(String location) {
        List<DimmerLight> dimmerLightsLocation = new ArrayList<DimmerLight>();
        for (DimmerLight dimmerLight : dimmerLights) {
            if (dimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
                dimmerLightsLocation.add(dimmerLight);
            }
        }
        return dimmerLightsLocation;
    }

    private synchronized int getLightTurnOn(String location) {
        int countTurnOn= 0;
        //count binary light on
        countTurnOn += getBinaryLightTurnOn(getBinaryLightFromLocation(location));
        //count dimmer light on
        countTurnOn += getDimmerLightTurnOn(getDimmerLightFromLocation(location));
        return countTurnOn;
    }

    private synchronized int getBinaryLightTurnOn(List<BinaryLight> listBinaryLights) {
        int countTurnOn= 0;
        for (BinaryLight binLight : listBinaryLights) {
            if (binLight.getPowerStatus()) {
                countTurnOn +=1;
            }
        }
        return countTurnOn;
    }

    private synchronized int getDimmerLightTurnOn(List<DimmerLight> listDimmerLights) {
        int countTurnOn= 0;
        for (DimmerLight binLight : listDimmerLights) {
            if (binLight.getPowerLevel() > 0) {
                countTurnOn +=1;
            }
        }
        return countTurnOn;
    }

    private synchronized List<PresenceSensor> getPresenceSensorFromLocation(String location) {
        List<PresenceSensor> presenceSensorLocation = new ArrayList<PresenceSensor>();
        for (PresenceSensor presenceSensor : presenceSensors) {
            if (presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
                presenceSensorLocation.add(presenceSensor);
            }
        }
        return presenceSensorLocation;
    }

    private synchronized int setBinaryLightFromLocation(String location,int lightOn) {
        // get the related binary lights
        List<BinaryLight> oldSameLocationLigths = getBinaryLightFromLocation(location);
        for (BinaryLight binaryLight : oldSameLocationLigths) {
            if (lightOn < maxLightsToTurnOnPerRoom ){
                binaryLight.turnOn();
                lightOn ++;
            }else{
                binaryLight.turnOff();
            }
        }
        return lightOn;
    }

    private synchronized int setDimmerLightFromLocation(String location,int lightOn) {
        // get the related binary lights
        List<DimmerLight> oldSameLocationLigths = getDimmerLightFromLocation(location);

        for (DimmerLight dimmerLight : oldSameLocationLigths) {
            if (lightOn < maxLightsToTurnOnPerRoom ){
                dimmerLight.setPowerLevel(1);
                lightOn ++;
            }else{
                dimmerLight.setPowerLevel(0);
            }
        }
        return lightOn;
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

    private synchronized List<String> getLocationWherePresence() {
        List<String> listOfLocationWithPresence= new ArrayList<String>();
        List<String>  listOfLocationCheck= new ArrayList<String>();
        for(PresenceSensor presenceSensor : presenceSensors){
            String location = (String) presenceSensor.getPropertyValue(presenceSensor.LOCATION_PROPERTY_NAME);
            if (!listOfLocationCheck.contains(location)){
                if(presenceFromLocation(location)){
                    listOfLocationWithPresence.add(location);
                }
                listOfLocationCheck.add(location);
            }
        }
        return listOfLocationWithPresence;
    }

    private synchronized void turnOffLightsFromLocation(String location) {
        List<BinaryLight> binaryLightList = getBinaryLightFromLocation(location);
        for(BinaryLight binaryLight:binaryLightList){
            binaryLight.turnOff();
        }
        List<DimmerLight> dimmerLightList = getDimmerLightFromLocation(location);
        for(DimmerLight dimmerLight:dimmerLightList){
            dimmerLight.setPowerLevel(0);
        }
    }


    private synchronized void applyMaximumNumberOfLightTurnOn(List<String> locations) {
        for(String location : locations){
            //get the number off light already turn on
            int countLightOn = getLightTurnOn(location);

            // get the related binary lights
            List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(location);
            if(countLightOn < maxLightsToTurnOnPerRoom){
                int lightToTurnOn = maxLightsToTurnOnPerRoom-countLightOn;
                int lightTurnOn = 0;
                for (BinaryLight binaryLight : sameLocationLigths) {
                    //check if we can turn off more lights
                    if (lightTurnOn < lightToTurnOn ){
                        if (!binaryLight.getPowerStatus()){
                            binaryLight.turnOn();
                            lightTurnOn ++;
                        }
                    }
                    else{
                        break;
                    }
                }
                List<DimmerLight> sameLocationDimmerLigths = getDimmerLightFromLocation(location);
                for (DimmerLight dimmerLight : sameLocationDimmerLigths) {
                    //check if we can turn off more lights
                    if (lightTurnOn < lightToTurnOn ){
                        if (dimmerLight.getPowerLevel() == 0){
                            dimmerLight.setPowerLevel(1);
                            lightTurnOn ++;
                        }
                    }else{
                        break;
                    }
                }
            } else if(countLightOn > maxLightsToTurnOnPerRoom){
                int lightToTurnOff =countLightOn- maxLightsToTurnOnPerRoom;
                int lightTurnOff = 0;
                for (BinaryLight binaryLight : sameLocationLigths) {
                    //check if we can turn off more lights
                    if (lightTurnOff < lightToTurnOff ){
                        if (binaryLight.getPowerStatus()){
                            binaryLight.turnOff();
                            lightTurnOff ++;
                        }
                    }
                    else{
                        break;
                    }
                }
                List<DimmerLight> sameLocationDimmerLigths = getDimmerLightFromLocation(location);
                for (DimmerLight dimmerLight : sameLocationDimmerLigths) {
                    //check if we can turn off more lights
                    if (lightTurnOff < lightToTurnOff ){
                        if (dimmerLight.getPowerLevel() != 0){
                            dimmerLight.setPowerLevel(0);
                            lightTurnOff ++;
                        }
                    }else{
                        break;
                    }
                }
            }
        }
    }

    private synchronized void dimmerLightModified(DimmerLight changingDimmerLight,String propertyName, Object oldValue, Object newValue) {
        if (propertyName.equals(DimmerLight.LOCATION_PROPERTY_NAME)){
            String dimmerLightLocation = (String) changingDimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME);

            String oldLocation = (String) oldValue;

            if (!dimmerLightLocation.equals(LOCATION_UNKNOWN)) {
                if (presenceFromLocation(dimmerLightLocation)){
                    List<String> locations = new ArrayList<String>();
                    locations.add(dimmerLightLocation);
                    applyMaximumNumberOfLightTurnOn(locations);
                }
            }else{
                changingDimmerLight.setPowerLevel(0);
            }

            if (!oldLocation.equals(LOCATION_UNKNOWN)) {
                //check if in the old location have always a person
                if (presenceFromLocation(oldLocation)){
                    List<String> locations = new ArrayList<String>();
                    locations.add(oldLocation);
                    applyMaximumNumberOfLightTurnOn(locations);
                }
            }
        }
    }

    private synchronized void presenceModified(PresenceSensor changingSensor,String propertyName, Object oldValue, Object newValue) {
        // check the change is related to presence sensing
        if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
            // get the location where the sensor is:
            String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
            // if the location is known :
            if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
                if (presenceFromLocation(detectorLocation)){
                    List<String> locations = new ArrayList<String>();
                    locations.add(detectorLocation);
                    applyMaximumNumberOfLightTurnOn(locations);
                }else{
                    turnOffLightsFromLocation(detectorLocation);
                }
            }
        } else  if (propertyName.equals(PresenceSensor.LOCATION_PROPERTY_NAME)) {
            String detectorLightLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);

            String oldLocation = (String) oldValue;

            if (!detectorLightLocation.equals(LOCATION_UNKNOWN)) {
                if (presenceFromLocation(detectorLightLocation)){
                    List<String> locations = new ArrayList<String>();
                    locations.add(detectorLightLocation);
                    applyMaximumNumberOfLightTurnOn(locations);
                }
            }

            if (!oldLocation.equals(LOCATION_UNKNOWN)) {
                //check if in the old location have always a person
                if (presenceFromLocation(oldLocation)){
                    List<String> locations = new ArrayList<String>();
                    locations.add(oldLocation);
                    applyMaximumNumberOfLightTurnOn(locations);
                }else{
                    turnOffLightsFromLocation(oldLocation);
                }
            }
        }
    }

    private synchronized void binaryLightModified(BinaryLight changingBinaryLight,String propertyName, Object oldValue, Object newValue) {
        if (propertyName.equals(BinaryLight.LOCATION_PROPERTY_NAME)){

            String binaryLightLocation = (String) changingBinaryLight.getPropertyValue(LOCATION_PROPERTY_NAME);

            String oldLocation = (String) oldValue;

            if (!binaryLightLocation.equals(LOCATION_UNKNOWN)) {
                if (presenceFromLocation(binaryLightLocation)){
                    List<String> locations = new ArrayList<String>();
                    locations.add(binaryLightLocation);
                    applyMaximumNumberOfLightTurnOn(locations);
                }
            }else{
                changingBinaryLight.turnOff();
            }

            if (!oldLocation.equals(LOCATION_UNKNOWN)) {
                //check if in the old location have always a person
                if (presenceFromLocation(oldLocation)){
                    List<String> locations = new ArrayList<String>();
                    locations.add(oldLocation);
                    applyMaximumNumberOfLightTurnOn(locations);
                }
            }
        }
    }


    @Override
    public int getMaximumNumberOfLightsToTurnOn() {
        return maxLightsToTurnOnPerRoom;
    }

    @Override
    public void setMaximumNumberOfLightsToTurnOn(int maximumNumberOfLightsToTurnOn) {
        maxLightsToTurnOnPerRoom = maximumNumberOfLightsToTurnOn;
        List<String> listOflocation = getLocationWherePresence();
        applyMaximumNumberOfLightTurnOn(listOflocation);
    }
}
