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
package fr.liglab.adele.zwave.device.proxyes;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.motion.MotionSensor;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.zwave.device.api.ZwaveControllerICasa;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import org.apache.felix.ipojo.annotations.*;
import org.openhab.binding.zwave.internal.protocol.ZWaveEventListener;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveAlarmCommandClass.ZWaveAlarmValueEvent;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveAlarmSensorCommandClass.ZWaveAlarmSensorValueEvent;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveBinarySensorCommandClass.ZWaveBinarySensorValueEvent;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveCommandClass.CommandClass;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveMultiLevelSensorCommandClass.ZWaveMultiLevelSensorValueEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveCommandClassValueEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *

 @Component(name="FibaroMotionSensor")

 @Provides(

 specifications	= {
 MotionSensor.class, GenericDevice.class
 },

 properties 		= {
 @StaticServiceProperty(immutable = true, name = ZWaveImporter.FACTORY_PROPERTY_MANUFACTURER,	type="java.lang.String", value = "010F"),
 @StaticServiceProperty(immutable = true, name = ZWaveImporter.FACTORY_PROPERTY_DEVICE_ID, 		type="java.lang.String", value = "1001"),
 @StaticServiceProperty(immutable = true, name = ZWaveImporter.FACTORY_PROPERTY_DEFAULT_PROXY,	type="java.lang.String", value = "true")
 }
 )*/
@ContextEntity(services = {ZwaveDevice.class,LocatedObject.class,MotionSensor.class})
public class FibaroMotionSensor implements MotionSensor,ZwaveDevice,LocatedObject,ZWaveEventListener  {

    /**
     * iPOJO Require
     */
    @Requires(optional = false,id = "zwaveNetworkController")
    private ZwaveControllerICasa controller;

    private static final Logger LOG = LoggerFactory.getLogger(FibaroMotionSensor.class);

    /**
     * STATES
     */
    @ContextEntity.State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @ContextEntity.State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_X,directAccess = true,value = "0")
    private int x;

    @ContextEntity.State.Field(service = LocatedObject.class,state = LocatedObject.OBJECT_Y,directAccess = true,value = "0")
    private int y;

    @ContextEntity.State.Field(service = LocatedObject.class,state = LocatedObject.ZONE,value = LOCATION_UNKNOWN)
    private String zoneName;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_NEIGHBORS)
    private List<Integer> neighbors;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_ID)
    private Integer zwaveId;

    /**
     * Services
     */
    @Override
    public List<Integer> getNeighbors() {
        return neighbors;
    }

    @Override
    public int getZwaveId() {
        return zwaveId;
    }

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

    @Validate
    private void start() {
        controller.addEventListener(this);
    }

    @Invalidate
    private void stop() {
        controller.removeEventListener(this);
    }


    /**
     * Synchro
     *
     */
    @Override
    public void ZWaveIncomingEvent(ZWaveEvent event) {

        if (event.getNodeId() == zwaveId ) {

            if (event instanceof ZWaveAlarmSensorValueEvent) {

                ZWaveAlarmSensorValueEvent alarm = (ZWaveAlarmSensorValueEvent) event;
                LOG.debug("Alarm received for Fibaro motion sensor ["+alarm+"] "+alarm.getValue());

                switch (alarm.getAlarmType()) {
                    default:
                        break;
                }
            }

            if (event instanceof ZWaveAlarmValueEvent) {

                ZWaveAlarmValueEvent alarm = (ZWaveAlarmValueEvent) event;
                LOG.debug("Alarm received for Fibaro motion sensor ["+alarm+"] "+alarm.getValue());

                switch (alarm.getAlarmType()) {
                    default:
                        break;
                }
            }

            if (event instanceof ZWaveBinarySensorValueEvent) {

                ZWaveBinarySensorValueEvent sensor = (ZWaveBinarySensorValueEvent) event;
                LOG.debug("Sensed value received for Fibaro motion sensor ["+sensor+"] "+sensor.getValue());

                switch (sensor.getSensorType()) {
                    case UNKNOWN:
                        int sensed = ((Integer) sensor.getValue()).intValue();
                        if (sensed == 255) {
                            LOG.info("Motion sensed "+sensor+"] "+sensor.getValue());
                            //	                this.notifyListeners(new DeviceDataEvent<Boolean>(this, DeviceEventType.DEVICE_EVENT, Boolean.TRUE));
                        }
                        break;
                    default:
                        break;
                }
            }

            if (event instanceof ZWaveMultiLevelSensorValueEvent) {

                ZWaveMultiLevelSensorValueEvent sensor = (ZWaveMultiLevelSensorValueEvent) event;
                LOG.debug("Sensed value received for Fibaro motion sensor ["+sensor+"] "+sensor.getValue());
                switch (sensor.getSensorType()) {
                    default:
                        break;
                }
            }

            if (event instanceof ZWaveCommandClassValueEvent) {

                ZWaveCommandClassValueEvent changedValue = (ZWaveCommandClassValueEvent) event;
                if (changedValue.getCommandClass().equals(CommandClass.BATTERY)) {
                    Integer batteryLevel = (Integer) changedValue.getValue();
                    //			setPropertyValue("zwave.batteryLevel", batteryLevel);
                }
            }

        }

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


    /**
     * Neighbors Synchro
     */
    @ContextEntity.Relation.Field(value = "isZwaveNeighbor",owner = ZwaveDevice.class)
    @Requires(id="zwavesNeighbors",specification=ZwaveDevice.class,optional=true)
    private List<ZwaveDevice> zwaveDevices;

    @Bind(id = "zwavesNeighbors")
    public void bindZDevice(ZwaveDevice device){
        pushNeighbors();
    }

    @Unbind(id= "zwavesNeighbors")
    public void unbindZDevice(ZwaveDevice device){
        pushNeighbors();
    }

    @ContextEntity.State.Push(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_NEIGHBORS)
    public List<Integer> pushNeighbors() {
        List<Integer> neighbors = new ArrayList<>();
        for (ZwaveDevice device : zwaveDevices){
            neighbors.add(device.getZwaveId());
        }
        return neighbors;
    }

}
