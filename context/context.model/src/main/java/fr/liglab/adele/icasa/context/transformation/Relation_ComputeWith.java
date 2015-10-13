package fr.liglab.adele.icasa.context.transformation;

import fr.liglab.adele.icasa.context.model.RelationTypeImpl;

/**
 * Created by Gerbert on 13/10/2015.
 */
public class Relation_ComputeWith extends RelationTypeImpl{

    public Relation_ComputeWith (){
        super("ComputeWith", "ComputeWith", true, m_state -> {
                    return m_state.get("device.serialNumber");
        });
    }
}
