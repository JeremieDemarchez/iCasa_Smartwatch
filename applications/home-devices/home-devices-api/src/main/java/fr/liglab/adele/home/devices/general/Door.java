package fr.liglab.adele.home.devices.general;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 *  Service definition of a Door.
 */
public interface Door extends GenericDevice {

    /**
     * Get the door location.
     * @return the zone location name where the door is.
     */
    String getLocation();


}
