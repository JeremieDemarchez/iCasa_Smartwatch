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

    @Validate
    private void validate(){
        PeriodicRunnable task = new WaterMeterCalculatorTask();
        registration = context.registerService(PeriodicRunnable.class.getName(), task, new Hashtable());
    }

    @Invalidate
    private void invalidate(){
        if(registration != null){
            registration.unregister();
            registration = null;
        }
    }

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    @Bind(optional = true, aggregate = true)
    public void bindDevice(WaterConsumerDevice device){
        consumerDevices.put(device.getSerialNumber(), device);
    }

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

    private class WaterMeterCalculatorTask implements PeriodicRunnable {

        @Override
        public long getPeriod() {
            return 60000;//calculates each minute
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
                incrementalValue = device.getCurrentConsumption() + incrementalValue;
            }
            setPropertyValue(WaterMeter.CURRENT_CONSUMPTION, incrementalValue);
        }
    }

}
