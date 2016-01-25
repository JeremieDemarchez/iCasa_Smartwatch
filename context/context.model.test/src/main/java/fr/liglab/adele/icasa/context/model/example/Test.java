package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.context.annotation.Entity;
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

    @Requires(optional = true)
    ContextEntityDescription description;

    @Requires(specification = Factory.class,optional = false,proxy = false,filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.ContextEntityImpl)")
    Factory factory;

    @Requires(specification = Factory.class,optional = false,proxy = false,filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.ContextEntityImplPrime)")
    Factory factoryNotWorking;

    @Requires(specification = Factory.class,optional = false,proxy = false,filter = "("+ Entity.FACTORY_OF_ENTITY+"=true)")
    Factory factoryTest;


    @Command
    public void testEntity(){
        System.out.println(" HELLO " + description.hello());
    }

    @Command
    public void createWorkingEntity(){
        try {
            Dictionary param = new Hashtable<>();
            param.put(ContextEntity.CONTEXT_ENTITY_ID,"test");
            factory.createComponentInstance(param);
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            unacceptableConfiguration.printStackTrace();
        } catch (MissingHandlerException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Command
    public void createWorkingEntityWithValidConfig(){
        try {
            Dictionary param = new Hashtable<>();
            param.put(ContextEntity.CONTEXT_ENTITY_ID,"testValid");
            Dictionary entityInit = new Hashtable<>();
            entityInit.put(ContextEntityDescription.HELLO_STATE,"initValue");
            param.put("context.entity.init",entityInit);
            factory.createComponentInstance(param);
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            unacceptableConfiguration.printStackTrace();
        } catch (MissingHandlerException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Command
    public void createWorkingEntityWithInValidConfig(){
        try {
            Dictionary param = new Hashtable<>();
            param.put(ContextEntity.CONTEXT_ENTITY_ID,"testInValidConfig");
            Dictionary entityInit = new Hashtable<>();
            entityInit.put("initValue","initValue");
            param.put("context.entity.init",entityInit);

            factory.createComponentInstance(param);
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            unacceptableConfiguration.printStackTrace();
        } catch (MissingHandlerException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Command
    public void createNotWorkingEntity(){
        try {
            factoryNotWorking.createComponentInstance(new Hashtable<>());
        } catch (UnacceptableConfiguration unacceptableConfiguration) {
            unacceptableConfiguration.printStackTrace();
        } catch (MissingHandlerException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Command
    public void testFilter(){
        System.out.println(" Filter work ? " + !(factoryTest == null));
    }



}
