/**
 * 
 */
package fr.liglab.adele.zigbee.device.factories;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDriver;

/**
 * Zigbee binary light factory.if (type == 'R') {
					write(buildResponseWithNewValue(ResponseType.REQUEST,
							deviceInfos.getModuleAddress(), dataValue.getData()
									.equals("1") ? "0" : "1"));
				} else {
					write(buildResponse(ResponseType.DATA,
							deviceInfos.getModuleAddress()));
				}author kettani Mehdi
 */
@Component(name = "zigbeeBinaryLight")
@Provides(specifications={GenericDevice.class, BinaryLight.class, ZigbeeDevice.class})
public class ZigbeeBinaryLight extends AbstractDevice implements
		BinaryLight, ZigbeeDevice, DeviceListener<BinaryLight> {
	
	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String serialNumber;

	@Requires
	private ZigbeeDriver driver;

	@Property(mandatory = true, name = "zigbee.moduleAddress")
	private String moduleAddress;
	
	public ZigbeeBinaryLight() {
		super();
		super.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, GenericDevice.LOCATION_UNKNOWN);
		super.setPropertyValue(BinaryLight.BINARY_LIGHT_POWER_STATUS, false);
		super.setPropertyValue(BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL, 1.0d); // TODO demander a jean paul
	}

	@Override
	public String getSerialNumber() {
		return serialNumber;
	}

	@Override
	public synchronized boolean getPowerStatus() {
		Boolean powerStatus = (Boolean) getPropertyValue(BinaryLight.BINARY_LIGHT_POWER_STATUS);
		if (powerStatus == null)
			return false;

		return powerStatus;
	}

	@Override
	public synchronized boolean setPowerStatus(boolean status) {
         setPropertyValue(BinaryLight.BINARY_LIGHT_POWER_STATUS, status);
		return status;
	}

	@Override
	public double getMaxPowerLevel() {
		Double maxLevel = (Double) getPropertyValue(BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL);
		if (maxLevel == null)
			return 0;

		return maxLevel;
	}

	@Override
	public void turnOn() {
		setPowerStatus(true);
	}

	@Override
	public void turnOff() {
		setPowerStatus(false);
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

	private boolean setPowerStatusToDevice(boolean powerStatus) {
		if (powerStatus) {
			driver.setData(moduleAddress, "1");
		} else {
			driver.setData(moduleAddress, "0");
		}
		return getPowerStatus();
	}
	
	@Validate
	public void start() {
		addListener(this);
		boolean initialValue = getPowerStatusFromDevice();
		setPowerStatus(initialValue); //TODO manage in a better way the initial value
	}
	
	@Invalidate
	public void stop() {
		removeListener(this);
	}

	@Override
	public void deviceAdded(BinaryLight arg0) {
		// do nothing
	}

	@Override
	public void deviceEvent(BinaryLight arg0, Object arg1) {
		// do nothing
	}

	@Override
	public void devicePropertyAdded(BinaryLight arg0, String arg1) {
		// do nothing
	}

	@Override
	public void devicePropertyModified(BinaryLight device, String propName,
			Object oldValue, Object newValue) {
		if (BinaryLight.BINARY_LIGHT_POWER_STATUS.equals(propName)) {
			Boolean newPowerStatus = (Boolean) newValue;
			if (newPowerStatus != null)
				setPowerStatusToDevice(newPowerStatus);
		}
	}

	@Override
	public void devicePropertyRemoved(BinaryLight arg0, String arg1) {
		// do nothing
	}

	@Override
	public void deviceRemoved(BinaryLight arg0) {
		// do nothing
	}

}
