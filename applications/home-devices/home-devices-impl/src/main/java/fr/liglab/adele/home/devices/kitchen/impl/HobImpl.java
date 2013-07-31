package fr.liglab.adele.home.devices.kitchen.impl;

import fr.liglab.adele.home.devices.kitchen.Hob;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;


/**
 * Simple simulated Hob implementation.
 */
@Component(name = "iCasa.Hob")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class HobImpl extends AbstractDevice implements Hob, SimulatedDevice {

    /**
     * The device serial number.
     */
    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

    /**
     * Retrieve the hob serial number.
     * @return the serial number.
     */
    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }
}
