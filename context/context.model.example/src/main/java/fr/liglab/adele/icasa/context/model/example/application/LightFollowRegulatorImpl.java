package fr.liglab.adele.icasa.context.model.example.application;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.transformation.Aggregation;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(immediate = true)
public class LightFollowRegulatorImpl {

    private static final Logger LOG = LoggerFactory.getLogger(LightFollowRegulatorImpl.class);

    @Requires(id = "presence",optional = false)
    Aggregation presenceAggregation;

    @Requires(id = "lights" , specification = ContextEntity.class,optional = true)
    List<ContextEntity> lightEntities;

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    @Bind(id = "presence")
    public void bindBedroomPresence(Aggregation aggregation){
        LOG.info(" Bind Presence");
        if(((boolean)aggregation.getResult() == true)){
            for (ContextEntity entity : lightEntities){
                LOG.info("BIND TURN ON " + entity.getId());
            }
        }else{
            for (ContextEntity entity : lightEntities){
                LOG.info("BIND TURN OFF " + entity.getId());
            }
        }
    }

    @Modified(id = "presence")
    public void modifiedBedroomPresence(Aggregation aggregation){
        LOG.info(" Modified Presence");
        if(((boolean)aggregation.getResult() == true)){
            for (ContextEntity entity : lightEntities){
                LOG.info("MODIFIED TURN ON " + entity.getId());
            }
        }else{
            for (ContextEntity entity : lightEntities){
                LOG.info("MODIFIED TURN OFF " + entity.getId());
            }
        }
    }
}
