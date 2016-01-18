package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.annotation.Entity;
import fr.liglab.adele.icasa.context.annotation.Pull;
import fr.liglab.adele.icasa.context.annotation.Set;
import fr.liglab.adele.icasa.context.annotation.StateField;
import fr.liglab.adele.icasa.context.model.ContextEntity;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Entity(spec = ContextEntityDescription.class)
public class ContextEntityImpl implements ContextEntity,ContextEntityDescription{

    @StateField(name = "hello")
    public String hello;

    @StateField(name = "ID")
    public String id;

    @Pull(state = "ID",period = 1,unit = TimeUnit.HOURS)
    public Function idsynchro;

    @Set(state = "ID")
    public Function idsynchroSet;

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