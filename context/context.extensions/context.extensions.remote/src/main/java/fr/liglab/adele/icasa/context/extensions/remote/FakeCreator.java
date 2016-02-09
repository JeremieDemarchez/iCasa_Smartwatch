package fr.liglab.adele.icasa.context.extensions.remote;

import fr.liglab.adele.icasa.context.model.introspection.EntityCreatorHandlerIntrospection;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by aygalinc on 29/01/16.
 */
public class FakeCreator implements EntityCreatorHandlerIntrospection {
    private final String myName;

    private final String myImplem;

    private boolean enabled;


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
        return enabled;
    }

    public boolean switchCreation(String implementation, boolean enable) {
        enabled = enable;
        return enabled;
    }

    public boolean deleteAllInstancesOf(String implementation) {
        return false;
    }
}
