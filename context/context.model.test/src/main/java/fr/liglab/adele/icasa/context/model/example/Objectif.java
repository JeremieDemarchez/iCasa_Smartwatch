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
**/public class Objectif implements ContextEntityDescription,ContextEntity{
    @Override
    public String getId() {
        return null;
    }

    @Override
    public Object getStateValue(String property) {
        return null;
    }

    @Override
    public void setState(String state, Object value) {

    }

    @Override
    public Map<String, Object> getState() {
        return null;
    }

    @Override
    public Object getStateExtensionValue(String property) {
        return null;
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
        return null;
    }

    @Override
    public void pushState(String state, Object value) {

    }

    @Override
    public String hello() {
        return null;
    }
}
