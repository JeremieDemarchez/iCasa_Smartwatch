/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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
