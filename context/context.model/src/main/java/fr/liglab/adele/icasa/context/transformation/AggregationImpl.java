package fr.liglab.adele.icasa.context.transformation;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Component
@Provides
public class AggregationImpl implements Aggregation {

    @Requires(id = "aggregation.sources", optional = false)
    List<ContextEntity> sources;

    @Property(name = "aggregation.name",mandatory = true)
    String name;

    @Property(name = "aggregation.source.filter", mandatory = true)
    String filter;

    @Property(name = "aggregation.sources.id", mandatory = true)
    List<String> sourcesId;


    private final AggregationFunction m_aggregationFunction;


    public AggregationImpl(@Property(name = "aggregation.function", mandatory = true, immutable = true) AggregationFunction aggregationFunction) {
        m_aggregationFunction = aggregationFunction;
    }

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    @Bind(id = "aggregation.sources", aggregate = true)
    public void bindContextEntities (ContextEntity contextEntity) {
        List<String> sourcesId = new ArrayList<>();
        sourcesId.addAll(this.sourcesId);
        sourcesId.add(contextEntity.getId());
        this.sourcesId = sourcesId;
    }

    @Unbind(id = "aggregation.sources")
    public void unbindContextEntities (ContextEntity contextEntity) {
        List<String> sourcesId = new ArrayList<>();
        sourcesId.addAll(this.sourcesId);
        sourcesId.remove(contextEntity.getId());
        this.sourcesId = sourcesId;
    }

    @Override
    public String getId() {
        return getName() + getFilter();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public List<String> getSources() {
        return sourcesId;
    }

    @Override
    public synchronized Object getResult() {
        return m_aggregationFunction.getResult(sources);
    }

}
