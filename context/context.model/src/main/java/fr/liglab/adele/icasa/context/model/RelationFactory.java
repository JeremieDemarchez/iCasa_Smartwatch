package fr.liglab.adele.icasa.context.model;

/**
 * Created by aygalinc on 15/09/15.
 */
public interface RelationFactory {

    public void createRelation(String name,String source,String end,String extendStateName,boolean isAggregate,RelationCallBack relationCallBack);

    public void deleteRelation(String name,String source,String end);

    public void updateRelation(String name, String oldSource, String oldEnd,String newSource,String newEnd);

}
