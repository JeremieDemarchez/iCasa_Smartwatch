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
package fr.liglab.adele.icasa.binary.light.follow.me.with.motion.sensor;

import java.util.*;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.clock.ClockListener;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.ZoneListener;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.apache.felix.ipojo.annotations.*;
import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.motion.MotionSensor;
import fr.liglab.adele.icasa.service.preferences.Preferences;

@Component(name = "LightFollowMeWithMotionSensorApplication")
@Instantiate
@Provides
public class LightFollowMeWithMotionSensorApplication implements DeviceListener,PeriodicRunnable,ZoneListener,ClockListener {


    private static long DEFAULT_TIMEOUT = 600000;

    private final Object m_lock ;

    private Map<String,Long> mapOfZone = new HashMap<String, Long>();
    @Requires
    private ContextManager _contextMgr;

    @Requires
    private Preferences preferences;

    public LightFollowMeWithMotionSensorApplication(){
        m_lock = new Object();
    }

    /** Field for binaryLights dependency */
    @RequiresDevice(id = "binaryLights", type = "field", optional = true)
    private BinaryLight[] binaryLights;

    /** Field for motionSensors dependency */
    @RequiresDevice(id = "motionSensors", type = "field", optional = true)
    private MotionSensor[] motionSensors;

    /** Bind Method for null dependency */
    @RequiresDevice(id = "motionSensors", type = "bind")
    public void bindMotionSensor(MotionSensor motionSensor, Map properties) {
        motionSensor.addListener(this);
    }

    /** Unbind Method for null dependency */
    @RequiresDevice(id = "motionSensors", type = "unbind")
    public void unbindMotionSensor(MotionSensor motionSensor, Map properties) {
        motionSensor.removeListener(this);
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


    @Requires
    private Clock clock;


    /** Component Lifecycle Method */
    @Invalidate
    public void stop() {
        /*
         * It is extremely important to unregister the device listener. Otherwise, iCasa will continue to send
         * notifications to the unpredictable and invalid component instance. This will also causes problem when the
         * bundle is stopped as iCasa will still hold a reference on the device listener object. Consequently, it (and
         * its bundle) won't be garbage collected causing a memory issue known as stale reference.
         */
        for (MotionSensor motionSensorSensor : motionSensors) {
            motionSensorSensor.removeListener(this);
        }
        for (BinaryLight binaryLight : binaryLights) {
            binaryLight.removeListener(this);
        }
        _contextMgr.removeListener(this);
    }

    /** Component Lifecycle Method */
    @Validate
    public void start() {
        _contextMgr.addListener(this);
        synchronized (m_lock){
            Set<String> zoneIds = _contextMgr.getZoneIds();
            for(String location : zoneIds){
                mapOfZone.put(location,clock.currentTimeMillis());
            }
        }

    }



    /**
     * Motion sensor will trigger a deviceEvent call.
     *
     * @param device the motion sensor detecting the movement.
     * @param data
     */
    public void deviceEvent(GenericDevice device, Object data) {
        String location = String.valueOf(device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME));
        synchronized (m_lock){
            setOnAllLightsInLocation(location);
            mapOfZone.put(location,clock.currentTimeMillis());
        }
    }

    /**
     * Return all BinaryLight from the given location
     *
     * @param location
     *            : the given location
     * @return the list of matching BinaryLights
     */
    private  List<BinaryLight> getBinaryLightFromLocation(String location) {
        List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
        for (BinaryLight binLight : binaryLights) {
            if (binLight.getPropertyValue(BinaryLight.LOCATION_PROPERTY_NAME).equals(location)) {
                binaryLightsLocation.add(binLight);
            }
        }
        return binaryLightsLocation;
    }

    private  void setOffAllLightsInLocation(String location) {

        List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(location);
        for(BinaryLight binaryLight : sameLocationLigths){
            binaryLight.turnOff();
        }
    }

    private  void setOnAllLightsInLocation(String location) {

        List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(location);
        for(BinaryLight binaryLight : sameLocationLigths){
            binaryLight.turnOn();
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

    @Override
    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {

        if (device instanceof BinaryLight){
            synchronized (m_lock){
                BinaryLight changingBinaryLight = (BinaryLight) device;
                if (propertyName.equals(BinaryLight.LOCATION_PROPERTY_NAME)){
                    changingBinaryLight.turnOff();
                }
            }
        }
    }


    @Override
    public long getPeriod() {
        return 60000;
    }

    @Override
    public String getGroup() {
        return "LightFollowMeWithMotionSensorApplication-PM";
    }

    @Override
    public void run() {

        synchronized (m_lock){
            for(String location : mapOfZone.keySet()){
                if ((mapOfZone.get(location) + DEFAULT_TIMEOUT) < clock.currentTimeMillis()){
                    setOffAllLightsInLocation(location);
                }
            }
        }
    }

    @Override
    public void zoneAdded(Zone zone) {
        synchronized (m_lock){
            mapOfZone.put(zone.getId(), clock.currentTimeMillis());
        }
    }

    @Override
    public void zoneRemoved(Zone zone) {
        synchronized (m_lock){
            mapOfZone.remove(zone);
        }
    }

    @Override
    public void zoneMoved(Zone zone, Position oldPosition, Position newPosition) {
    }

    @Override
    public void zoneResized(Zone zone) {
    }

    @Override
    public void zoneParentModified(Zone zone, Zone oldParentZone, Zone newParentZone) {
    }

    @Override
    public void deviceAttached(Zone container, LocatedDevice child) {
    }

    @Override
    public void deviceDetached(Zone container, LocatedDevice child) {
    }

    @Override
    public void zoneVariableAdded(Zone zone, String variableName) {
    }

    @Override
    public void zoneVariableRemoved(Zone zone, String variableName) {
    }

    @Override
    public void zoneVariableModified(Zone zone, String variableName, Object oldValue, Object newValue) {
    }

    @Override
    public void factorModified(int oldFactor) {

    }

    @Override
    public void startDateModified(long oldStartDate) {

    }

    @Override
    public void clockPaused() {

    }

    @Override
    public void clockResumed() {

    }

    @Override
    public void clockReset() {
        synchronized (m_lock){
            for(String location : mapOfZone.keySet()){
                mapOfZone.put(location,clock.currentTimeMillis());
                setOffAllLightsInLocation(location);
            }
        }
    }
}
