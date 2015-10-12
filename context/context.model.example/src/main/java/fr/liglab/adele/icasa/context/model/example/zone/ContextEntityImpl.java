package fr.liglab.adele.icasa.context.model.example.zone;

import fr.liglab.adele.icasa.context.handler.synchronization.Pull;
import fr.liglab.adele.icasa.context.handler.synchronization.State;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component(immediate = true)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State(states={"area","zone.name"})
public class ContextEntityImpl implements ContextEntity{

    private static final Logger LOG = LoggerFactory.getLogger(ContextEntityImpl.class);

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    private String zoneName = "Fake";

    private Double size = 93.5;

    @Pull(state = "area")
    private final Function getZoneName = (Object obj)->{
        return zoneName;
    };


    @Pull(state = "zone.name")
    private final Function getSize = (Object obj)->{
        return size;
    };




























    private final Map<String,Object> injectedState = new HashMap<>();

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

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
        return injectedState;
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