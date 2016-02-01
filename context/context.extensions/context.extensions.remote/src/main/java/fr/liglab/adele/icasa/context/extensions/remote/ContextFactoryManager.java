package fr.liglab.adele.icasa.context.extensions.remote;

import java.util.Set;

/**
 * Created by aygalinc on 29/01/16.
 */
public interface ContextFactoryManager {

    Set<String> getContextFactoriesIds();

    Set<String> getSetOfContextServices(String factoryId) throws NullPointerException;

}
