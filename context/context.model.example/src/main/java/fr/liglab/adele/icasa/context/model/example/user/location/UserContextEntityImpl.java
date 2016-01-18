package fr.liglab.adele.icasa.context.model.example.user.location;

import fr.liglab.adele.icasa.context.annotation.Pull;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
@Component(immediate = true)
@Instantiate
@Provides(specifications = {ContextEntity.class})
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State(states = {UserContextEntityImpl.USER_ID_PROP})**/
public class UserContextEntityImpl implements ContextEntity{

    public static final String USER_ID_PROP = "user.id";

    public static final String USER_ID = "Everyone_userGroup";

    private static final Logger LOG = LoggerFactory.getLogger(UserContextEntityImpl.class);

    @ServiceProperty(name = "context.entity.id",mandatory = true, value=USER_ID)
    String name;

    @Pull(state = UserContextEntityImpl.USER_ID_PROP)
    private final Function getUserName = (Object obj)->{
        return name;
    };

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }










    private final Map<String,Object> injectedState = new HashMap<>();

    private final Map<String,Object> injectedExtensionState =new HashMap<>();

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
    public Object getStateExtensionValue(String property) {
        return injectedExtensionState.get(property);
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
        return injectedExtensionState;
    }

    @Override
    public void pushState(String state, Object value) {

    }
}