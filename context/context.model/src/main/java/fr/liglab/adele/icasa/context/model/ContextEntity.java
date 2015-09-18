package fr.liglab.adele.icasa.context.model;

import java.util.Hashtable;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface ContextEntity {

    public String getId();

    public void setStateValue (String property, Object value);

    public void removeStateValue (String property);

    public Object getStateValue (String property);

    public Hashtable<String,Object> getState();
}
