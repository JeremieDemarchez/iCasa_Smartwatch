package fr.liglab.adele.icasa.context.model.introspection;

import java.util.Set;

/**
 * Created by Eva on 14/12/2015.
 */
public interface EntityCreatorHandlerIntrospection {

    String getAttachedComponentInstanceName();

    Set<String> getImplementations();

    Set<String> getPendingInstances(String implementation);

    boolean getImplentationState(String implementation);

    boolean switchCreation(String implementation, boolean enable);

    boolean deleteAllInstancesOf(String implementation);
}
