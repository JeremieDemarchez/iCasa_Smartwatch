package fr.liglab.adele.icasa.context.handler.creator.entity;

import java.util.Map;

/**
 * Created by Eva on 14/12/2015.
 */
public interface _EntityCreator {

    void createEntity(String id);

    void createEntity(String id, Map<String, Object> initialization);

    void deleteEntity(String id);

    void deleteAllEntities();
}
