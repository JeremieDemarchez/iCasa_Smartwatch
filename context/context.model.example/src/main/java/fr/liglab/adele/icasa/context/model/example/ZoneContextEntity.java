package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.annotations.*;

import java.util.Hashtable;
import java.util.List;

@Component
@Provides
public class ZoneContextEntity implements ContextEntity{

    @Requires(specification = Relation.class, id = "context.entity.relation", optional = true)
    List<Relation> relations;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @Property(name = "context.entity.state", mandatory = true)
    Hashtable<String,String> state;

    @Override
    public String getId() {
        return name;
    }

    @Override
    public void setStateValue(String property, String value) {
        state.put(property, value);
    }

    @Override
    public void removeStateValue(String property) {
        state.remove(property);
    }

    @Override
    public String getStateValue(String property) {
        return state.get(property);
    }

    @Override
    public Hashtable<String, String> getState() {
        return state;
    }

    @Bind(id = "context.entity.relation",aggregate = true)
    public void bindRelations (Relation relation) {
        setStateValue(relation.getName(), relation.getEnd());
    }

    @Unbind(id = "context.entity.relation")
    public void unbindRelations (Relation relation) {
        removeStateValue(relation.getName());
    }
}
