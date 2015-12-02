package fr.liglab.adele.icasa.context.model.example.user.location;

import fr.liglab.adele.icasa.context.model.RelationTypeImpl;
import fr.liglab.adele.icasa.context.model.example.transformation.PhysicalParameterImpl;

/**
 * Created by Gerbert on 13/10/2015.
 */
public class Relation_ComputeWithPP extends RelationTypeImpl{

    public Relation_ComputeWithPP(){
        super("ComputeWith", "ComputeWith", true, m_state -> {
            return m_state.get(PhysicalParameterImpl.AGGREGATION_NAME);
        });
    }
}
