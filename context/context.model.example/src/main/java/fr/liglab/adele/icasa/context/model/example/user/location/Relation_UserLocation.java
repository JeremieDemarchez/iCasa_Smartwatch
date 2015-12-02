package fr.liglab.adele.icasa.context.model.example.user.location;

import fr.liglab.adele.icasa.context.model.RelationTypeImpl;
import fr.liglab.adele.icasa.context.model.example.zone.ZoneContextEntityImpl;

/**
 * Created by Gerbert on 13/10/2015.
 */
public class Relation_UserLocation extends RelationTypeImpl{

    public Relation_UserLocation(){
        super("user.location", "is in", true, state->{
            return state.get(ZoneContextEntityImpl.ZONE_NAME);
        });
    }
}
