package fr.liglab.adele.icasa.context.model.example;

import org.apache.felix.ipojo.annotations.*;

@Component(immediate = true)
@Instantiate
@Provides(properties= {
        @StaticServiceProperty(name="icasa.application", type="String", value="Light.Follow.Me.Application", immutable=true)
})
public class FakeApps {

    @Requires(specification = ContextEntityDescription.class,optional = true)
    ContextEntityDescription contextEntityDescription;
}
