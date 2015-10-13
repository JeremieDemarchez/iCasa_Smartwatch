package fr.liglab.adele.icasa.context.model;

import java.util.List;
import java.util.UUID;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface RelationFactory {

    public UUID createRelation(RelationType relationType, String source, String end);

    public UUID createRelation(String name, String source,String end, String extendStateName, boolean isAggregate, RelationCallBack relationCallBack);

    public void deleteRelation(UUID relationId);

    public void updateRelation(UUID relationId, String newSource, String newEnd);

    public UUID findId(String name, String source, String end);

    /*Source + end*/
    public List<UUID> findIdsByEndpoint(String endpoint);

    public List<UUID> findIdsBySource(String source);

    public List<UUID> findIdsByEnd(String end);
}
