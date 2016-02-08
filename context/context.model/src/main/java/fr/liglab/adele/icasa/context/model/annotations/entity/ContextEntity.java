package fr.liglab.adele.icasa.context.model.annotations.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContextEntity {

	/**
	 * The list of provided context services
	 */
    Class<?> [] services();

    /**
     * The name of the service property used to describe context provider factories
     */
    public static final String ENTITY_CONTEXT_SERVICES = "factory.context.entity.services";
}
