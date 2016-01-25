package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**@Component(immediate = true)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State**/
public class ContextEntityImpl {

    private static final Logger LOG = LoggerFactory.getLogger(ContextEntityImpl.class);

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

}