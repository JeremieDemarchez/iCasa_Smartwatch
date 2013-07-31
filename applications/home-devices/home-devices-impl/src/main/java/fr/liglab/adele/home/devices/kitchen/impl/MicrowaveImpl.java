package fr.liglab.adele.home.devices.kitchen.impl;

import fr.liglab.adele.home.devices.kitchen.Microwave;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.Constants;



@Component(name = "iCasa.Microwave")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class MicrowaveImpl extends AbstractDevice implements Microwave, SimulatedDevice {

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

    public MicrowaveImpl(){
        super();
        super.setPropertyValue(Microwave.CURRENT_TEMPERATURE, 30.0f);
        super.setPropertyValue(Microwave.CURRENT_POWER_LEVEL, 0.0f);
    }

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    /**
     * Get the Microwave current temperature.
     *
     * @return the current temperature.
     */
    @Override
    public float getTemperature() {
        return (Float) getPropertyValue(Microwave.CURRENT_TEMPERATURE);
    }

    /**
     * Get the microwave power level.
     *
     * @return
     */
    @Override
    public float getPowerLevel() {
        return (Float) getPropertyValue(Microwave.CURRENT_POWER_LEVEL);
    }


    @Override
    public void setPropertyValue(String propertyName, Object value) {
        if (propertyName.equals(Microwave.CURRENT_POWER_LEVEL)) {

            float previousLevel = getPowerLevel();
            float level = Float.parseFloat(String.valueOf(value));

            if (previousLevel!=level) {
                super.setPropertyValue(Microwave.CURRENT_POWER_LEVEL, level);
            }
        } else if (propertyName.equals(Microwave.CURRENT_TEMPERATURE)){
            float previousValue = getTemperature();
            float newValue = Float.parseFloat(String.valueOf(value));

            if (previousValue!=newValue) {
                super.setPropertyValue(Microwave.CURRENT_TEMPERATURE, newValue);
            }
        } else{
            super.setPropertyValue(propertyName, value);
        }
    }
}
