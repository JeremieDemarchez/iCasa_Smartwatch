package fr.liglab.adele.home.devices.water;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 *  Service definition of a water consumer device..
 */
public interface WaterMeter extends GenericDevice {
    String CURRENT_CONSUMPTION = "current.consumption";

    /**
     * Get the current consumption (lt/min)
     * @return
     */
    float getCurrentConsumption();

}
