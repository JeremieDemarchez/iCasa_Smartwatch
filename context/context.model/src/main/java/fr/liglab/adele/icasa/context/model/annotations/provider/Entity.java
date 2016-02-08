package fr.liglab.adele.icasa.context.model.annotations.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Set;

public interface Entity {

	public interface Creator<T> {
	    
		Set<String> getEntityIdsCreated();
	
	    void createEntity(String id);
	
	    void createEntity(String id, Map<String, Object> initialization);
	
	    void deleteEntity(String id);
	
	    void deleteAllEntities();
	    
		@Target(ElementType.FIELD)
		public @interface Field {
		}
    
	}	
}
