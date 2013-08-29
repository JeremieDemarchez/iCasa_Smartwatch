package fr.liglab.adele.home.devices.water.impl;

import fr.liglab.adele.home.devices.water.WaterConsumerDevice;
import fr.liglab.adele.home.devices.water.WaterMeter;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;

import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import java.util.*;

/**
 * Simple implementation of a simulated water meter.
 * It tracks all water consumer devices to calculate its current consumed water.
 */
@Component(name = "iCasa.WaterMeter")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class WaterMeterImpl extends AbstractDevice implements WaterMeter, SimulatedDevice {

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

    private Map<String, WaterConsumerDevice> consumerDevices = new HashMap<String, WaterConsumerDevice>();

    private BundleContext context;

    private ServiceRegistration registration;

    public WaterMeterImpl(BundleContext context){
        super();
        this.context = context;
        super.setPropertyValue(WaterMeter.CURRENT_CONSUMPTION, 0.0f);
    }

    /**
     * When device is valid, it register a task which will calculate the consumed of water each 10 sec.
     */
    @Validate
    private void validate(){
        PeriodicRunnable task = new WaterMeterCalculatorTask();
        registration = context.registerService(PeriodicRunnable.class.getName(), task, new Hashtable());
    }

    /**
     * When invalid, it will unregister the calculator task.
     */
    @Invalidate
    private void invalidate(){
        if(registration != null){
            registration.unregister();
            registration = null;
        }
    }

    /**
     * Retrieves the serial number
     * @return
     */
    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    /**
     * Callback to track all WaterConsumer devices.
     * @param device
     */
    @Bind(optional = true, aggregate = true)
    public void bindDevice(WaterConsumerDevice device){
        consumerDevices.put(device.getSerialNumber(), device);
    }

    /**
     * Callback when a water consumer device has disappear.
     * @param device
     */
    @Unbind
    public void unbindDevice(WaterConsumerDevice device){
        consumerDevices.remove(device.getSerialNumber());
    }

    /**
     * Get the current consumption (lt/min)
     *
     * @return
     */
    @Override
    public float getCurrentConsumption() {
        return (Float)getPropertyValue(WaterMeter.CURRENT_CONSUMPTION);
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) {
        if (propertyName.equals(WaterMeter.CURRENT_CONSUMPTION)) {

            float previousLevel = getCurrentConsumption();
            float level = Float.parseFloat(String.valueOf(value));

            if (previousLevel!=level ) {
                super.setPropertyValue(WaterConsumerDevice.CURRENT_CONSUMPTION, level);
            }
        } else{
            super.setPropertyValue(propertyName, value);
        }
    }

    private synchronized List<WaterConsumerDevice> getDevices(){
        return new ArrayList<WaterConsumerDevice>(consumerDevices.values());
    }

    /**
     * This class is a periodic task which will calculate the current consumed water.
     */
    private class WaterMeterCalculatorTask implements PeriodicRunnable {

        @Override
        public long getPeriod() {
            return 10000;//calculates each 10 seconds
        }

        @Override
        public String getGroup() {
            return null;  //The default group.
        }

        @Override
        public void run() { // each minute it calculates the new consumption.
            float incrementalValue = getCurrentConsumption();
            List<WaterConsumerDevice> devices = getDevices();
            for(WaterConsumerDevice device: devices){
                //current consumtion / 6 'cause the thread run each 10 secs. And current consumption is lt/min
                incrementalValue = (device.getCurrentConsumption()/6) + incrementalValue;
            }
            setPropertyValue(WaterMeter.CURRENT_CONSUMPTION, incrementalValue);
        }
    }

}
