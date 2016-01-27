package fr.liglab.adele.icasa.context.handler.creator.entity;

import java.util.Map;
import java.util.Set;

/**
 * Created by Eva on 14/12/2015.
 */
public interface Creator {

    Set<String> getEntityIdsCreated();

    void createEntity(String id);

    void createEntity(String id, Map<String, Object> initialization);

    void deleteEntity(String id);

    void deleteAllEntities();
}
