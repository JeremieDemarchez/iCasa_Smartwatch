package fr.liglab.adele.icasa.context.handler.creator;

import fr.liglab.adele.icasa.context.handler.creator.entity.EntityCreatorManagementInterface;
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

    @Requires(specification = EntityCreatorManagementInterface.class)
    List<EntityCreatorManagementInterface> entityCreators;

    private final String pck = "fr.liglab.adele.icasa.context.model.";

    @Command
    public void getEntities(){

        System.out.println("Entity Implementations : ");
        Set<String> implementations = new HashSet<>();


        for (EntityCreatorManagementInterface ec : entityCreators){
            for (String imp : ec.getImplementations()){
                implementations.add(imp.replace(pck, ""));
            }
        }

        System.out.println(implementations.toString());
    }

    @Command
    public void getEntityState(String name){

        System.out.println("Entity Implementations : ");
        Set<String> implementations = new HashSet<>();


        for (EntityCreatorManagementInterface ec : entityCreators){
            for (String imp : ec.getImplementations()){
                implementations.add(imp.replace(pck, ""));
            }
        }

        for (EntityCreatorManagementInterface ec : entityCreators){
            ec.switchCreation(pck + name, true);
        }

        System.out.println(implementations.toString());
    }

    @Command
    public void enableEntity(String name){

        boolean result = false;
        for (EntityCreatorManagementInterface ec : entityCreators){
            result = result || ec.switchCreation(pck + name, true);
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
        for (EntityCreatorManagementInterface ec : entityCreators){
            result = result || ec.switchCreation(pck + name, false);
        }

        if (result) {
            System.out.println("Entity " + name + " disabled");
        } else {
            System.out.println("Error disabling");
        }
    }
}
