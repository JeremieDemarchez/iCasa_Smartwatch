package fr.liglab.adele.icasa.context.ipojo.module;

import static org.apache.felix.ipojo.manipulator.spi.helper.Predicates.and;
import static org.apache.felix.ipojo.manipulator.spi.helper.Predicates.on;
import static org.apache.felix.ipojo.manipulator.spi.helper.Predicates.reference;

import java.lang.annotation.ElementType;

import org.apache.felix.ipojo.manipulator.spi.AbsBindingModule;
import org.apache.felix.ipojo.manipulator.spi.BindingContext;
import org.apache.felix.ipojo.manipulator.spi.Predicate;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.entity.State;

import fr.liglab.adele.icasa.context.model.annotations.provider.Entity;
import fr.liglab.adele.icasa.context.model.annotations.provider.Relation;

/**
 * Created by aygalinc on 14/01/16.
 */
public class ContextBindingModule extends AbsBindingModule {
    /**
     * Adds the Wisdom annotation to the iPOJO manipulator.
     */
    @Override
    public void configure() {
    	
    	/*
    	 * The loader used to load the classes referenced in annotations. Notice that we try to load classes using
    	 * the class loader of this module, and if it is not possible we use a loader that creates an empty mocked-up
    	 * class that represent the referenced class. 
    	 */
    	ClassLoader classReferenceLoader = new AnnotationBuilder.ClassReferenceLoader(this.getClass().getClassLoader());
    	
    	/*
    	 * Bind the context entity annotation processors
    	 */
        bind(ContextEntity.class)
        	.to(
        		new ContextEntityProcessor(classReferenceLoader)
        );
        
        bind(State.Field.class)
        	.when(and( on(ElementType.FIELD), reference(ContextEntityProcessor.CONTEXT_ENTITY_ELEMENT).exists()))
        	.to(
        		new StateVariableFieldProcessor(classReferenceLoader)
        );

        bind(State.Pull.class)
        	.when(and( on(ElementType.FIELD), reference(ContextEntityProcessor.CONTEXT_ENTITY_ELEMENT).exists()))
        	.to(
        		new PullFieldProcessor(classReferenceLoader)
        );

        bind(State.Apply.class)
        	.when(and( on(ElementType.FIELD), reference(ContextEntityProcessor.CONTEXT_ENTITY_ELEMENT).exists()))
        	.to( 
       			new ApplyFieldProcessor(classReferenceLoader)
        );

        bind(State.Push.class)
        	.when(and( on(ElementType.METHOD), not(method().returns(Void.TYPE)), reference(ContextEntityProcessor.CONTEXT_ENTITY_ELEMENT).exists()))
        	.to(
           		new PushMethodProcessor(classReferenceLoader)
        );

    	/*
    	 * Bind the context provider annotation processors
    	 */
        bind(Entity.Creator.Field.class)
	    	.when(and( on(ElementType.FIELD), field().hasType(Entity.Creator.class)))
	    	.to( 
	   			new EntityProviderProcessor(classReferenceLoader)
    	);
        
        bind(Relation.Creator.Field.class)
	    	.when(and( on(ElementType.FIELD), field().hasType(Relation.Creator.class)))
	    	.to( 
	   			new RelationProviderProcessor(classReferenceLoader)
    	);
        

    	/*
    	 * Bind empty processors to error conditions
    	 */
        
        bind(State.Field.class)
        	.when(and( on(ElementType.FIELD), not(reference(ContextEntityProcessor.CONTEXT_ENTITY_ELEMENT).exists())))
        	.to((BindingContext context) -> 
        		error(context,"Class %s must be annotated with %s to use State injection annotation",
        			context.getWorkbench().getClassNode().name, ContextEntity.class.getSimpleName())
        );
        
        bind(State.Pull.class)
        	.when(and( on(ElementType.FIELD), not(reference(ContextEntityProcessor.CONTEXT_ENTITY_ELEMENT).exists())))
        	.to((BindingContext context) -> 
        		error(context,"Class %s must be annotated with %s to use pull annotation",
        			context.getWorkbench().getClassNode().name, ContextEntity.class.getSimpleName())
        );
        

        bind(State.Apply.class)
        	.when(and( on(ElementType.FIELD), not(reference(ContextEntityProcessor.CONTEXT_ENTITY_ELEMENT).exists())))
        	.to((BindingContext context) -> 
    			error(context,"Class %s must be annotated with %s to use aply annotation",
        			context.getWorkbench().getClassNode().name, ContextEntity.class.getSimpleName())
        );

        bind(State.Push.class)
        	.when(and( on(ElementType.METHOD), not(reference(ContextEntityProcessor.CONTEXT_ENTITY_ELEMENT).exists())))
        	.to((BindingContext context) -> 
        		error(context,"Class %s must be annotated with %s to use push injection annotation",
        			context.getWorkbench().getClassNode().name, ContextEntity.class.getSimpleName())
        );

        bind(State.Push.class)
        	.when(and( on(ElementType.METHOD), method().returns(Void.TYPE)))
        	.to((BindingContext context) -> 
    			error(context,"Push method '%s' in class %s must have a return type. The value of this return is affected in the state buffer each time the method is called.",
    					context.getMethodNode().name, context.getWorkbench().getClassNode().name)
        );
        
        bind(Entity.Creator.Field.class)
	    	.when(and( on(ElementType.FIELD), not(field().hasType(Entity.Creator.class))))
        	.to((BindingContext context) -> 
    			error(context,"Entity creator field '%s' in class %s must have type Entity.Creator",
    					context.getFieldNode().name, context.getWorkbench().getClassNode().name)
    	);
        
        bind(Relation.Creator.Field.class)
	    	.when(and( on(ElementType.FIELD), not(field().hasType(Relation.Creator.class))))
        	.to((BindingContext context) -> 
			error(context,"Entity creator field '%s' in class %s must have type Relation.Creator",
					context.getFieldNode().name, context.getWorkbench().getClassNode().name)
    	);
       
    }
    
    private static final AnnotationVisitor error(BindingContext context, String message,  Object... args) {
        context.getReporter().error(message, args);
    	return null;
    }
    
    private static final Predicate not(Predicate predicate) {
    	return context -> ! predicate.matches(context);
    }

    
    public static Method method() {
    	return new Method();
    } 
    
    public static class Method {
        /**
         * Restrict execution if the supported {@literal MethidNode} has the given return type.
         */
        public Predicate returns(final Class<?> expected) {
            return 	context ->	context.getMethodNode() != null && 
            					Type.getReturnType(context.getMethodNode().desc).equals(Type.getType(expected));
            
        }
    }

    public static Field field() {
    	return new Field();
    } 
    
    public static class Field {
        /**
         * Restrict execution if the supported {@literal FieldNode} has the given type.
         */
        public Predicate hasType(final Class<?> expected) {
            return 	context ->	context.getFieldNode() != null && 
            					Type.getType(context.getFieldNode().desc).equals(Type.getType(expected));
            
        }
    }
    
}

