package fr.liglab.adele.icasa.context.extensions.remote.impl;



import fr.liglab.adele.icasa.context.extensions.remote.api.ControllerConfigurator;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.*;

import java.util.HashMap;
import java.util.Map;

@Component
@Provides(specifications = ControllerConfigurator.class)
public class ControllerConfiguratorDefaultImpl implements ControllerConfigurator {

    int i =0;

    public String getEntityGroup(ContextEntity contextEntity) {
            return GROUP_DEFAULT;
    }

    public Map<String, Boolean> getGroupDefaultStates() {
        Map<String, Boolean> groupDefaultStates = new HashMap<String, Boolean>();
        groupDefaultStates.put(GROUP_DEFAULT, false);
        return groupDefaultStates;
    }
}
