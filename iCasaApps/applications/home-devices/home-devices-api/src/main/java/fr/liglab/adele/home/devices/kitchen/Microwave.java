package fr.liglab.adele.home.devices.kitchen;
import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Service definition of a simple Microwave
 */
public interface Microwave extends GenericDevice{

    String CURRENT_TEMPERATURE = "temperature";

    String CURRENT_POWER_LEVEL = "power";

    /**
     * Get the Microwave current temperature.
     * @return the current temperature.
     */
    float getTemperature();

    /**
     * Get the microwave power level.
     * @return
     */
    float getPowerLevel();


}
