package fr.liglab.adele.icasa.context.extensions.remote.api;

import fr.liglab.adele.icasa.context.model.ContextEntity;

import java.util.Map;


public interface ControllerConfigurator {

    String GROUP_DEFAULT = "default_group";

    String getEntityGroup(ContextEntity contextEntity);

    Map<String, Boolean> getGroupDefaultStates();

}
