package fr.liglab.adele.icasa.context.extensions.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;

import fr.liglab.adele.icasa.context.model.introspection.EntityProvider;

/**
 * Created by Eva on 18/01/2016.
 */
@Component
@Instantiate
@CommandProvider(namespace = "creators")
public class CreatorAdministrator {

    private static final Logger LOG = LoggerFactory.getLogger(CreatorAdministrator.class);

    @Requires(specification = EntityProvider.class)
    List<EntityProvider> entityCreators;

    private final String pck = "fr.liglab.adele.icasa.context.model.";

    @Command
    public void getEntities(){

        LOG.info("Entity Implementations : ");
        Set<String> implementations = new HashSet<>();


        for (EntityProvider ec : entityCreators){
            for (String imp : ec.getProvidedEntities()){
                implementations.add(imp.replace(pck, ""));
            }
        }

        LOG.info(implementations.toString());
    }

    @Command
    public void getEntityState(String name){

        LOG.info("Entity Implementation : " + name + " - State : ");

        boolean state = false;

        for (EntityProvider ec : entityCreators){
            state = state || ec.isEnabled(pck + name);
        }

        if (state){
            LOG.info("enabled");
        } else {
            LOG.info("disabled");
        }

    }

    @Command
    public void enableEntity(String name){

        boolean result = false;
        for (EntityProvider ec : entityCreators){
            /*switch creation return true if something was enabled*/
            result = ec.enable(pck + name) || result;
        }

        if (result) {
            LOG.info("Entity " + name + " enabled");
        } else {
            LOG.info("Error enabling");
        }
    }

    @Command
    public void disableEntity(String name){

        boolean result = false;
        for (EntityProvider ec : entityCreators){
            /*switch creation return true if something was disabled*/
            result = ec.disable(pck + name) || result;
        }

        if (result) {
            LOG.info("Entity " + name + " disabled");
        } else {
            LOG.info("Error disabling");
        }
    }

    @Command
    public void deleteAll(String name){

        boolean result = false;
        for (EntityProvider ec : entityCreators){
            /*switch creation return true if something was disabled*/
            result = ec.deleteInstances(pck + name,false) || result;
        }

        if (result) {
            LOG.info("Entity " + name + " deleted");
        } else {
            LOG.info("Error deleting");
        }
    }
}
