package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;

@Component
@Provides
public class DeviceContextEntity implements ContextEntity{

    @ServiceProperty(name = "context.entity.id")
    String name;

    @Override
    public String getId() {
        return name;
    }
}
