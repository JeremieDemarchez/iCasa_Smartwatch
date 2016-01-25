package fr.liglab.adele.icasa.context.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface ContextEntity {

    public static final String CONTEXT_ENTITY_ID = "context.entity.id";

    public String getId();

    public Object getStateValue (String property);

    public Set<String> getStates();
}
