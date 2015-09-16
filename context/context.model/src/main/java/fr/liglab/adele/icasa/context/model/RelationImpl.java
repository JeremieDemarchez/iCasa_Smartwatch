package fr.liglab.adele.icasa.context.model;

import org.apache.felix.ipojo.annotations.*;

@Component
@Provides
public class RelationImpl implements Relation {

    @Requires(id = "relation.source",optional = false)
    ContextEntity source;

    @Requires(id = "relation.end",optional = false)
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
        return sourceId;
        //NE FONCTIONNE PAS AVEC LES FILTRES DANS LES CONTEXT ENTITY
        //LE SYSTEME PLANTE QUAND L AFFICHAGE WEB DU CONTEXT EST ACTUALISE
        //return source.getId();
    }

    @Override
    public String getEnd() {
        return endId;
        //NE FONCTIONNE PAS AVEC LES FILTRES DANS LES CONTEXT ENTITY
        //LE SYSTEME PLANTE QUAND L AFFICHAGE WEB DU CONTEXT EST ACTUALISE
        //return end.getId();
    }
}
