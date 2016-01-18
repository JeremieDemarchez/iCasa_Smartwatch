package fr.liglab.adele.icasa.context.handler.creator.entity;

import java.util.Set;

/**
 * Created by Eva on 14/12/2015.
 */
public interface EntityCreatorManagementInterface {

    Set<String> getImplementations();

    Set<String> getPendingInstances(String implementation);

    boolean getImplentationState(String implementation);

    boolean switchCreation(String implementation, boolean enable);


}
