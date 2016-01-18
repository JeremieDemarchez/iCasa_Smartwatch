package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.annotation.EntityType;

@EntityType(states = {"hello","ID"})
public interface ContextEntityDescription {

    public static final String HELLO_STATE = "hello";

    public static final String ID = "ID";

    String hello();
}
