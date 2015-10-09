package fr.liglab.adele.icasa.context.model.example.transformation;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.RelationFactory;
import fr.liglab.adele.icasa.context.transformation.Aggregation;
import fr.liglab.adele.icasa.context.transformation.AggregationFunction;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true,propagation = true)
@Provides
@fr.liglab.adele.icasa.context.handler.ContextEntity
public class PhysicalParameterImpl implements Aggregation {

    @Requires(optional = false)
    RelationFactory relationFactory;

    @Requires(id = "aggregation.sources", optional = true)
    List<ContextEntity> sources;

    /** PROPERTIES **/
    @ServiceProperty(name = "aggregation.source.filter", mandatory = true)
    String filter;

    @ServiceProperty(name = "physical.parameter.zone", mandatory = true)
    String zoneId;

    @ServiceProperty(name = "physical.parameter.name", mandatory = true)
    String physicalParameterName;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @ServiceProperty(name = "context.entity.state", mandatory = true)
    List<List<Object>> state;

    /** ATTRIBUTES **/
    private final AggregationFunction m_aggregationFunction;

    private static final Logger LOG = LoggerFactory.getLogger(PhysicalParameterImpl.class);

    public PhysicalParameterImpl(@Property(name = "aggregation.function", mandatory = true, immutable = true) AggregationFunction aggregationFunction) {
        m_aggregationFunction = aggregationFunction;
    }

    @Validate
    public void start(){
        relationFactory.createRelation("isPhysicalParameterOf", this.getId(), zoneId, physicalParameterName, false, m_state -> {
            return m_state.get("aggregation.value");
        });
        relationFactory.createRelation("havePhysicalParameterOf",zoneId,this.getId(),"zone.impacted", false, m_state -> {
            return m_state.get("zone.name");
        });
    }

    @Invalidate
    public void stop(){

    }

    @Bind(id = "aggregation.sources", aggregate = true)
    public void bindContextEntities (ContextEntity contextEntity) {
        relationFactory.createRelation("ComputeWith", contextEntity.getId(), this.getId(), "ComputeWith", true, m_state -> {
            return m_state.get("serial.number");
        });
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(getResult());
        List<List<Object>> newState = new ArrayList<>();
        newState.add(property_array);
        state = new ArrayList<List<Object>>(newState);
    }

    @Modified(id = "aggregation.sources")
    public void modifiedContextEntities (ContextEntity contextEntity) {
        System.out.println(" MODIFIIIIIIIIIIIIIIIIIIIIIIIIIIIIIEDDD " +getStateValue("aggregation.value") +" equals" +  getResult() );
        if (getStateValue("aggregation.value").equals(getResult())){
            return;
        }
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(getResult());
        List<List<Object>> newState = new ArrayList<>();
        newState.add(property_array);
        state = new ArrayList<List<Object>>(newState);
    }

    @Unbind(id = "aggregation.sources")
    public void unbindContextEntities (ContextEntity contextEntity) {
        relationFactory.deleteRelation("ComputeWith",contextEntity.getId(),this.getId());
        List property_array = new ArrayList<>();
        property_array.add("aggregation.value");
        property_array.add(getResult());
        List<List<Object>> newState = new ArrayList<>();
        newState.add(property_array);
        state = new ArrayList<List<Object>>(newState);
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public synchronized Object getResult() {
        return m_aggregationFunction.getResult(sources);
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public List<Object> getStateValue(String property) {
        List<Object> value = new ArrayList<>();

        for (List<Object> property_array : state) {
            if (property_array.get(0) == property) {
                value = new ArrayList<>(property_array);
            }
        }
        return value;
    }

    @Override
    //TODO : return a copy of the list
    public List<List<Object>> getState() {
        List<List<Object>> stateCopy = new ArrayList<>();
        for (List<Object> property_array : state) {
            List copyProperty = new ArrayList<>(property_array);
            stateCopy.add(copyProperty);
        }
        return stateCopy;
    }

    @Override
    public void setState(String state, Object value) {

    }

    @Override
    public Map<String,Object> getStateAsMap() {
        Map<String,Object> stateMap = new HashMap<String,Object>();
        for (List<Object> property_array : state){
            if (property_array.size() == 2){
                stateMap.put((String)property_array.get(0),property_array.get(1));
            }else {
                List<Object> paramsValue = new ArrayList<>();
                for (Object obj : property_array){
                    if (obj.equals(property_array.get(0))){
                        //do nothing
                    }else {
                        paramsValue.add(obj);
                    }
                }
                stateMap.put((String)property_array.get(0),paramsValue);
            }
        }
        return stateMap;
    }

    @Override
    public List<Object> getStateExtensionValue(String property) {
       return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
      return new HashMap<>();
    }
}
