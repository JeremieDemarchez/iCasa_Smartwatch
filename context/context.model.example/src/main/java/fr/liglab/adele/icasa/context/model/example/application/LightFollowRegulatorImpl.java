package fr.liglab.adele.icasa.context.model.example.application;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.transformation.Aggregation;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(immediate = true)
public class LightFollowRegulatorImpl {

    private static final Logger LOG = LoggerFactory.getLogger(LightFollowRegulatorImpl.class);

    private final String stateProp = BinaryLight.BINARY_LIGHT_POWER_STATUS;

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
               entity.setState(stateProp,true);
            }
        }else{
            for (ContextEntity entity : lightEntities){
                LOG.info("BIND TURN OFF " + entity.getId());
                entity.setState(stateProp, false);
            }
        }
    }

    @Modified(id = "presence")
    public void modifiedBedroomPresence(Aggregation aggregation){
        LOG.info(" Modified Presence");
        if(((boolean)aggregation.getResult() == true)){
            for (ContextEntity entity : lightEntities){
                entity.setState(stateProp, true);
            }
        }else{
            for (ContextEntity entity : lightEntities){
                entity.setState(stateProp,false);
            }
        }
    }
}
