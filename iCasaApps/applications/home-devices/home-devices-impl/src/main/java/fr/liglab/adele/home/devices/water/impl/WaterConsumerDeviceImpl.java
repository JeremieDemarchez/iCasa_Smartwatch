package fr.liglab.adele.home.devices.water.impl;

import fr.liglab.adele.home.devices.water.WaterConsumerDevice;
import fr.liglab.adele.icasa.device.util.AbstractDevice;


/**
 * Abstract class for water consumer devices..
 */
public abstract class WaterConsumerDeviceImpl extends AbstractDevice implements WaterConsumerDevice {

    public WaterConsumerDeviceImpl(){
        super();
        super.setPropertyValue(WaterConsumerDevice.CURRENT_CONSUMPTION,0.0f);
        super.setPropertyValue(WaterConsumerDevice.MAX_CONSUMPTION,100.0f);
    }

    /**
     * Get the current consumption (lt/min)
     *
     * @return
     */
    @Override
    public float getCurrentConsumption() {
        return (Float)getPropertyValue(WaterConsumerDevice.CURRENT_CONSUMPTION);
    }
    /**
     * Get the max consumption (lt/min)
     *
     * @return
     */
    @Override
    public float getMaxConsumption() {
        return (Float)getPropertyValue(WaterConsumerDevice.MAX_CONSUMPTION);
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) {
        if (propertyName.equals(WaterConsumerDevice.CURRENT_CONSUMPTION)) {

            float previousLevel = getCurrentConsumption();
            float level = Float.parseFloat(String.valueOf(value));

            if (previousLevel!=level && level <= getMaxConsumption()) {
                super.setPropertyValue(WaterConsumerDevice.CURRENT_CONSUMPTION, level);
            }
        } else if (propertyName.equals(WaterConsumerDevice.MAX_CONSUMPTION)){
            float previousValue = getMaxConsumption();
            float newValue = Float.parseFloat(String.valueOf(value));

            if (previousValue!=newValue ) {
                super.setPropertyValue(WaterConsumerDevice.MAX_CONSUMPTION, newValue);
            }
        } else{
            super.setPropertyValue(propertyName, value);
        }
    }


}
