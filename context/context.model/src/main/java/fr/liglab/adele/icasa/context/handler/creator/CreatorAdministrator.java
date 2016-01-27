package fr.liglab.adele.icasa.context.handler.creator;

import fr.liglab.adele.icasa.context.handler.creator.entity.CreatorHandlerIntrospection;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import org.apache.felix.ipojo.annotations.Requires;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Eva on 18/01/2016.
 */
@Component
@Instantiate
@CommandProvider(namespace = "creators")
public class CreatorAdministrator {

    @Requires(specification = CreatorHandlerIntrospection.class)
    List<CreatorHandlerIntrospection> entityCreators;

    private final String pck = "fr.liglab.adele.icasa.context.model.";

    @Command
    public void getEntities(){

        System.out.println("Entity Implementations : ");
        Set<String> implementations = new HashSet<>();


        for (CreatorHandlerIntrospection ec : entityCreators){
            for (String imp : ec.getImplementations()){
                implementations.add(imp.replace(pck, ""));
            }
        }

        System.out.println(implementations.toString());
    }

    @Command
    public void getEntityState(String name){

        System.out.println("Entity Implementation : " + name + " - State : ");

        boolean state = false;

        for (CreatorHandlerIntrospection ec : entityCreators){
            state = state || ec.getImplentationState(pck + name);
        }

        if (state){
            System.out.println("enabled");
        } else {
            System.out.println("disabled");
        }

    }

    @Command
    public void enableEntity(String name){

        boolean result = false;
        for (CreatorHandlerIntrospection ec : entityCreators){
            /*switch creation return true if something was enabled*/
            result = ec.switchCreation(pck + name, true) || result;
        }

        if (result) {
            System.out.println("Entity " + name + " enabled");
        } else {
            System.out.println("Error enabling");
        }
    }

    @Command
    public void disableEntity(String name){

        boolean result = false;
        for (CreatorHandlerIntrospection ec : entityCreators){
            /*switch creation return true if something was disabled*/
            result = ec.switchCreation(pck + name, false) || result;
        }

        if (result) {
            System.out.println("Entity " + name + " disabled");
        } else {
            System.out.println("Error disabling");
        }
    }

    @Command
    public void deleteAll(String name){

        boolean result = false;
        for (CreatorHandlerIntrospection ec : entityCreators){
            /*switch creation return true if something was disabled*/
            result = ec.deleteAllInstancesOf(pck + name) || result;
        }

        if (result) {
            System.out.println("Entity " + name + " deleted");
        } else {
            System.out.println("Error deleting");
        }
    }
}
