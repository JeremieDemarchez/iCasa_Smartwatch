package fr.liglab.adele.icasa.context.model;

import org.apache.felix.ipojo.annotations.*;

@Component
@Provides
public class RelationImpl implements Relation {

    @Requires(id = "relation.source",optional = false, filter="(context.entity.id=${relation.source.id})")
    ContextEntity source;

    @Requires(id = "relation.end",optional = false, filter="(context.entity.id=${relation.end.id})")
    ContextEntity end;

    @ServiceProperty(name = "relation.source.id",mandatory = true)
    public String sourceId;

    @ServiceProperty(name = "relation.end.id",mandatory = true)
    public String endId;

    @Property( name = "relation.name",mandatory = true)
    String name;

    public RelationImpl(){

    }

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSource() {
        return source.getId();
    }

    @Override
    public String getEnd() {
        return end.getId();
    }
}
