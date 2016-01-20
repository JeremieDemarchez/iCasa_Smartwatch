package fr.liglab.adele.icasa.context.handler.creator.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by Eva on 14/12/2015.
 */
@Target(ElementType.FIELD)
public @interface EntityCreator {

    Class entity();                 /*Context Entity Implementation*/
}
