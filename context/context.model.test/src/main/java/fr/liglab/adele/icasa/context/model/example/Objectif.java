package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.annotation.Entity;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;

import java.util.Map;

/**
@Component
@Provides(specifications = {ContextEntity.class,ContextEntityDescription.class},properties = {
        @StaticServiceProperty(name= Entity.FACTORY_OF_ENTITY, type="java.lang.String",value="true",immutable = true),
        @StaticServiceProperty(name= Entity.FACTORY_OF_ENTITY_TYPE, type="java.lang.String",value="fr.liglab.adele.icasa.context.model.example.ContextEntityDescription",immutable = true)})
**/public class Objectif implements ContextEntityDescription{


    @Override
    public String hello() {
        return null;
    }

    @Override
    public String getSerialNumber() {
        return null;
    }
}
