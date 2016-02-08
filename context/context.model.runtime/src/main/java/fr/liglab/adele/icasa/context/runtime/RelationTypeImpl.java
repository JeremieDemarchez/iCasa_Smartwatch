package fr.liglab.adele.icasa.context.runtime;


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

    @Override
    public String toString() {
        /*The definition of equivalence can be modified*/
        return name + "." + extendStateName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RelationType) {
            RelationType relationType = (RelationType) o;
            if (this.toString().equals(relationType.toString())) {
                /*The definition of equivalence can be modified*/
                return true;
            } else {}
        } else {}
        return false;
    }
}
