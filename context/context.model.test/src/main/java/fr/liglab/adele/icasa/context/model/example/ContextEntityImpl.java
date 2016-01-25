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
public class ContextEntityImpl implements ContextEntityDescription{

    @StateField(name = "hello")
    public String hello;

    @StateField(name = "ID")
    public String id;

    @StateField(name = "serial.number")
    public String serialNumber;

    @Pull(state = "ID",period = 1,unit = TimeUnit.HOURS)
    public Function idsynchro;

    @Set(state = "ID")
    public Function idsynchroSet;

    @Override
    public String hello() {
        return null;
    }

    @Override
    public String getSerialNumber() {
        return null;
    }
}