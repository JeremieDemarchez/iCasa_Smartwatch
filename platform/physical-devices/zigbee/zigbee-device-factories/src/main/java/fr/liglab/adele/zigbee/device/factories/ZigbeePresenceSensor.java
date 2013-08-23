package fr.liglab.adele.zigbee.device.factories;

import org.apache.felix.ipojo.annotations.*;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDriver;

@Component(name="zigbeePresenceSensor")
@Provides
/**
 * Zigbee presence sensor factory.
 * @author Kettani Mehdi.
 *
 */
public class ZigbeePresenceSensor extends AbstractDevice implements PresenceSensor, ZigbeeDevice {
	
	@Requires
	private ZigbeeDriver driver;
	
	@Property(mandatory=true, name="zigbee.moduleAddress")
	private String moduleAddress;
	
	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String serialNumber;
	
	public ZigbeePresenceSensor(){
		super();
        super.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, GenericDevice.LOCATION_UNKNOWN);
        super.setPropertyValue(PRESENCE_SENSOR_SENSED_PRESENCE, false);
	}

	@Override
	public boolean getSensedPresence() {
		boolean value = getSensedPresenceFromDevice();
		setPropertyValue(PRESENCE_SENSOR_SENSED_PRESENCE, value);
		
		Boolean presence = (Boolean) getPropertyValue(PRESENCE_SENSOR_SENSED_PRESENCE);
		if (presence != null)
			return presence;
		return false;
	}

	@Override
	public String getSerialNumber() {
		return serialNumber;
	}
	
	private boolean getSensedPresenceFromDevice() {
		boolean value = false;

		Data devData = driver.getData(moduleAddress);
		if (devData == null)
			return value;
		String strValue = devData.getData();
		Integer intValue = null;
		try {
			intValue = Integer.valueOf(strValue);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return value;
		}

		return (intValue != null) && (intValue == 1);
	}
}
