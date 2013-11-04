/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.zigbee.device.factories;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDriver;

/**
 * User: Kettani Mehdi
 * Date: 24/10/13
 * Time: 4:27 PM
 */
@Component(name="zigbeeThermometer")
@Provides
public class ZigbeeThermometer extends AbstractDevice implements Thermometer, ZigbeeDevice, ZigbeeDeviceTracker {

	@Requires
    private ZigbeeDriver driver;

    @Property(mandatory=true, name="zigbee.moduleAddress")
    private String moduleAddress;

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String serialNumber;

    public ZigbeeThermometer(){
        super();
        super.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, GenericDevice.LOCATION_UNKNOWN);
        super.setPropertyValue(ZigbeeDevice.BATTERY_LEVEL, 0f);
    }

	@Override
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
     * Called when a new device has been discovered by the driver.
     *
     * @param deviceInfo information about the device
     */
	@Override
	public void deviceAdded(DeviceInfo deviceInfo) {/*nothing to do*/}

	/**
     * Called when a device has been removed by the driver.
     *
     * @param deviceInfo information about the device
     */
	@Override
	public void deviceRemoved(DeviceInfo deviceInfo) {/*nothing to do*/}
	
	/**
     * Called when a device data has changed.
     *
     * @param address a device module address
     * @param oldData       previous device data
     * @param newData       new device data
     */
	@Override
	public void deviceDataChanged(String moduleAddress, Data oldData,
			Data newData) {
		if(moduleAddress.compareTo(this.moduleAddress) == 0){
            String data = newData.getData();
            String computedTemperature = computeTemperature(data);
            setPropertyValue(THERMOMETER_CURRENT_TEMPERATURE, computedTemperature);
        }
	}

	/**
	 * Compute temperature from the given hexadecimal value.
	 * @param data
	 * @return
	 */
	public String computeTemperature(String data) {
		
		double computedTemp;
		
		String sign = data.substring(0, 1);
		
		if (Integer.valueOf(sign, 16) > 7){ // negative value
			computedTemp = -(Integer.parseInt("1000", 16) - Integer.parseInt(data, 16)) * 0.0625;
		} else { // positive value
			computedTemp = Integer.parseInt(data, 16) * 0.0625;
		}
		
		return String.valueOf(computedTemp);
	}

	/**
     * Called when a device battery level has changed.
     *
     * @param address   a device module address
     * @param oldBatteryLevel previous device battery level
     * @param newBatteryLevel new device battery level
     */
	@Override
	public void deviceBatteryLevelChanged(String moduleAddress,
			float oldBatteryLevel, float newBatteryLevel) {
		if(moduleAddress.compareToIgnoreCase(this.moduleAddress) == 0){ //this device.
            setPropertyValue(ZigbeeDevice.BATTERY_LEVEL, newBatteryLevel);
        }
	}

	@Override
	public double getTemperature() {
		Double temperature =(Double)getPropertyValue(THERMOMETER_CURRENT_TEMPERATURE);
		if (temperature == null){
			return 0;
		}
		return temperature; 
	}
}
