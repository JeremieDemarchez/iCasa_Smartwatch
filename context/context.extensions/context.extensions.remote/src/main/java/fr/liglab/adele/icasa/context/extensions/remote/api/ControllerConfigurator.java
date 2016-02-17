package fr.liglab.adele.icasa.context.extensions.remote.api;

import fr.liglab.adele.icasa.context.model.ContextEntity;

import java.util.Set;

public interface ControllerConfigurator {

    String GROUP_DEFAULT = "default";

    String getEntityGroup(ContextEntity contextEntity);
}
