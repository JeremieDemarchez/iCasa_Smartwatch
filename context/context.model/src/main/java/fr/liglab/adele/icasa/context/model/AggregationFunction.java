package fr.liglab.adele.icasa.context.model;

import java.util.List;

@FunctionalInterface
public interface AggregationFunction<T> {

    T getResult(List<ContextEntity> sources);
}
