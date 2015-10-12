package fr.liglab.adele.icasa.context.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface ContextEntity {

    public String getId();

    public Object getStateValue (String property);

    public void setState(String state,Object value);

    public Map<String,Object> getState();

    public List<Object> getStateExtensionValue (String property);

    public Map<String,Object> getStateExtensionAsMap();

    public void pushState(String state,Object value);

}
