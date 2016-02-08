package fr.liglab.adele.icasa.context.runtime;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface RelationCallBack<T> {

    T callBack(Map<String,Object> sourceState);
}
