package fr.liglab.adele.icasa.context.model;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface RelationCallBack<T> {

    T callBack(List<List<Object>> sourceState);
}
