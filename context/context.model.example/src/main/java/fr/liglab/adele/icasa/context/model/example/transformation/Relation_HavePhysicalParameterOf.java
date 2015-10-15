package fr.liglab.adele.icasa.context.model.example.transformation;

import fr.liglab.adele.icasa.context.model.RelationTypeImpl;
import fr.liglab.adele.icasa.context.model.example.zone.ZoneContextEntityImpl;

/**
 * Created by Gerbert on 13/10/2015.
 */
public class Relation_HavePhysicalParameterOf extends RelationTypeImpl{

    public Relation_HavePhysicalParameterOf(){
        super("havePhysicalParameterOf", "zone.impacted", false, m_state -> {
            return m_state.get(ZoneContextEntityImpl.ZONE_NAME);
        });
    }
}