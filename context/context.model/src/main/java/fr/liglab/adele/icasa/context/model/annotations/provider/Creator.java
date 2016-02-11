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
		    
		Set<String> getInstances();
		
		/**
		 * Return the created instance of the context entity
		 * 
		 * TODO should return a Future because if the creator is disabled the actual instance
		 * may not be available
		 */
		E getInstance(String id);

		/**
		 * Creates a new instance of the context entity
		 * 
		 * TODO If there are errors at instantiation, and the creator is disabled how to notify
		 * the client?
		 */
	    void create(String id, Map<String, Object> initialization);

	    void create(String id);

	    void delete(String id);
	
	    void deleteAll();
	}
	
	/**
	 * A factory object used to create relations between entities of the specified type
	 * 
	 * @param <S> The source entity type
	 * @param <T> The target entity type
	 */
	public interface Relation<S,T> {
		
		Set<String> getInstances();
		
	    void create(String sourceId, String targetId);

	    void delete(String sourceId, String targetId);
	    
	    void delete(String id);
	
	    void deleteAll();
		
	}	
}
