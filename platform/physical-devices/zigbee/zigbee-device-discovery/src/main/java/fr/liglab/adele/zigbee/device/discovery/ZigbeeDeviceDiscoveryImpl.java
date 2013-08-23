/**
 * 
 */
package fr.liglab.adele.zigbee.device.discovery;

import java.util.Map;
import java.util.Properties;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.ow2.chameleon.rose.RoseMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;

/**
 * @author tfqg0024
 *
 */
@Component(name = "zigbee.rose.discovery", immediate = true)
@Provides(specifications={ZigbeeDeviceTracker.class})
public class ZigbeeDeviceDiscoveryImpl implements ZigbeeDeviceTracker {

	@Requires(id="rose.machine")
	private RoseMachine roseMachine;
	
	private static final Logger logger = LoggerFactory
			.getLogger(ZigbeeDeviceDiscoveryImpl.class);
	
	@Override
	public void deviceAdded(DeviceInfo deviceInfo) {
		logger.debug("new zigbee device added in discovery : " + deviceInfo.getModuleAddress());
		registerZigbeeDeviceInROSE(deviceInfo);
	}

	@Override
	public void deviceRemoved(DeviceInfo deviceInfo) {
		String serial = computeSerialNumber(deviceInfo.getModuleAddress());
		unregisterZigbeeDeviceInROSE(serial);
	}

	@Override
	public void deviceDataChanged(String moduleAddress, Data oldData,
			Data newData) {
		// do nothing
		
	}

	@Override
	public void deviceBatteryLevelChanged(String moduleAddress,
			float oldBatteryLevel, float newBatteryLevel) {
		// do nothing
		
	}
	
	private void unregisterZigbeeDeviceInROSE(String serialNumber) {

		roseMachine.removeRemote(serialNumber);
	}

	private void registerZigbeeDeviceInROSE(DeviceInfo deviceInfo) {
		
		Map props = new Properties();

		String serialNumber = computeSerialNumber(deviceInfo.getModuleAddress());

		props.put(RemoteConstants.ENDPOINT_ID, serialNumber);
		props.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "zigbee");
		props.put("objectClass", new String[] { "fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo" });

		props.put("id", deviceInfo.getModuleAddress());
		props.put("zigbee.device.type.code", deviceInfo.getTypeCode().toString());

		EndpointDescription epd = new EndpointDescription(props);
		roseMachine.putRemote(serialNumber, epd);
	}
	
	private String computeSerialNumber(String moduleAddress){
		return "zigbee#"+moduleAddress;
	}
}
