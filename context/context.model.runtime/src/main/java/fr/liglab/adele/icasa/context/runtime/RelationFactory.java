package fr.liglab.adele.icasa.context.runtime;

        import java.util.Set;
        import java.util.UUID;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface RelationFactory {
    /*Shouldn't be used directly by context (use Relation creator instead)*/

    /*TODO A ENLEVER!!!!!*/
    UUID createRelation(RelationType relationType, String source, String end);

    UUID createRelation(UUID uuid, RelationType relationType, String source, String end);

    void deleteRelation(UUID relationId);

    void updateRelation(UUID relationId, String newSource, String newEnd);

    UUID findId(String name, String source, String end);

    /*Source + end*/
    Set<UUID> findIdsByEndpoint(String endpoint);

    Set<UUID> findIdsBySource(String source);

    Set<UUID> findIdsByEnd(String end);
}
