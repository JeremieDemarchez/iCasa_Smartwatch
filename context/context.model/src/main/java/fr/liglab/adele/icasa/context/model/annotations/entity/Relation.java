package fr.liglab.adele.icasa.context.model.annotations.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.felix.ipojo.annotations.HandlerBinding;

import fr.liglab.adele.icasa.context.model.annotations.internal.HandlerReference;

public class Relation {
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@HandlerBinding(value = HandlerReference.RELATION_HANDLER, namespace = HandlerReference.NAMESPACE)
	public @interface Field {
		
	}
}

