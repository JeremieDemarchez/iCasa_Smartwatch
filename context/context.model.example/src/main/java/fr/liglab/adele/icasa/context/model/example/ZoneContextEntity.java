package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;

@Component
@Provides
public class ZoneContextEntity implements ContextEntity{

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @Override
    public String getId() {
        return name;
    }
}
