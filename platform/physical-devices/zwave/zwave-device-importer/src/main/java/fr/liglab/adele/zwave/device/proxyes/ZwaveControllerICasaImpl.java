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
import fr.liglab.adele.zwave.device.api.ZwaveControllerICasa;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import fr.liglab.adele.zwave.device.api.ZwaveRepeater;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.openhab.binding.zwave.internal.protocol.ZWaveController;
import org.openhab.binding.zwave.internal.protocol.ZWaveEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ContextEntity(services = {ZwaveControllerICasa.class,ZwaveDevice.class, ZwaveRepeater.class})
public class ZwaveControllerICasaImpl implements ZwaveRepeater,ZwaveDevice,ZwaveControllerICasa{

    /**
     * STATES
     */
    @ContextEntity.State.Field(service = ZwaveControllerICasa.class,state = ZwaveControllerICasa.CONTROLLER,directAccess = true)
    private ZWaveController controller;

    @ContextEntity.State.Field(service = ZwaveControllerICasa.class,state = ZwaveControllerICasa.MASTER)
    private boolean master;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_NEIGHBORS)
    private List<Integer> neighbors;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_ID)
    private int zwaveId;

    /**
     *
     * SERVICES
     */
    @Override
    public boolean isMaster() {
        return master;
    }

    @Override
    public synchronized void addEventListener(ZWaveEventListener eventListener) {
        controller.addEventListener(eventListener);
    }

    @Override
    public synchronized void removeEventListener(ZWaveEventListener eventListener) {
        controller.removeEventListener(eventListener);
    }

    @Override
    public List<Integer> getNeighbors() {
        return neighbors;
    }

    @Override
    public int getZwaveId() {
        return zwaveId;
    }

    /**
     * SYNCHRO
     */

    @ContextEntity.State.Pull(service = ZwaveControllerICasa.class,state = ZwaveControllerICasa.MASTER)
    Supplier<Boolean> pullMaster=()-> true;

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
