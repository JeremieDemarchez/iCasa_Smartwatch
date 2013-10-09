package fr.liglab.adele.home.devices.kitchen;
import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Service definition of a simple Oven
 */
public interface Oven extends GenericDevice {

    String CURRENT_TEMPERATURE = "temperature";

    /**
     * Get the Oven current temperature.
     * @return the current temperature.
     */
    float getTemperature();

}
