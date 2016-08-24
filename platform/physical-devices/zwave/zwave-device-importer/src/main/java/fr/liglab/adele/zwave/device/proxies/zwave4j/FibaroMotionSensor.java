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
package fr.liglab.adele.zwave.device.proxies.zwave4j;

import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.cream.annotations.behavior.Behavior;

import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.motion.MotionSensor;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.LocatedObjectBehaviorProvider;

import fr.liglab.adele.zwave.device.api.ZwaveController;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ContextEntity(services = {ZwaveDevice.class, MotionSensor.class})
@Behavior(id="LocatedBehavior",spec = LocatedObject.class,implem = LocatedObjectBehaviorProvider.class)

public class FibaroMotionSensor implements MotionSensor, ZwaveDevice, GenericDevice  {

    /**
     * iPOJO Require
     */
    @Requires(optional = false, proxy=false)
    private ZwaveController controller;

    private static final Logger LOG = LoggerFactory.getLogger(FibaroMotionSensor.class);

    /**
     * STATES
     */
    @ContextEntity.State.Field(service = GenericDevice.class,state = GenericDevice.DEVICE_SERIAL_NUMBER)
    private String serialNumber;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.NEIGHBORS)
    private List<Integer> neighbors;

    /**
     * Neighbors Synchro
     */
    @ContextEntity.Relation.Field(value = "isZwaveNeighbor",owner = ZwaveDevice.class)
    @Requires(id="zwavesNeighbors",specification=ZwaveDevice.class,optional=true)
    private List<ZwaveDevice> zwaveDevices;

    @Bind(id = "zwavesNeighbors")
    private void bindZDevice(ZwaveDevice device){
        pushNeighbors();
    }

    @Unbind(id= "zwavesNeighbors")
    private void unbindZDevice(ZwaveDevice device){
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
    
    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.HOME_ID)
    private Integer zwaveHomeId;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.NODE_ID)
    private Integer zwaveNodeId;

    /**
     * Services
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

    @Validate
    private void start() {
    }

    @Invalidate
    private void stop() {
    }



}
