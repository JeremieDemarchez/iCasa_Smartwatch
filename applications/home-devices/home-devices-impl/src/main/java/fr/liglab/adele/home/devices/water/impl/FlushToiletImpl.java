package fr.liglab.adele.home.devices.water.impl;
import fr.liglab.adele.home.devices.water.FlushToilet;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;

/**
 * Simple implementation of a simulated flush toilet.
 */
@Component(name = "iCasa.FlushToilet")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class FlushToiletImpl extends WaterConsumerDeviceImpl implements FlushToilet, SimulatedDevice{

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

    public FlushToiletImpl(){
        super();
    }

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }
}
