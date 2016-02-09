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

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.entity.State;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDriver;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import org.apache.felix.ipojo.annotations.Requires;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@ContextEntity(services = {Thermometer.class,ZigbeeDevice.class,ZigbeeDeviceTracker.class})
public class ZigbeeThermometer implements Thermometer,ZigbeeDevice, ZigbeeDeviceTracker {

    @Requires
    private ZigbeeDriver driver;

    @State.Field(service = Thermometer.class,state = THERMOMETER_CURRENT_TEMPERATURE )
    private double currentTemperature;

    @State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_X,directAccess = true)
    private int x;

    @State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_Y,directAccess = true)
    private int y;

    @State.Field(service = LocatedObject.class,state = LocatedObject.ZONE,directAccess = true)
    private String zone;

    @State.Field(service = ZigbeeDevice.class,state = ZigbeeDevice.MODULE_ADRESS)
    private String moduleAddress;

    @State.Field(service = ZigbeeDevice.class,state = ZigbeeDevice.BATTERY_LEVEL)
    private float batteryLevel;


    @Override
    public double getTemperature() {
        return currentTemperature;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String getZone() {
        return zone;
    }

    @Override
    public Position getPosition() {
        return new Position(x,y);
    }

    @Override
    public void setPosition(Position position) {
        x = position.x;
        y = position.y;
    }


    /**
     * Called when a new device has been discovered by the driver.
     *
     * @param deviceInfo
     *            information about the device
     */
    @Override
    public void deviceAdded(DeviceInfo deviceInfo) {/* nothing to do */
    }

    /**
     * Called when a device has been removed by the driver.
     *
     * @param deviceInfo
     *            information about the device
     */
    @Override
    public void deviceRemoved(DeviceInfo deviceInfo) {/* nothing to do */
    }

    /**
     * Called when a device data has changed.
     *
     * @param moduleAddress
     *            a device module address
     * @param oldData
     *            previous device data
     * @param newData
     *            new device data
     */
    @Override
    public void deviceDataChanged(String moduleAddress, Data oldData,
                                  Data newData) {
        if (moduleAddress.compareTo(this.moduleAddress) == 0) {
            String data = newData.getData();
            String computedTemperature = computeTemperature(data);
            pushTemperature(Double.valueOf(computedTemperature));
        }
    }

    @State.Push(service = Thermometer.class,state = THERMOMETER_CURRENT_TEMPERATURE)
    public double pushTemperature(double temperature){
        return temperature;
    }

    /**
     * Compute temperature from the given hexadecimal value.
     *
     * @param data
     * @return
     */
    public String computeTemperature(String data) {

        if (data.length() != 4) {
            return null;
        }

        double computedTemp;

        StringBuilder convertedData = new StringBuilder();

        for (byte b : data.getBytes()) {
            String hex = String.format("%04x", (int) b);
            char c = hex.charAt(hex.length() - 1);
            convertedData.append(c);
        }
        convertedData.deleteCharAt(convertedData.length() - 1);

        String sign = convertedData.substring(0, 1);

        if (Integer.valueOf(sign, 16) > 7) { // negative value
            computedTemp = -(Integer.parseInt("1000", 16) - Integer.valueOf(
                    convertedData.toString(), 16)) * 0.0625;
        } else { // positive value
            computedTemp = Integer.valueOf(convertedData.toString(), 16) * 0.0625;
        }

        // format temperature to take integer part and 2 decimal values
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        return df.format(computedTemp);
    }

    /**
     * Called when a device battery level has changed.
     *
     * @param moduleAddress
     *            a device module address
     * @param oldBatteryLevel
     *            previous device battery level
     * @param newBatteryLevel
     *            new device battery level
     */
    @Override
    public void deviceBatteryLevelChanged(String moduleAddress,
                                          float oldBatteryLevel, float newBatteryLevel) {
        if (moduleAddress.compareToIgnoreCase(this.moduleAddress) == 0) { // this
            // device.
            pushBatteryLevel(newBatteryLevel);
        }
    }

    @State.Push(service = ZigbeeDevice.class,state = BATTERY_LEVEL)
    public float pushBatteryLevel(float battery){
        return battery;
    }

}
