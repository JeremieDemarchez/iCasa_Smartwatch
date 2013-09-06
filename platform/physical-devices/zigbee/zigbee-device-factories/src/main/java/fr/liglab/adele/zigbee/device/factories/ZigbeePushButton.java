package fr.liglab.adele.zigbee.device.factories;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.button.PushButton;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDriver;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: garciai@imag.fr
 * Date: 9/6/13
 * Time: 11:49 AM
 */
@Component(name="zigbeePushButton")
@Provides
public class ZigbeePushButton extends AbstractDevice implements PushButton, ZigbeeDevice, ZigbeeDeviceTracker {

    @Requires
    private ZigbeeDriver driver;

    @Property(mandatory = true, name = "zigbee.moduleAddress")
    private String moduleAddress;



    private static final Logger logger = LoggerFactory
            .getLogger(Constants.ICASA_LOG_DEVICE + ".zigBee.powerSwitch");

    @ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
    private String serialNumber;

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    public ZigbeePushButton(){
        super();
        super.setPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME, GenericDevice.LOCATION_UNKNOWN);
        super.setPropertyValue(PUSH_AND_HOLD, false);
        super.setPropertyValue(BATTERY_LEVEL, 0f);
    }

    @Override
    public boolean isPushed() {
        Boolean powerStatus = (Boolean) getPropertyValue(PushButton.PUSH_AND_HOLD);
        if (powerStatus == null){
            return false;
        }
        return powerStatus;
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
            setPropertyValue(PushButton.PUSH_AND_HOLD, status);
        }
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
            setPropertyValue(BATTERY_LEVEL, newBatteryLevel);
        }
    }


}
