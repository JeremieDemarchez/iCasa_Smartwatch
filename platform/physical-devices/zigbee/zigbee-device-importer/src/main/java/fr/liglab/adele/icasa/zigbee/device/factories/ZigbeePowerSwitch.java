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
package fr.liglab.adele.icasa.zigbee.device.factories;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDriver;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.zigbee.device.api.ZigbeeDevice;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;

/**
 * Zigbee power switch factory.
 */
@ContextEntity(services = {PowerSwitch.class, ZigbeeDevice.class,ZigbeeDeviceTracker.class,LocatedObject.class})
public class ZigbeePowerSwitch implements PowerSwitch, ZigbeeDevice, ZigbeeDeviceTracker,LocatedObject {

    @Requires
    private ZigbeeDriver driver;

    @ContextEntity.State.Field(service = PowerSwitch.class,state = PowerSwitch.POWER_SWITCH_CURRENT_STATUS,value = "false")
    private boolean status;

    @ContextEntity.State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @ContextEntity.State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_X,directAccess = true,value = "0")
    private int x;

    @ContextEntity.State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_Y,directAccess = true,value = "0")
    private int y;

    @ContextEntity.State.Field(service = LocatedObject.class,state = LocatedObject.ZONE,value = LOCATION_UNKNOWN)
    private String zoneName;

    @ContextEntity.State.Field(service = ZigbeeDevice.class,state = MODULE_ADRESS)
    private String moduleAddress;

    @ContextEntity.State.Field(service = ZigbeeDevice.class,state = BATTERY_LEVEL)
    private float batteryLevel;

    @Override
    public String getZone() {
        return zoneName;
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

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public boolean getStatus() {
        return status;
    }

    /**
     * Called when a new device has been discovered by the driver.
     *
     * @param deviceInfo information about the device
     */
    @Override
    public void deviceAdded(DeviceInfo deviceInfo) {/*nothing to do*/}

    /**
     * Called when a device has been discovered by the driver.
     *
     * @param deviceInfo information about the device
     */
    @Override
    public void deviceRemoved(DeviceInfo deviceInfo){/*nothing to do*/}

    /**
     * Called when a device data has changed.
     *
     * @param address a device module address
     * @param oldData       previous device data
     * @param newData       new device data
     */
    @Override
    public void deviceDataChanged(String address, Data oldData, Data newData) {
        if(address.compareToIgnoreCase(this.moduleAddress) == 0){ //this device.
            boolean status = newData.getData().equalsIgnoreCase("1")? true: false;
            pushStatus(status);
        }
    }

    @ContextEntity.State.Push(service = PowerSwitch.class,state = POWER_SWITCH_CURRENT_STATUS)
    private boolean pushStatus(boolean status){
        return status;
    }
    /**
     * Called when a device battery level has changed.
     *
     * @param address   a device module address
     * @param oldBatteryLevel previous device battery level
     * @param newBatteryLevel new device battery level
     */
    @Override
    public void deviceBatteryLevelChanged(String address, float oldBatteryLevel, float newBatteryLevel) {
        if(address.compareToIgnoreCase(this.moduleAddress) == 0){ //this device.
            pushBatteryLevel(newBatteryLevel);
        }
    }

    @ContextEntity.State.Push(service = ZigbeeDevice.class,state = BATTERY_LEVEL)
    public float pushBatteryLevel(float battery){
        return battery;
    }

    /**
     * Zone
     */
    @ContextEntity.Relation.Field(value = "isIn",owner = LocatedObject.class)
    @Requires(id="zone",specification=Zone.class,optional=true)
    private Zone zoneAttached;

    @Bind(id = "zone")
    public void bindZone(Zone zone){
        pushZone(zone.getZoneName());
    }

    @Unbind(id= "zone")
    public void unbindZone(Zone zone){
        pushZone(LOCATION_UNKNOWN);
    }

    @ContextEntity.State.Push(service = LocatedObject.class,state = LocatedObject.ZONE)
    public String pushZone(String zoneName) {
        return zoneName;
    }
}
