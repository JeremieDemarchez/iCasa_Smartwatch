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
package fr.liglab.adele.zwave.device.proxies.openhab;

import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.liglab.adele.cream.annotations.behavior.Behavior;
import fr.liglab.adele.cream.annotations.entity.ContextEntity;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.doorWindow.DoorWindowSensor;

import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.LocatedObjectBehaviorProvider;

import fr.liglab.adele.zwave.device.api.ZwaveDevice;

import org.openhab.binding.zwave.internal.protocol.ZWaveEventListener;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveBinarySensorCommandClass;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveCommandClass;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveEvent;

import java.util.ArrayList;
import java.util.List;

@ContextEntity(services = {ZwaveDevice.class, DoorWindowSensor.class})
@Behavior(id="LocatedBehavior",spec = LocatedObject.class,implem = LocatedObjectBehaviorProvider.class)

public class FibaroDoorWindowSensor implements ZwaveDevice, GenericDevice, DoorWindowSensor, ZWaveEventListener {

    /**
     * iPOJO Require
     */
    @Requires(optional = false, proxy=false)
    private OpenhabController controller;

    /**
     * STATES
     */
    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.NEIGHBORS)
    private List<Integer> neighbors;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.HOME_ID)
    private Integer zwaveHomeId;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.NODE_ID)
    private Integer zwaveNodeId;

    @ContextEntity.State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @ContextEntity.State.Field(service = DoorWindowSensor.class,state = DoorWindowSensor.DOOR_WINDOW_SENSOR_OPENING_DETECTCION,value = "false")
    private boolean status;

    /**
     * SERVICE
     */
    @Override
    public List<Integer> getNeighbors() {
        return neighbors;
    }

    @Override
    public int getNodeId() {
        return zwaveNodeId;
    }
    
    @Override
    public int getHomeId() {
    	return zwaveHomeId;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public boolean isOpened() {
        return status;
    }

    /**
     * LIFECYCLE
     */

    @Validate
    private void start() {
        controller.addEventListener(this);
    }

    @Invalidate
    private void stop() {
        controller.removeEventListener(this);
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

    @ContextEntity.State.Push(service = ZwaveDevice.class,state = ZwaveDevice.NEIGHBORS)
    public List<Integer> pushNeighbors() {
        List<Integer> neighbors = new ArrayList<>();
        for (ZwaveDevice device : zwaveDevices){
            neighbors.add(device.getNodeId());
        }
        return neighbors;
    }

    @Override
    public void ZWaveIncomingEvent(ZWaveEvent event) {
        if (event.getNodeId() == zwaveNodeId) {
            if (event instanceof ZWaveBinarySensorCommandClass.ZWaveBinarySensorValueEvent){
                ZWaveBinarySensorCommandClass.ZWaveBinarySensorValueEvent castEvent = (ZWaveBinarySensorCommandClass.ZWaveBinarySensorValueEvent) event;
                ZWaveCommandClass.CommandClass commandClass = castEvent.getCommandClass();
                if (commandClass.getLabel().equals(ZWaveCommandClass.CommandClass.SENSOR_BINARY.getLabel())){
                    Object value = castEvent.getValue();
                    pushStatus(value.equals(255));
                }
            }
        }
    }

    @ContextEntity.State.Push(service = DoorWindowSensor.class,state =DoorWindowSensor.DOOR_WINDOW_SENSOR_OPENING_DETECTCION)
    public boolean pushStatus(boolean newStatus){
        return newStatus;
    }
}
