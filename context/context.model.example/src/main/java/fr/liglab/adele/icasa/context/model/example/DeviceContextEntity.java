package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Provides
public class DeviceContextEntity implements ContextEntity{

    private static final Logger LOG = LoggerFactory.getLogger(DeviceContextEntity.class);


    @Requires(specification = Relation.class, id = "context.entity.relation", optional = true,
            filter = "(relation.end.id=${context.entity.id})")
    List<Relation> relations;

    @Property(name = "context.entity.id",mandatory = true)
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
    public void addStateValue(String property, Object value, boolean isAggregated) {
        List<List<Object>>state = new ArrayList<>();
        state.addAll(this.state);

        boolean property_exists = false;
        for (List<Object> property_array : state){
            if (property_array.get(0)==property){
                property_exists = true;
                if (!isAggregated){
                    property_array.clear();
                    property_array.add(property);
                    property_array.add(value);
                } else {
                    if (!property_array.contains(value)){
                        property_array.add(value);
                    }
                }
            }
        }
        if (!property_exists){
            List<Object> property_array = new ArrayList<>();
            property_array.add(property);
            property_array.add(value);
            state.add(property_array);
        }

        this.state = state;
    }

    @Override
    public void removeStateValue(String property, Object value) {
        List<List<Object>>state = new ArrayList<>();
        state.addAll(this.state);

        int index = -1;
        for (List<Object> property_array : state){
            if (property_array.get(0)==property){
                index = state.indexOf(property_array);
                if (property_array.contains(value)){

                    property_array.remove(value);
                }
            }
        }

        /*If the property hasn't value any more, it is cleared*/
        if (index>=0){
            if(state.get(index).size()==1){
                state.remove(index);
            }
        }

        this.state = state;
    }


    public List<Object> getStateValue(String property) {
        List<Object> value = null;

        for (List<Object> property_array : state) {
            if (property_array.get(0) == property) {
                value = property_array;
            }
        }
        return value;
    }

    @Override
    public List<List<Object>> getState() {
        return state;
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


    @Bind(id = "context.entity.relation")
    public synchronized void bindRelations (Relation relation) {
        LOG.info("Entity : " + name + " BIND relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );
        /*state actualisation*/
        addStateValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue(),relation.getExtendedState().isAggregate());
    }

    @Modified(id = "context.entity.relation")
    public synchronized void modifiedRelations(Relation relation) {
        LOG.info("Modified !!");
        LOG.info("Entity : " + name + " modified relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );
        addStateValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue(), relation.getExtendedState().isAggregate());
    }

    @Unbind(id = "context.entity.relation")
    //TODO : INSPECT EXCEPTION
    public synchronized void unbindRelations (Relation relation) {
        LOG.info("Entity : " + name + " UNBIND relation " + relation.getName()  );
        removeStateValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue());

    }
}