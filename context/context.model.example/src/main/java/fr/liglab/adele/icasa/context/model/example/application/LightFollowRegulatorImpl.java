package fr.liglab.adele.icasa.context.model.example.application;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.transformation.Aggregation;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true)
@Provides
public class LightFollowRegulatorImpl implements LightFollowRegulator {

    private static final Logger LOG = LoggerFactory.getLogger(LightFollowRegulatorImpl.class);

    private final String stateProp_binarystatus = BinaryLight.BINARY_LIGHT_POWER_STATUS;
    private final String stateProp_binarymax = BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL;
    private final String stateProp_dimmerlevel = DimmerLight.DIMMER_LIGHT_POWER_LEVEL;
    private final String stateProp_dimmermax = DimmerLight.DIMMER_LIGHT_MAX_POWER_LEVEL;

    private double illuminanceFactor = 1;


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
        updateLightRegulation(aggregation);
    }

    @Modified(id = "presence")
    public void modifiedBedroomPresence(Aggregation aggregation){
        LOG.info(" Modified Presence");
        updateLightRegulation(aggregation);
    }


    private void updateLightRegulation (Aggregation aggregation){
        if(((boolean)aggregation.getResult() == true)){
            if (illuminanceFactor>=1){
                setOnAllLights();
            } else if (illuminanceFactor<=0){
                setOffAllLights();
            } else {
                setOnLightsWithFactor();
            }
        }else{
            setOffAllLights();
        }
    }

    private synchronized void setOffAllLights() {
        for (ContextEntity entity : lightEntities){
            if(!entity.getStateValue(stateProp_binarystatus).isEmpty()) {
                entity.setState(stateProp_binarystatus, false);
            } else if(!entity.getStateValue(stateProp_dimmerlevel).isEmpty()) {
                entity.setState(stateProp_dimmerlevel, (double)0);
            }
        }
    }

    private synchronized void setOnAllLights(){
        double dimmerMaxLevel;
        for (ContextEntity entity : lightEntities){
            if(!entity.getStateValue(stateProp_binarystatus).isEmpty()) {
                entity.setState(stateProp_binarystatus, true);
            } else if(!entity.getStateValue(stateProp_dimmermax).isEmpty()) {
                dimmerMaxLevel = (double)entity.getStateValue(stateProp_dimmermax).get(1);
                entity.setState(stateProp_dimmerlevel, dimmerMaxLevel);
            }
        }
    }

    private synchronized void setOnLightsWithFactor(){
        //TODO : is a map usefull to find best configuration?
        //Map<Integer, Double> illuminances = new HashMap<>();
        double illuminanceTemp;
        double illuminance = 0;
        int n = 0;
        /*get max illuminance*/
        for (ContextEntity entity : lightEntities){
            if(!entity.getStateValue(stateProp_binarymax).isEmpty()) {
                illuminanceTemp = (double)entity.getStateValue(stateProp_binarymax).get(1);
                //illuminances.put(lightEntities.indexOf(entity),illuminanceTemp);
                illuminance += illuminanceTemp;
            } else if(!entity.getStateValue(stateProp_dimmermax).isEmpty()) {
                illuminanceTemp = (double)entity.getStateValue(stateProp_dimmermax).get(1);
                //illuminances.put(lightEntities.indexOf(entity),illuminanceTemp);
                illuminance += illuminanceTemp;
            }
        }

        /*set illuminance with factor*/
        illuminance *= illuminanceFactor;
        double maxLevel;

        /*Particuliar behavior if only one lamp*/
        if (lightEntities.size()==1){
            ContextEntity entity = lightEntities.get(0);
            if(!entity.getStateValue(stateProp_binarystatus).isEmpty()) {
                maxLevel = (double)entity.getStateValue(stateProp_binarymax).get(1);
                if (illuminance >= (maxLevel/2)){ /*If it doesn't really require light, doesn't set status to on*/
                    entity.setState(stateProp_binarystatus, true);
                } else {
                    entity.setState(stateProp_binarystatus, false);
                }
            } else if(!entity.getStateValue(stateProp_dimmermax).isEmpty()) {
                maxLevel = (double)entity.getStateValue(stateProp_dimmermax).get(1);
                maxLevel = Math.min(maxLevel, illuminance);
                entity.setState(stateProp_dimmerlevel, maxLevel);
            }

        } else {
            for (ContextEntity entity : lightEntities){
                if(!entity.getStateValue(stateProp_binarystatus).isEmpty()) {
                    maxLevel = (double)entity.getStateValue(stateProp_binarymax).get(1);
                    if (illuminance >= maxLevel){
                        entity.setState(stateProp_binarystatus, true);
                        illuminance -= maxLevel;
                    } else {
                        entity.setState(stateProp_binarystatus, false);
                    }
                }
            }

            for (ContextEntity entity : lightEntities){
                if(!entity.getStateValue(stateProp_dimmermax).isEmpty()) {
                    maxLevel = (double)entity.getStateValue(stateProp_dimmermax).get(1);
                    maxLevel = Math.min(maxLevel, illuminance);
                    entity.setState(stateProp_dimmerlevel, maxLevel);
                    illuminance -= maxLevel;
                }
            }
        }
    }

    @Override
    public void setIlluminanceFactor (double illuminanceFactor){
        if (illuminanceFactor>=1){
            this.illuminanceFactor = 1;
        } else if (illuminanceFactor<=0){
            this.illuminanceFactor = 0;
        } else {
            this.illuminanceFactor = illuminanceFactor;
        }
        updateLightRegulation(presenceAggregation);
    }
}
