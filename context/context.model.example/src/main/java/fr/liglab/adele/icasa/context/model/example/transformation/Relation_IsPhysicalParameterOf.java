package fr.liglab.adele.icasa.context.model.example.transformation;

import fr.liglab.adele.icasa.context.model.RelationTypeImpl;

/**
 * Created by Gerbert on 13/10/2015.
 */
public class Relation_IsPhysicalParameterOf extends RelationTypeImpl{

    public Relation_IsPhysicalParameterOf(String physicalParameterName){
        super("isPhysicalParameterOf", physicalParameterName, false, m_state -> {
            return m_state.get("aggregation.value");
        });
    }
}
