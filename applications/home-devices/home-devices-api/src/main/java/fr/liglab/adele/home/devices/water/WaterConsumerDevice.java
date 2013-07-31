package fr.liglab.adele.home.devices.water;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 *  Service definition of a simple FlushToilet.
 */
public interface WaterConsumerDevice extends GenericDevice {

    String CURRENT_CONSUMPTION = "current.consumption";

    String MAX_CONSUMPTION = "max.consumption";

    /**
     * Get the current consumption (lt/min)
     * @return
     */
    float getCurrentConsumption();

    /**
     * Get the max consumption (lt/min)
     * @return
     */
    float getMaxConsumption();
}
