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

@Component(name = "iCasa.Door")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class DoorImpl extends AbstractDevice implements Door, SimulatedDevice {

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

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

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }

    @Override
    public void enterInZones(List<Zone> zones) {
        if (!zones.isEmpty()) {
            zone = zones.get(0);
            super.setPropertyValue(SimulatedDevice.LOCATION_PROPERTY_NAME, zone.getId());

        }
    }

    @Override
    public void leavingZones(List<Zone> zones) {
        zone = null;
        super.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, SimulatedDevice.LOCATION_UNKNOWN);
    }

}
