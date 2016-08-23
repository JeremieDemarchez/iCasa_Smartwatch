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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.cream.annotations.entity.ContextEntity;

import org.openhab.binding.zwave.internal.protocol.ZWaveEventListener;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveCommandClass;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveMultiLevelSensorCommandClass;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveCommandClassValueEvent;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveEvent;


import fr.liglab.adele.icasa.device.power.SmartPlug;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import fr.liglab.adele.zwave.device.api.ZwaveRepeater;

@ContextEntity(services = {ZwaveDevice.class, ZwaveRepeater.class,SmartPlug.class})
public class FibaroWallPlug implements ZwaveDevice, ZwaveRepeater, ZWaveEventListener, SmartPlug {


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

    @ContextEntity.State.Field(service = SmartPlug.class,state = SmartPlug.SMART_PLUG_STATUS,value = "false")
    private boolean status;

    @ContextEntity.State.Field(service = SmartPlug.class,state = SmartPlug.SMART_PLUG_CONSUMPTION,value = "0.0")
    private float consumption;

    @Override
    public List<Integer> getNeighbors() {
        return neighbors;
    }

    @Override
    public int getHomeId() {
    	return zwaveHomeId;
    }
    
    @Override
    public int getNodeId() {
        return zwaveNodeId;
    }

    @Override
    public boolean isOn() {
        return status;
    }

    @Override
    public float currentConsumption() {
        return consumption;
    }

    /**
     * LifeCycle
     */
    @Validate
    public void start(){
        controller.addEventListener(this);
    }

    @Invalidate
    public void stop(){
        controller.addEventListener(this);
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
            if (event instanceof ZWaveCommandClassValueEvent) {
                ZWaveCommandClassValueEvent commandClass = (ZWaveCommandClassValueEvent) event;
                if (commandClass.getCommandClass().getLabel().equals(ZWaveCommandClass.CommandClass.SWITCH_BINARY.getLabel())) {
                        pushStatus(commandClass.getValue().equals(255));
                }
            }

            if (event instanceof ZWaveMultiLevelSensorCommandClass.ZWaveMultiLevelSensorValueEvent) {
                ZWaveMultiLevelSensorCommandClass.ZWaveMultiLevelSensorValueEvent castEvent = (ZWaveMultiLevelSensorCommandClass.ZWaveMultiLevelSensorValueEvent) event;

                ZWaveMultiLevelSensorCommandClass.SensorType sensorType = castEvent.getSensorType();
                if (sensorType.getLabel().equals(ZWaveMultiLevelSensorCommandClass.SensorType.POWER.getLabel())){
                    Object value = castEvent.getValue();
                    if (value instanceof BigDecimal){
                        pushConsumption(((BigDecimal) value).floatValue());
                    }
                }
            }
        }
    }

    @ContextEntity.State.Push(service = SmartPlug.class,state =SmartPlug.SMART_PLUG_STATUS )
    public boolean pushStatus(boolean newStatus){
        return newStatus;
    }

    @ContextEntity.State.Push(service = SmartPlug.class,state =SmartPlug.SMART_PLUG_CONSUMPTION )
    public float pushConsumption(float newConso){
        return newConso;
    }

}