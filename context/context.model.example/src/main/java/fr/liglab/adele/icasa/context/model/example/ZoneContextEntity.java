package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Component
@Provides
public class ZoneContextEntity implements ContextEntity {
    @Requires(specification = Relation.class, id = "context.entity.relation", optional = true,
            filter = "(relation.source.id=${context.entity.id})")
    List<Relation> relations;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @ServiceProperty(name = "context.entity.state", mandatory = true)
    List<List<String>> state;

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
    public void addStateValue(String property, String value) {
        List<List<String>>state = new ArrayList<>();
        state.addAll(this.state);

        boolean property_exists = false;
        for (List<String> property_array : state){
            if (property_array.get(0)==property){
                property_exists = true;
                if (!property_array.contains(value)){
                    property_array.add(value);
                }
            }
        }
        if (!property_exists){
            List<String> property_array = new ArrayList<>();
            property_array.add(property);
            property_array.add(value);
            state.add(property_array);
        }

        this.state = state;
    }

    @Override
    public void removeStateValue(String property, String value) {
        List<List<String>>state = new ArrayList<>();
        state.addAll(this.state);

        int index = -1;
        for (List<String> property_array : state){
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

    @Override
    public List<String> getStateValue(String property) {
        List<String> value = null;

        for (List<String> property_array : state) {
            if (property_array.get(0) == property) {
                value = property_array;
            }
        }
        return value;
    }

    @Override
    public List<List<String>> getState() {
        return state;
    }

    @Bind(id = "context.entity.relation",aggregate = true)
    public void bindRelations (Relation relation) {
        addStateValue(relation.getName(), relation.getEnd());
    }

    @Unbind(id = "context.entity.relation")
    public void unbindRelations (Relation relation) {
        removeStateValue(relation.getName(), relation.getEnd());
    }
}