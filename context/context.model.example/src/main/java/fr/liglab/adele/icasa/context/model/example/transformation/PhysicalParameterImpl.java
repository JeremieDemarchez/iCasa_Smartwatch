package fr.liglab.adele.icasa.context.model.example.transformation;

import fr.liglab.adele.icasa.context.handler.synchronization.Pull;
import fr.liglab.adele.icasa.context.handler.synchronization.State;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.RelationFactory;
import fr.liglab.adele.icasa.context.model.example.zone.ZoneContextEntityImpl;
import fr.liglab.adele.icasa.context.transformation.Aggregation;
import fr.liglab.adele.icasa.context.transformation.AggregationFunction;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component(immediate = true,propagation = false)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State(states = {PhysicalParameterImpl.PHYSICAL_PARAMETER_NAME,PhysicalParameterImpl.AGGREGATION_VALUE})
public class PhysicalParameterImpl implements Aggregation {

    public final static String PHYSICAL_PARAMETER_NAME = "physical.parameter.name";

    public final static String AGGREGATION_VALUE = "aggregation.value";

    @Requires(optional = false)
    RelationFactory relationFactory;

    @Requires(id = "aggregation.sources", optional = true)
    List<ContextEntity> sources;

    /** PROPERTIES **/
    @ServiceProperty(name = "aggregation.source.filter", mandatory = true)
    String filter;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    /** ATTRIBUTES **/
    private final AggregationFunction m_aggregationFunction;

    private final String m_physicalParameterName;

    private final String m_zoneId;

    private static final Logger LOG = LoggerFactory.getLogger(PhysicalParameterImpl.class);

    public PhysicalParameterImpl(@Property(name = "aggregation.function", mandatory = true, immutable = true) AggregationFunction aggregationFunction,
                                 @Property(name = PHYSICAL_PARAMETER_NAME,mandatory = true,immutable = true) String name,
                                 @Property(name = "physical.parameter.zone", mandatory = true,immutable = true)String zoneName) {
        m_aggregationFunction = aggregationFunction;
        m_physicalParameterName = name;
        m_zoneId = zoneName;
    }

    @Pull(state = PhysicalParameterImpl.PHYSICAL_PARAMETER_NAME )
    Function getName = (Object obj) ->{
        return getPhysicalParameterName();
    };

    @Pull(state = PhysicalParameterImpl.AGGREGATION_VALUE )
    Function getAggregationValue = (Object obj) ->{
        return getResult();
    };

    private String getPhysicalParameterName(){
        return m_physicalParameterName;
    }
    @Validate
    public void start(){
        relationFactory.createRelation("isPhysicalParameterOf", this.getId(), m_zoneId, m_physicalParameterName, false, m_state -> {
            return m_state.get(AGGREGATION_VALUE);
        });
        relationFactory.createRelation("havePhysicalParameterOf",m_zoneId,this.getId(),"zone.impacted", false, m_state -> {
            return m_state.get(ZoneContextEntityImpl.ZONE_NAME);
        });
    }

    @Invalidate
    public void stop(){

    }

    @Bind(id = "aggregation.sources", aggregate = true)
    public void bindContextEntities (ContextEntity contextEntity) {
        relationFactory.createRelation("ComputeWith", contextEntity.getId(), this.getId(), "ComputeWith", true, m_state -> {
            return m_state.get(PresenceSensor.DEVICE_SERIAL_NUMBER);
        });
        pushState(AGGREGATION_VALUE,getResult());
    }

    @Modified(id = "aggregation.sources")
    public void modifiedContextEntities (ContextEntity contextEntity) {
        pushState(AGGREGATION_VALUE,getResult());
    }

    @Unbind(id = "aggregation.sources")
    public void unbindContextEntities (ContextEntity contextEntity) {
        pushState(AGGREGATION_VALUE,getResult());
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public synchronized Object getResult() {
        return m_aggregationFunction.getResult(sources);
    }



    private final Map<String,Object> injectedState = new HashMap<>();

    @Override
    public String getId() {
        return name;
    }

    @Override
    public Object getStateValue(String property) {
        return injectedState.get(property);
    }

    @Override
    public void setState(String state, Object value) {
        //DO NOTHING
    }

    @Override
    public Map<String,Object> getState() {
        return injectedState;
    }

    @Override
    public List<Object> getStateExtensionValue(String property) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
        return new HashMap<String,Object>();
    }

    @Override
    public void pushState(String state, Object value) {

    }
}
