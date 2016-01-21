package fr.liglab.adele.icasa.context.handler.creator.relation;

import java.util.Set;

/**
 * Created by Eva on 14/12/2015.
 */
public interface _RelationCreatorManagement {

    Set<String> getTypes();

    Set<String> getPendingInstances(String type);

    boolean getTypeState(String type);

    void switchCreation(String type, boolean enable);
}
