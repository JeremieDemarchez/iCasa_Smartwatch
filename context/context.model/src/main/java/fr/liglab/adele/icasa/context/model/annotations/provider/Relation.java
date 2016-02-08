package fr.liglab.adele.icasa.context.model.annotations.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public interface Relation {

	public interface Creator {
	    
		@Target(ElementType.FIELD)
		public @interface Field {
			String value();
		}
    
	}	

}
