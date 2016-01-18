package fr.liglab.adele.icasa.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    Class spec();

    public static final String FACTORY_OF_ENTITY = "factory.of.context.entity";
    public static final String FACTORY_OF_ENTITY_VALUE = "true";

    public static final String FACTORY_OF_ENTITY_TYPE = "factory.of.context.entity.type";

}
