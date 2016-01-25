package fr.liglab.adele.icasa.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Apply {

    String state();

}
