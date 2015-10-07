package fr.liglab.adele.icasa.context.model;

import java.util.List;
import java.util.Map;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface ContextEntity {

    public String getId();

    public List<Object> getStateValue (String property);

    public List<List<Object>> getState();

    public void setState(String state,Object value);

    public Map<String,Object> getStateAsMap();

    public List<Object> getStateExtensionValue (String property);

    public Map<String,Object> getStateExtensionAsMap();

}
