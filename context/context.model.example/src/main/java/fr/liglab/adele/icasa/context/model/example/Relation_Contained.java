package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.RelationTypeImpl;
import fr.liglab.adele.icasa.context.model.example.zone.ZoneContextEntityImpl;

/**
 * Created by Gerbert on 13/10/2015.
 */
public class Relation_Contained extends RelationTypeImpl{

    public Relation_Contained(){
        super("contained", "location",false, state->{
            return state.get(ZoneContextEntityImpl.ZONE_NAME);
        });
    }
}
