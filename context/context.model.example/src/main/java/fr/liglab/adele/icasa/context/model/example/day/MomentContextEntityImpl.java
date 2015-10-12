package fr.liglab.adele.icasa.context.model.example.day;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
public class MomentContextEntityImpl implements ContextEntity, MomentOfTheDayListener {

    private static final Logger LOG = LoggerFactory.getLogger(MomentContextEntityImpl.class);

    @Requires
    private MomentOfTheDayService momentOfTheDayService;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @ServiceProperty(name = "context.entity.state", mandatory = true)
    List<List<Object>> state;

    @Validate
    public void start(){
        momentOfTheDayService.register(this);
    }

    @Invalidate
    public void stop(){
        momentOfTheDayService.register(this);
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public List<Object> getStateValue(String property) {
        List<Object> value = new ArrayList<>();

        for (List<Object> property_array : state) {
            if (property_array.get(0) == property) {
                value = new ArrayList<>(property_array);
            }
        }
        return value;
    }

    @Override
    public void setState(String state, Object value) {
        //DO NOTHING
    }

    @Override
    public Map<String,Object> getState() {
        Map<String,Object> stateMap = new HashMap<String,Object>();
        for (List<Object> property_array : state){
            if (property_array.size() == 2){
                stateMap.put((String)property_array.get(0),property_array.get(1));
            }else {
                List<Object> paramsValue = new ArrayList<>();
                for (Object obj : property_array){
                    if (obj.equals(property_array.get(0))){
                        //do nothing
                    }else {
                        paramsValue.add(obj);
                    }
                }
                stateMap.put((String)property_array.get(0),paramsValue);
            }
        }
        return stateMap;
    }

    @Override
    public List<Object> getStateExtensionValue(String property) {
      return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
      return new HashMap<>();
    }

    @Override
    public void pushState(String state, Object value) {

    }

    private synchronized void replaceStateValue(String property,Object newValue,Object oldValue){
        List<List<Object>> stateCopy = new ArrayList<>(this.state);
        List<List<Object>> stateCopyShuffle = new ArrayList<>();

        for (List<Object> property_array : stateCopy){
            //TODO : to verify if there are several parameters in state
            if (property_array.get(0)==property){
                property_array.set(1,newValue);
            }
        }

        //HACK : IF THE LIST IS NOT SHUFFLE EVENT ISN4T PROPAGED TO OSGI REGISTRY
        int size = stateCopy.size();
        for(int i=0;i<size;i++){
            stateCopyShuffle.add(new ArrayList<>());
        }
        for(int i=0;i<size;i++){
            stateCopyShuffle.set(i,stateCopy.get(size-i-1));
        }

        this.state = new ArrayList<>(stateCopyShuffle);
    }

    @Override
    public void momentOfTheDayHasChanged(MomentOfTheDay newMomentOfTheDay) {
//        LOG.info("Moment of the day changed : " + newMomentOfTheDay );
        String propertyName = "currentMomentOfTheDay";
        replaceStateValue(propertyName, newMomentOfTheDay, getStateValue(propertyName));
    }
}