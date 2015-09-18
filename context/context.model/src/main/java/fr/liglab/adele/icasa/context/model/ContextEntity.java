package fr.liglab.adele.icasa.context.model;

import java.util.List;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface ContextEntity {

    public String getId();

    public void addStateValue (String property, String value);

    public void removeStateValue (String property, String value);

    public List<String> getStateValue (String property);

    public List<List<String>> getState();
}
