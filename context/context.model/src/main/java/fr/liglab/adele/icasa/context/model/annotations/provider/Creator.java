package fr.liglab.adele.icasa.context.model.annotations.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Set;

/**
 * This interface groups all annotations useful to context entity provider
 *
 */
public interface Creator {

    
	/**
	 * Annotation to allow automatic injection of creator factories
	 *
	 */
	@Target(ElementType.FIELD)
	public @interface Field {
		
		public static final String NO_PARAMETER = "";
		
		String value() default NO_PARAMETER;
	}
    

	/**
	 * A factory object used to create context entities of the specified type
	 * 
	 * @param <E> The entity type
	 */
	public interface Entity<E> {
		    
		Set<String> getEntityIdsCreated();
	
	    void createEntity(String id);
	
	    void createEntity(String id, Map<String, Object> initialization);
	
	    void deleteEntity(String id);
	
	    void deleteAllEntities();
	}
	
	/**
	 * A factory object used to create relations between entities of the specified type
	 * 
	 * @param <S> The source entity type
	 * @param <T> The target entity type
	 */
	public interface Relation<S,T> {
	}	
}
