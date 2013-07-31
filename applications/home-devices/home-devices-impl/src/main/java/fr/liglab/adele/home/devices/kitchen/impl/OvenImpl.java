package fr.liglab.adele.home.devices.kitchen.impl;

import fr.liglab.adele.home.devices.kitchen.Oven;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;

@Component(name = "iCasa.Oven")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class OvenImpl extends AbstractDevice implements SimulatedDevice, Oven {

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

    public OvenImpl(){
        super();
        super.setPropertyValue(Oven.CURRENT_TEMPERATURE, 20.0f);
    }

    /**
     * Get the Oven current temperature.
     *
     * @return the current temperature.
     */
    @Override
    public float getTemperature() {
        return (Float) getPropertyValue(Oven.CURRENT_TEMPERATURE);
    }

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) {
        if (propertyName.equals(Oven.CURRENT_TEMPERATURE)) {

            float previousTemperature = getTemperature();
            float newTemperature = Float.parseFloat(String.valueOf(value));

            if (previousTemperature!=newTemperature) {
                super.setPropertyValue(Oven.CURRENT_TEMPERATURE, newTemperature);
            }
        } else{
            super.setPropertyValue(propertyName, value);
        }
    }
}
