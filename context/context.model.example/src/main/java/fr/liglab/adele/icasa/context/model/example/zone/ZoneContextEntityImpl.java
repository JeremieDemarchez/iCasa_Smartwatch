package fr.liglab.adele.icasa.context.model.example.zone;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.context.handler.synchronization.Pull;
import fr.liglab.adele.icasa.context.handler.synchronization.State;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.ZoneListener;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

@Component(immediate = true)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State(states={ZoneContextEntityImpl.ZONE_AREA,ZoneContextEntityImpl.ZONE_NAME})
public class ZoneContextEntityImpl implements ContextEntity,ZoneListener{

    public static final String ZONE_NAME = "zone.name";

    public static final String ZONE_AREA = "area";

    private static final Logger LOG = LoggerFactory.getLogger(ZoneContextEntityImpl.class);

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @Requires
    ContextManager manager;

    @Pull(state = ZONE_AREA)
    private final Function getZoneName = (Object obj)->{
        return manager.getZone(name).getVariableValue("Area");
    };


    @Pull(state = ZONE_NAME)
    private final Function getSize = (Object obj)->{
        return name;
    };

    @Validate
    public void start(){
        manager.addListener(this);
    }

    @Invalidate
    public void stop(){
        manager.removeListener(this);
    }

    @Override
    public void zoneAdded(Zone zone) {

    }

    @Override
    public void zoneRemoved(Zone zone) {

    }

    @Override
    public void zoneMoved(Zone zone, Position oldPosition, Position newPosition) {

    }

    @Override
    public void zoneResized(Zone zone) {
        if (zone.getId().equals(name)){
            pushState(ZONE_AREA,zone.getVariableValue("Area"));
        }
    }

    @Override
    public void zoneParentModified(Zone zone, Zone oldParentZone, Zone newParentZone) {

    }

    @Override
    public void deviceAttached(Zone container, LocatedDevice child) {

    }

    @Override
    public void deviceDetached(Zone container, LocatedDevice child) {

    }

    @Override
    public void zoneVariableAdded(Zone zone, String variableName) {

    }

    @Override
    public void zoneVariableRemoved(Zone zone, String variableName) {

    }

    @Override
    public void zoneVariableModified(Zone zone, String variableName, Object oldValue, Object newValue) {

    }



























    private final Map<String,Object> injectedState = new HashMap<>();


    @Override
    public String getId() {
        return name;
    }

    @Override
    public Object getStateValue(String property) {
        return injectedState.get(property);
    }

    @Override
    public void setState(String state, Object value) {
        //DO NOTHING
    }

    @Override
    public Map<String,Object> getState() {
        return Collections.unmodifiableMap(injectedState);
    }

    @Override
    public List<Object> getStateExtensionValue(String property) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
        return new HashMap<String,Object>();
    }

    @Override
    public void pushState(String state, Object value) {

    }
}