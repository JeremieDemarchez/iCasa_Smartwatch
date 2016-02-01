package fr.liglab.adele.icasa.context.extensions.remote;

import fr.liglab.adele.icasa.context.handler.creator.entity.CreatorHandlerIntrospection;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by aygalinc on 29/01/16.
 */
public class FakeCreator implements CreatorHandlerIntrospection {
    private final String myName;

    private final String myImplem;


    FakeCreator(String name,String implem){
        myName = name;
        myImplem = implem;
    }

    public String getAttachedComponentInstanceName() {
        return myName;
    }

    public Set<String> getImplementations() {
        Set<String> returnSet = new HashSet<String>();
        returnSet.add(myImplem);
        return returnSet;
    }

    public Set<String> getPendingInstances(String implementation) {
        return null;
    }

    public boolean getImplentationState(String implementation) {
        return false;
    }

    public boolean switchCreation(String implementation, boolean enable) {
        return false;
    }

    public boolean deleteAllInstancesOf(String implementation) {
        return false;
    }
}
