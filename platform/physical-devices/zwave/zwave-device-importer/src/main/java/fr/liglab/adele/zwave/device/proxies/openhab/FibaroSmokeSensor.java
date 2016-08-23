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

import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;

import java.util.ArrayList;
import java.util.List;

@ContextEntity(services = {ZwaveDevice.class})
public class FibaroSmokeSensor implements ZwaveDevice{

    /**
     * STATES
     */
    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.NEIGHBORS)
    private List<Integer> neighbors;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.HOME_ID)
    private Integer zwaveHomeId;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.NODE_ID)
    private Integer zwaveNodeId;

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
}