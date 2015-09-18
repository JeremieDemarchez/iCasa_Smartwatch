package fr.liglab.adele.icasa.context.model;

import java.util.Map;

@FunctionalInterface
public interface RelationCallBack<T> {

    T callBack(Map<String,Object> sourceState);
}
