package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.annotation.EntityType;

@EntityType(states = {"hello"})
public interface ContextEntityDescription extends ContextEntityFirstLevel {

    public static final String HELLO_STATE = "hello";

    String hello();
}
