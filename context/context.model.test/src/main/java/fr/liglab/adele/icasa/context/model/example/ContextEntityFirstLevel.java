package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.annotation.EntityType;

@EntityType(states = {"serial.number"})
public interface ContextEntityFirstLevel {

    String getSerialNumber();
}
