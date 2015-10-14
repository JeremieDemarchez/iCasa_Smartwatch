package fr.liglab.adele.icasa.context.handler.synchronization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Set {

    String state();

}
