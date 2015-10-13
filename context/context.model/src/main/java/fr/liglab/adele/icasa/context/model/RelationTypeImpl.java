package fr.liglab.adele.icasa.context.model;


/**
 * Created by Gerbert on 07/10/2015.
 */
public class RelationTypeImpl implements RelationType{
    protected String name;

    protected String extendStateName;

    protected boolean isAggregate;

    protected RelationCallBack relationCallBack;

    public RelationTypeImpl(RelationType relationType){
        name = relationType.getName();
        extendStateName = relationType.getExtendStateName();
        isAggregate = relationType.isAggregate();
        relationCallBack = relationType.getRelationCallBack();
    }

    public RelationTypeImpl(String name, String extendStateName, boolean isAggregate, RelationCallBack relationCallBack){
        this.name = name;
        this.extendStateName = extendStateName;
        this.isAggregate = isAggregate;
        this.relationCallBack = relationCallBack;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getExtendStateName() {
        return extendStateName;
    }

    @Override
    public boolean isAggregate() {
        return isAggregate;
    }

    @Override
    public RelationCallBack getRelationCallBack() {
        return relationCallBack;
    }

}
