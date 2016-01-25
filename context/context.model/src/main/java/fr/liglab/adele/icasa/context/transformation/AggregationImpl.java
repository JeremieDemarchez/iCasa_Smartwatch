package fr.liglab.adele.icasa.context.transformation;

import fr.liglab.adele.icasa.context.handler.creator.relation.RelationCreator;
import fr.liglab.adele.icasa.context.handler.creator.relation._RelationCreator;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import fr.liglab.adele.icasa.context.model.RelationImpl;
import fr.liglab.adele.icasa.context.model.RelationType;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component
@Provides
public class AggregationImpl /*implements Aggregation */{
    /*TODO PAS A JOUR!!!*/

    @RelationCreator(relation= RelationImpl.class)
    private _RelationCreator m_relationCreator;

    @Requires(id = "aggregation.sources", optional = true)
    List<ContextEntity> sources;

    @Property(name = "aggregation.source.filter", mandatory = true)
    String filter;

    private final AggregationFunction m_aggregationFunction;

    private final RelationType relation_computeWith = new Relation_ComputeWith();


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
        //     m_relationCreator.createRelation(relation_computeWith, contextEntity.getId(), this.getId());
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(getResult());
        List<List<Object>> newState = new ArrayList<>();
        newState.add(property_array);
  //      state = newState;
    }

    @Modified(id = "aggregation.sources")
    public void modifiedContextEntities (ContextEntity contextEntity) {
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(getResult());
        List<List<Object>> newState = new ArrayList<>();
        newState.add(property_array);
 //       state = newState;
    }

    @Unbind(id = "aggregation.sources")
    public void unbindContextEntities (ContextEntity contextEntity) {
        //       UUID uuid = m_relationCreator.findId(relation_computeWith.getName(), contextEntity.getId(), this.getId());
        //   m_relationCreator.deleteRelation(uuid);

        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(getResult());
        List<List<Object>> newState = new ArrayList<>();
        newState.add(property_array);
    //    state = newState;
    }

//    @Override
    public String getFilter() {
        return filter;
    }

  //  @Override
    public synchronized Object getResult() {
        return m_aggregationFunction.getResult(sources);
    }

    private static final Logger LOG = LoggerFactory.getLogger(AggregationImpl.class);

    @Requires(specification = Relation.class, id = "context.entity.relation", optional = true,
            filter = "(relation.end.id=${context.entity.id})",proxy = false)
    List<Relation> relations;




}
