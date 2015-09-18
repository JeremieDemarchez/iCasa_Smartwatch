package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Component
@Provides
public class ZoneContextEntity implements ContextEntity{

    private static final Logger LOG = LoggerFactory.getLogger(DeviceContextEntity.class);

    @Requires(specification = Relation.class, id = "context.entity.relation", optional = true,filter="(relation.end.id =${context.entity.id})")
    List<Relation> relations;

    @Property(name = "context.entity.id",mandatory = true)
    String name;

    @Property(name = "context.entity.state", mandatory = true)
    Hashtable<String,Object> state;

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
    public void setStateValue(String property, Object value) {
        state.put(property, value);
    }

    @Override
    public void removeStateValue(String property) {
        state.remove(property);
    }

    @Override
    public Object getStateValue(String property) {
        return state.get(property);
    }

    @Override
    public Hashtable<String, Object> getState() {
        return state;
    }


    @Bind(id = "context.entity.relation")
    public void bindRelations (Relation relation) {
        LOG.info("Entity : " + name + " BIND relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );
        /*state actualisation*/
        if (relation.getExtendedState().isAggregate()){
            if (state.get(relation.getExtendedState().getName()) == null) {
                List<Object> values = new ArrayList<>();
                values.add(relation.getExtendedState().getValue());
                setStateValue(relation.getExtendedState().getName(), values);
            }else {
                ((List) (state.get(relation.getExtendedState().getName()))).add(relation.getExtendedState().getValue());
            }
        }else {
            setStateValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue());
        }
    }

    @Modified(id = "context.entity.relation")
    public void modifiedRelations(Relation relation) {
        LOG.info("Entity : " + name + " modified relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );

 /**       if (relation.getExtendedState().isAggregate()){
            List values = (List) state.get(relation.getExtendedState().getName());
            values.add(relation.getExtendedState().getValue());
        }else {
            setStateValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue());
        }**/
    }

    @Unbind(id = "context.entity.relation")
    public void unbindRelations (Relation relation) {
        LOG.info("Entity : " + name + " UNBIND relation " + relation.getName()  );
        removeStateValue(relation.getExtendedState().getName());
    }
}
