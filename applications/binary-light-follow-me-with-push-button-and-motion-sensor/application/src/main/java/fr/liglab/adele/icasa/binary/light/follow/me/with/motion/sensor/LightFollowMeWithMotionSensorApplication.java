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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.motion.MotionSensor;
import fr.liglab.adele.icasa.device.util.EmptyDeviceListener;
import fr.liglab.adele.icasa.service.preferences.Preferences;
import fr.liglab.adele.icasa.service.scheduler.ScheduledRunnable;

@Component(name = "LightFollowMeWithMotionSensorApplication")
@Instantiate
public class LightFollowMeWithMotionSensorApplication extends EmptyDeviceListener {

    private final BundleContext bundleContext;

    private static long DEFAULT_TIMEOUT = 60000;

    protected static final String APPLICATION_ID = "light.follow.me.with.motion.sensor";

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG + APPLICATION_ID);

    @Requires
    private Preferences preferences;

    /** Field for binaryLights dependency */
    @RequiresDevice(id = "binaryLights", type = "field", optional = true)
    private BinaryLight[] binaryLights;

    /** Field for motionSensors dependency */
    @RequiresDevice(id = "motionSensors", type = "field", optional = true)
    private MotionSensor[] motionSensors;


    Map<String, ServiceRegistration> registrations = new HashMap<String, ServiceRegistration>();

    Map<String, TurnOffLightTask> tasks = new HashMap<String, TurnOffLightTask>();

    Object lockObject = new Object();

    /** Bind Method for null dependency */
    @RequiresDevice(id = "motionSensors", type = "bind")
    public void bindMotionSensor(MotionSensor motionSensor, Map properties) {
        logger.trace("Register Listener to MotionSensor" + motionSensor.getSerialNumber());
        motionSensor.addListener(this);
    }

    /** Unbind Method for null dependency */
    @RequiresDevice(id = "motionSensors", type = "unbind")
    public void unbindMotionSensor(MotionSensor motionSensor, Map properties) {
        logger.trace("Remove Listener to MotionSensor" + motionSensor.getSerialNumber());
        motionSensor.removeListener(this);
    }

    @Requires
    private Clock clock;

    public LightFollowMeWithMotionSensorApplication(BundleContext context) {
        this.bundleContext = context;
    }

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
    }

    /** Component Lifecycle Method */
    @Validate
    public void start() {
        // do nothing
    }

    /**
     * Return all BinaryLight from the given location
     * 
     * @param location : the given location
     * @return the list of matching BinaryLights
     */
    private synchronized List<BinaryLight> getBinaryLightFromLocation(String location) {
        List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
        for (BinaryLight binLight : binaryLights) {
            if (binLight.getPropertyValue(BinaryLight.LOCATION_PROPERTY_NAME).equals(location)) {
                binaryLightsLocation.add(binLight);
                logger.trace("Light match with location " + binLight.getSerialNumber());
            } else {
                logger.trace("Light not matching with location " + binLight.getSerialNumber());
            }
        }
        return binaryLightsLocation;
    }

    /**
     * Motion sensor will trigger a deviceEvent call.
     * 
     * @param device the motion sensor detecting the movement.
     * @param data
     */
    public void deviceEvent(GenericDevice device, Object data) {
        String location = String.valueOf(device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME));
        if (device instanceof MotionSensor) {
            logger.trace("Motion detected in " + location);
            turnOnTheLights(location);
            scheduleTask(location);
        }
    }

    private void scheduleTask(String location) {
        // If exists, remove service
        TurnOffLightTask task = null;
        ServiceRegistration registration = null;
        synchronized (lockObject) {
            if (registrations.containsKey(location)) {
                registration = registrations.remove(location);
                task = tasks.get(location);
            }
            if (task == null) {
                task = new TurnOffLightTask();
                tasks.put(location, task);
                task.setLocation(location);
            }            
            long scheduledTime = clock.currentTimeMillis() + getTimeout();
            task.setExecutionDate(scheduledTime);
        }
        if (registration != null) {
            try{
                registration.unregister();
            }catch(Exception ex){}
        }
        registration = bundleContext.registerService(ScheduledRunnable.class.getName(), task, new Hashtable());
        synchronized (lockObject) {
            registrations.put(location, registration);
        }
    }


    protected void turnOffTheLights(String location) {
        List<BinaryLight> sameLocationLights = getBinaryLightFromLocation(location);
        logger.trace("To turn on Light in " + location);
        for (BinaryLight binaryLight : sameLocationLights) {
            logger.trace("To turn on Light in " + binaryLight.getSerialNumber());
            binaryLight.setPowerStatus(false);
        }
    }

    protected void turnOnTheLights(String location) {
        List<BinaryLight> sameLocationLights = getBinaryLightFromLocation(location);
        logger.trace("To turn on Light in " + location);
        for (BinaryLight binaryLight : sameLocationLights) {
            logger.trace("To turn on Light in " + binaryLight.getSerialNumber());
            binaryLight.setPowerStatus(true);
        }
    }

    private long getTimeout() {
        Long tempValue = (Long) preferences.getApplicationPropertyValue(APPLICATION_ID, "Timeout");
        if (tempValue != null) {
            return tempValue;
        } else {
            return DEFAULT_TIMEOUT;
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

    /**
     * This task is charged of turn off the light.
     */
    private class TurnOffLightTask implements ScheduledRunnable {

        private long executionDate = 0;

        private String location = GenericDevice.LOCATION_UNKNOWN;

        public void setLocation(String location) {
            this.location = location;
        }

        public void setExecutionDate(long executionDate) {
            this.executionDate = executionDate;
        }

        @Override
        public long getExecutionDate() {
            return executionDate;
        }

        @Override
        public String getGroup() {
            return null;
        }

        @Override
        public void run() {
            turnOffTheLights(location);
            ServiceRegistration registrationTask = null;
            synchronized (lockObject) {
                registrationTask = registrations.remove(location);
            }
            if (registrationTask != null) {
                try {
                    registrationTask.unregister();
                } catch (Exception ex) {
                }
            }
        }
    }

}
