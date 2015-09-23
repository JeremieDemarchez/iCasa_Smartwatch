package fr.liglab.adele.icasa.context.transformation;

import fr.liglab.adele.icasa.context.model.ContextEntity;

import java.util.List;

@FunctionalInterface
public interface AggregationFunction<T> {

    T getResult(List<ContextEntity> sources);
}
