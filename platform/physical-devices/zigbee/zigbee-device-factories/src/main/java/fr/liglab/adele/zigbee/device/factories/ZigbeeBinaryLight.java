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
/**
 *
 */
package fr.liglab.adele.zigbee.device.factories;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.entity.State;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDriver;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.function.Consumer;


@ContextEntity(services = {BinaryLight.class,ZigbeeDevice.class,ZigbeeDeviceTracker.class})
public class ZigbeeBinaryLight implements BinaryLight, ZigbeeDevice,ZigbeeDeviceTracker{

    @State.Field(service = BinaryLight.class,state = BinaryLight.BINARY_LIGHT_POWER_STATUS)
    private boolean powerStatus;

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

    @Requires
    private ZigbeeDriver driver;

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public boolean getPowerStatus() {
        return powerStatus;
    }

    @Override
    public boolean setPowerStatus(boolean status) {
        powerStatus = status;
        return powerStatus;
    }

    @Override
    public void turnOn() {
        powerStatus = true;
    }

    @Override
    public void turnOff() {
        powerStatus = false;
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

    @State.Apply(service = BinaryLight.class,state = BINARY_LIGHT_POWER_STATUS)
    Consumer<Boolean> setPowerStatus = (powerStatus) -> {
        if (powerStatus) {
            driver.setData(moduleAddress, "1");
        } else {
            driver.setData(moduleAddress, "0");
        }
    };

    @Validate
    public void start() {

    }

    @Invalidate
    public void stop() {

    }

    //ZigbeeDeviceTracker Methods.
    /**
     * Called when a new device has been discovered by the driver.
     *
     * @param deviceInfo information about the device
     */
    @Override
    public void deviceAdded(DeviceInfo deviceInfo) {/*do nothing*/}

    /**
     * Called when a device has been discovered by the driver.
     *
     * @param deviceInfo information about the device
     */
    @Override
    public void deviceRemoved(DeviceInfo deviceInfo) {/*do nothing*/}

    /**
     * Called when a device data has changed.
     *
     * @param address a device module address
     * @param oldData       previous device data
     * @param newData       new device data
     */
    @Override
    public void deviceDataChanged(String address, Data oldData, Data newData) {
        if(address.compareTo(this.moduleAddress) == 0){
            String data = newData.getData();
            boolean status = data.compareTo("1")==0? true : false;
            pushPowerStatus(status);
        }
    }

    @State.Push(service = BinaryLight.class,state = BINARY_LIGHT_POWER_STATUS)
    public boolean pushPowerStatus(boolean powerStatus){
        return powerStatus;
    }
    /**
     * Called when a device battery level has changed.
     *
     * @param address   a device module address
     * @param oldBatteryLevel previous device battery level
     * @param newBatteryLevel new device battery level
     */
    @Override
    public void deviceBatteryLevelChanged(String address,
                                          float oldBatteryLevel,
                                          float newBatteryLevel) {
        if(address.compareToIgnoreCase(this.moduleAddress) == 0){ //this device.
            pushBatteryLevel(newBatteryLevel);
        }
    }

    @State.Push(service = ZigbeeDevice.class,state = BATTERY_LEVEL)
    public float pushBatteryLevel(float battery){
        return battery;
    }

}
