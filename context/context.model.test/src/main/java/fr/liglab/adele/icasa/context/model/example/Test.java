package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;
import fr.liglab.adele.icasa.context.model.annotations.entity.State;
import fr.liglab.adele.icasa.context.model.annotations.provider.Entity;
import fr.liglab.adele.icasa.context.model.annotations.provider.Entity.Creator;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;

import java.util.Hashtable;
import java.util.Map;


@Component
@Instantiate
@CommandProvider(namespace = "test")
public class Test {


    @Requires
    LocationManager locationManager;

    @Requires(optional = true)
    ContextEntityDescription description;

    @Requires(specification = Factory.class,optional = false,proxy = false,filter = "("+ ContextEntity.ENTITY_CONTEXT_SERVICES+"=*)")
    Factory factoryTest;

    @Creator.Field Entity.Creator<ContextEntityImpl> creatorValidComponent;

    @Command
    public void testEntity(){
        System.out.println(" HELLO " + description.hello());
    }

    @Command
    public void testSetEntity(String value){
    	description.setHello(value);
    }
    
    @Command
    public void testPushEntity(String value){
    	description.externalNotification(value);
    }
    
    
    @Command
    public void createWorkingEntity(){
       creatorValidComponent.createEntity("ValidEntity");
    }

    @Command
    public void createWorkingEntityWithValidConfig(){
            Map<String,Object> entityInit = new Hashtable<>();
            entityInit.put(State.ID(ContextEntityDescription.class,ContextEntityDescription.HELLO),"initValue");
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
