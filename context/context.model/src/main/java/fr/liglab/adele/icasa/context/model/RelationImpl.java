package fr.liglab.adele.icasa.context.model;

import org.apache.felix.ipojo.annotations.*;

@Component
@Provides
public class RelationImpl implements Relation {

    @Requires(id = "relation.source",optional = false)
    ContextEntity source;

    @Requires(id = "relation.end",optional = false)
    ContextEntity end;

    @Property( name = "relation.name")
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
