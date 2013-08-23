/**
 * 
 */
package fr.liglab.adele.zigbee.device.factories;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDriver;

/**
 * Zigbee power switch factory.
 * @author Kettani Mehdi
 */
@Component(name="zigbeePowerSwitch")
@Provides
public class ZigbeePowerSwitch extends AbstractDevice implements PowerSwitch, ZigbeeDevice {

	@Requires
	private ZigbeeDriver driver;

	@Property(mandatory = true, name = "zigbee.moduleAddress")
	private String moduleAddress;

	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String serialNumber;
	
	@Override
	public String getSerialNumber() {
		return serialNumber;
	}
	
	public ZigbeePowerSwitch(){
		super();
		super.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, GenericDevice.LOCATION_UNKNOWN);
        super.setPropertyValue(POWER_SWITCH_CURRENT_STATUS, false);
	}

	@Override
	public boolean getStatus() {
		boolean status = getPowerStatusFromDevice();
		setPropertyValue(BinaryLight.BINARY_LIGHT_POWER_STATUS, status);
		
		Boolean powerStatus = (Boolean) getPropertyValue(BinaryLight.BINARY_LIGHT_POWER_STATUS);
		if (powerStatus == null)
			return false;

		return powerStatus;
	}

	@Override
	public boolean switchOff() {
		// the only available data is the switch value, no programmatical modification allowed 
		return getStatus();
	}

	@Override
	public boolean switchOn() {
		// the only available data is the switch value, no programmatical modification allowed 
		return getStatus();
	}
	
	private boolean getPowerStatusFromDevice() {
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
