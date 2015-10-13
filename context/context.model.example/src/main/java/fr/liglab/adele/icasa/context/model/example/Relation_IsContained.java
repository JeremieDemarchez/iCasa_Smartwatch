package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.RelationTypeImpl;
import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Created by Gerbert on 13/10/2015.
 */
public class Relation_IsContained extends RelationTypeImpl{

    public Relation_IsContained(){
        super("isContained", "containedDevice", true, state ->{
            return state.get(GenericDevice.DEVICE_SERIAL_NUMBER);
        });
    }
}
