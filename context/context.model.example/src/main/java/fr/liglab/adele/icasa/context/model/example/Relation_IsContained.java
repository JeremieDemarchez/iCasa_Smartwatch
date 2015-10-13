package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.RelationTypeImpl;

/**
 * Created by Gerbert on 13/10/2015.
 */
public class Relation_IsContained extends RelationTypeImpl{

    public Relation_IsContained(){
        super("isContained", "Relation.ContainedDevice", true, state ->{
            return state.get("serial.number");
        });
    }
}
