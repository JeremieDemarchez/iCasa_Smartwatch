package fr.liglab.adele.icasa.context.handler.creator.relation;

import fr.liglab.adele.icasa.context.model.RelationType;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Eva on 14/12/2015.
 */
public interface _RelationCreator {

    UUID createRelation(RelationType relationType, String source, String end);

    void deleteRelation(UUID relationId);

    void updateRelation(UUID relationId, String newSource, String newEnd);

    /*Interface for entity creator*/
    void deleteRelationsOfEntity(String id);

    void removeRelationsOfEntity(String id);

    void retrieveRelationsOfEntity(String id);




    UUID findId(String name, String source, String end);

    /*Source + end*/
    Set<UUID> findIdsByEndpoint(String endpoint);

    Set<UUID> findIdsBySource(String source);

    Set<UUID> findIdsByEnd(String end);
}
