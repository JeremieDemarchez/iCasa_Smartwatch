package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.handler.AbstractContextEntity;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true)
@Provides
@fr.liglab.adele.icasa.context.handler.ContextEntity(name = "Zone")
public class ContextEntityImpl implements ContextEntity{

    private static final Logger LOG = LoggerFactory.getLogger(ContextEntityImpl.class);



    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @ServiceProperty(name = "context.entity.state", mandatory = true)
    List<List<Object>> state;

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
    //TODO : return a copy of the list
    public List<List<Object>> getState() {
        List<List<Object>> stateCopy = new ArrayList<>();
        for (List<Object> property_array : state) {
            List copyProperty = new ArrayList<>(property_array);
            stateCopy.add(copyProperty);
        }
        return stateCopy;
    }

    @Override
    public void setState(String state, Object value) {
        //DO NOTHING
    }

    @Override
    public Map<String,Object> getStateAsMap() {
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
        return new HashMap<String,Object>();
    }
}