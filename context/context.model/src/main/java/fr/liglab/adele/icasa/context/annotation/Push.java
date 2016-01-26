package fr.liglab.adele.icasa.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by aygalinc on 26/01/16.
 */
@Target(ElementType.METHOD)
public @interface Push {

    String state();

}
