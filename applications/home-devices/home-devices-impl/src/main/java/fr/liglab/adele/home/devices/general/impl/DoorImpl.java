package fr.liglab.adele.home.devices.general.impl;

import fr.liglab.adele.home.devices.general.Door;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;

import java.util.List;

/**
 * Simple implementation of a device Door.
 */
@Component(name = "iCasa.Door")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class DoorImpl extends AbstractDevice implements Door, SimulatedDevice {

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

    /**
     * The current zone where the door is located.
     */
    private Zone zone;

    public DoorImpl(){
        super.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);
    }

    /**
     * Get the door location.
     *
     * @return the zone location name where the door is.
     */
    @Override
    public String getLocation() {
        return String.valueOf(getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME));
    }

    /**
     * Get the device serial number.
     * @return the serial number.
     */
    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    /**
     * Callback called when the door enters in a set of zones.
     * @param zones the list of zones where the door is located.
     */
    @Override
    public void enterInZones(List<Zone> zones) {
        if (!zones.isEmpty()) {
            zone = zones.get(0);
            super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, zone.getId());

        }
    }

    /**
     * Callback called when the door is leaving a zone.
     * @param zones The set of zones where the door were located.
     */
    @Override
    public void leavingZones(List<Zone> zones) {
        zone = null;
        super.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);
    }

}
