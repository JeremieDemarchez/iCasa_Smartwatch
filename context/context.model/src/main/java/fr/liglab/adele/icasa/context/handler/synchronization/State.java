package fr.liglab.adele.icasa.context.handler.synchronization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface State {

    String[] states() default {};

}
