package fr.liglab.adele.icasa.context.extensions.remote;

import fr.liglab.adele.icasa.context.annotation.Entity;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.*;

import java.util.*;

@Component(immediate = true)
@Instantiate
@Provides(specifications = ContextFactoryManager.class)
public class ContextFactoryManagerImpl implements ContextFactoryManager {


    @Requires(id = "factories",specification = Factory.class,optional = true,proxy = false,filter = "("+ Entity.FACTORY_OF_ENTITY+"=true)")
    private List<Factory> myFactories;

    private final Map<String,Set<String>> myContextFactoriesWithContextSpec = new HashMap<String, Set<String>>();

    @Bind(id = "factories" )
    public synchronized void bindFactories(Factory factory,Map<String,Object> properties){
        String[] specifications = (String[]) properties.get(Entity.FACTORY_OF_ENTITY_TYPE);
        List<String> listOfSpecifications = Arrays.asList(specifications);
        Set<String> setOfSpecification = new HashSet<>(listOfSpecifications);
        myContextFactoriesWithContextSpec.put(factory.getName(),setOfSpecification);
    }

    @Unbind(id = "factories")
    public synchronized void unbindFactories(Factory factory){
        myContextFactoriesWithContextSpec.remove(factory.getName());
    }


    @Validate
    public synchronized void start(){

    }


    @Invalidate
    public synchronized void stop(){

    }


    public synchronized Set<String> getContextFactoriesIds() {
        return new HashSet<String>(myContextFactoriesWithContextSpec.keySet());
    }

    public synchronized Set<String> getSetOfContextServices(String factoryId) throws NullPointerException{
        return new HashSet<String>(myContextFactoriesWithContextSpec.get(factoryId));
    }
}
