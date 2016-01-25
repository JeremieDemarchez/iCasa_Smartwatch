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
public class UserContextEntityImpl {

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
}