package fr.liglab.adele.icasa.context.model;

/**
 * Created by Gerbert on 07/10/2015.
 */
public interface RelationType {

    String getName();

    String getExtendStateName();

    boolean isAggregate();

    RelationCallBack getRelationCallBack();
}
