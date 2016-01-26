package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.annotation.Entity;
import fr.liglab.adele.icasa.context.annotation.Push;
import fr.liglab.adele.icasa.context.annotation.StateField;

@Entity(spec = ContextEntityDescription.class)
public class ContextEntityImpl implements ContextEntityDescription{

    @StateField(name = "hello")
    public String hello;

    @StateField(name = "serial.number")
    public String serial;

    @Override
    public String hello() {
        return hello;
    }

    @Override
    public String getSerialNumber() {
        return hello;
    }

    @Push(state = "hello")
    public void pushHello(String sentence){

    }
}