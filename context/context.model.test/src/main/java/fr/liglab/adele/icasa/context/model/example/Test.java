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

import java.util.Hashtable;
import java.util.List;


@Component
@Instantiate
@CommandProvider(namespace = "test")
public class Test {

    @Requires(specification = Factory.class,optional = false,proxy = false,filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.ContextEntityImpl)")
    Factory factory;

    @Requires(specification = Factory.class,optional = false,proxy = false,filter = "(factory.name=fr.liglab.adele.icasa.context.model.example.ContextEntityImplPrime)")
    Factory factoryNotWorking;

    @Requires(specification = Factory.class,optional = false,proxy = false,filter = "("+ Entity.FACTORY_OF_ENTITY+"=true)")
    Factory factoryTest;

    @Command
    public void createWorkingEntity(){
        try {
            factory.createComponentInstance(new Hashtable<>());
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
