package fr.liglab.adele.icasa.context.model.introspection;

import java.util.Set;

/**
 * Created by Eva on 14/12/2015.
 */
public interface RelationCreatorHandlerIntrospection {

    Set<String> getTypes();

    Set<String> getPendingInstances(String type);

    boolean getTypeState(String type);

    void switchCreation(String type, boolean enable);
}
