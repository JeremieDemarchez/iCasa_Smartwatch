package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.context.annotation.Entity;
import fr.liglab.adele.icasa.context.handler.creator.entity.EntityCreator;
import fr.liglab.adele.icasa.context.handler.creator.entity._EntityCreator;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


@Component
@Instantiate
@CommandProvider(namespace = "test")
public class Test {


    @Requires
    LocationManager locationManager;

    @Requires(optional = true)
    ContextEntityDescription description;

    @Requires(specification = Factory.class,optional = false,proxy = false,filter = "("+ Entity.FACTORY_OF_ENTITY+"=true)")
    Factory factoryTest;

    @EntityCreator(entity = ContextEntityImpl.class)
    _EntityCreator creatorValidComponent;

    @Command
    public void testEntity(){
        System.out.println(" HELLO " + description.hello());
    }

    @Command
    public void createWorkingEntity(){
       creatorValidComponent.createEntity("ValidEntity");
    }

    @Command
    public void createWorkingEntityWithValidConfig(){
            Map entityInit = new Hashtable<>();
            entityInit.put(ContextEntityDescription.HELLO_STATE,"initValue");
       // entityInit.put(Zone.ZONE_NAME,"initValue");
            creatorValidComponent.createEntity("ValidEntityWithValidConfig", entityInit);
    }

   @Command
   public void createZone(){
       locationManager.createZone("Kitchen", 10, 10, 10, 10, 10, 10);
   }

    @Command
    public void getZone(){
        for (String zoneid : locationManager.getZoneIds()){
            System.out.printf(" Zone Present " + zoneid);
        }
    }

    @Command
    public void testFilter(){
        System.out.println(" Filter work ? " + !(factoryTest == null));
    }



}
