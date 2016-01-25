package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.annotation.Entity;
import fr.liglab.adele.icasa.context.annotation.StateField;
import fr.liglab.adele.icasa.context.model.ContextEntity;

import java.util.Map;

@Entity(spec = ContextEntityDescription.class)
public class ContextEntityImplPrime implements ContextEntityDescription{

    @StateField(name = "hello")
    public String hello;

    @Override
    public String hello() {
        return null;
    }

    @Override
    public String getSerialNumber() {
        return null;
    }
}