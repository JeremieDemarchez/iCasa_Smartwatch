package fr.liglab.adele.icasa.context.annotation;

import java.lang.annotation.*;

/**
 * Created by aygalinc on 14/01/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EntityType {

    String[] states() default {};

}
