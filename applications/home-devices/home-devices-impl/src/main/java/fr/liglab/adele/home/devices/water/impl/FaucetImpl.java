package fr.liglab.adele.home.devices.water.impl;

import fr.liglab.adele.home.devices.water.Faucet;
import fr.liglab.adele.home.devices.water.impl.WaterConsumerDeviceImpl;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.framework.Constants;

@Component(name = "iCasa.Faucet")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class FaucetImpl extends WaterConsumerDeviceImpl implements SimulatedDevice, Faucet {

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String m_serialNumber;

    public FaucetImpl(){
        super();
    }

    @Override
    public String getSerialNumber() {
        return m_serialNumber;
    }
}

