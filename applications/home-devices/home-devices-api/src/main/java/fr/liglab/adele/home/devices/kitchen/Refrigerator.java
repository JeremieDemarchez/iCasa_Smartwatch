package fr.liglab.adele.home.devices.kitchen;
import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Service definition of a simple refrigerator.
 */
public interface Refrigerator extends GenericDevice {

    String CURRENT_TEMPERATURE = "temperature";

    /**
     * Get the refrigerator current temperature.
     * @return the current temperature.
     */
    float getTemperature();


}
