package fr.liglab.adele.zwave.device.proxyes;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.zwave.device.api.ZwaveDevice;
import fr.liglab.adele.zwave.device.api.ZwaveRepeater;
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
    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_NEIGHBORS)
    private List<Integer> neighbors;

    @ContextEntity.State.Field(service = ZwaveDevice.class,state = ZwaveDevice.ZWAVE_ID)
    private Integer zwaveId;

    @Override
    public List<Integer> getNeighbors() {
        return neighbors;
    }

    @Override
    public int getZwaveId() {
        return zwaveId;
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
