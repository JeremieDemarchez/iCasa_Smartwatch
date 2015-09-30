package fr.liglab.adele.icasa.context.model.example.transformation;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import fr.liglab.adele.icasa.context.model.RelationFactory;
import fr.liglab.adele.icasa.context.transformation.Aggregation;
import fr.liglab.adele.icasa.context.transformation.AggregationFunction;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true,propagation = true)
@Provides
public class PhysicalParameterImpl implements Aggregation {

    /** REQUIRES **/
    @Requires(specification = Relation.class, id = "context.entity.relation", optional = true,
            filter = "(relation.end.id=${context.entity.id})",proxy = false)
    List<Relation> relations;

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

    @ServiceProperty(name = "context.entity.state.extension", mandatory = true)
    List<List<Object>> stateExtensions;


    /** ATTRIBUTES **/
    private final AggregationFunction m_aggregationFunction;

    private static final Logger LOG = LoggerFactory.getLogger(PhysicalParameterImpl.class);

    private final Map<ServiceReference,Object> m_stateExtension = new HashMap<>();


    public PhysicalParameterImpl(@Property(name = "aggregation.function", mandatory = true, immutable = true) AggregationFunction aggregationFunction) {
        m_aggregationFunction = aggregationFunction;
    }

    @Validate
    public void start(){
        relationFactory.createRelation("isPhysicalParameterOf", this.getId(), zoneId,physicalParameterName, false, m_state -> {
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

    protected synchronized void addStateExtensionValue(String property, Object value, boolean isAggregated) {
        List<List<Object>> stateExtensions = new ArrayList<>(this.stateExtensions);

        boolean property_exists = false;

        for (List<Object> property_array : stateExtensions){
            if (property_array.get(0)==property){
                property_exists = true;
                if (!isAggregated){
                    property_array.clear();
                    property_array.add(property);
                    property_array.add(value);
                } else {
                    if (!property_array.contains(value)){
                        property_array.add(value);
                    }
                }
            }
        }
        if (!property_exists){
            List<Object> property_array = new ArrayList<>();
            property_array.add(property);
            property_array.add(value);
            stateExtensions.add(property_array);
        }

        this.stateExtensions = new ArrayList<>(stateExtensions);
    }

    protected synchronized void removeStateExtensionValue(String property, Object value) {
        List<List<Object>> stateExtensions = new ArrayList<>(this.stateExtensions);

        int index = -1;
        for (List<Object> property_array : stateExtensions){
            if (property_array.get(0)==property){
                index = stateExtensions.indexOf(property_array);
                if (property_array.contains(value)){

                    property_array.remove(value);
                }
            }
        }

        /*If the property hasn't value any more, it is cleared*/
        if (index>=0){
            if(stateExtensions.get(index).size()==1){
                stateExtensions.remove(index);
            }
        }

        this.stateExtensions = new ArrayList<>(stateExtensions);
    }

    private synchronized void replaceStateExtensionValue(String property,Object newValue,Object oldValue){
        List<List<Object>> stateExtensions = new ArrayList<>(this.stateExtensions);

        for (List<Object> property_array : stateExtensions){
            if (property_array.get(0)==property){
                if (property_array.contains(oldValue)){
                    int index = property_array.indexOf(oldValue);
                    property_array.set(index,newValue);
                }
            }
        }

        this.stateExtensions = new ArrayList<>(stateExtensions);
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
        List<Object> value = new ArrayList<>();

        for (List<Object> property_array : stateExtensions) {
            if (property_array.get(0) == property) {
                value = new ArrayList<>(property_array);
            }
        }
        return value;
    }

    @Override
    //TODO : Return a copy of the stateextension list
    public List<List<Object>> getStateExtension() {
        List<List<Object>> stateCopy = new ArrayList<>();
        for (List<Object> property_array : stateExtensions) {
            List copyProperty = new ArrayList<>(property_array);
            stateCopy.add(copyProperty);
        }
        return stateCopy;
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
        Map<String,Object> stateMap = new HashMap<String,Object>();
        for (List<Object> property_array : stateExtensions){
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


    @Bind(id = "context.entity.relation")
    public synchronized void bindRelations (Relation relation,ServiceReference serviceReference) {
 //       LOG.info("Entity : " + name + " BIND relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );
        /*state actualisation*/
        m_stateExtension.put(serviceReference,relation.getExtendedState().getValue());
        addStateExtensionValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue(), relation.getExtendedState().isAggregate());
    }

    @Modified(id = "context.entity.relation")
    public synchronized void modifiedRelations(Relation relation,ServiceReference serviceReference) {
   /**     LOG.info("Entity : " + name + " MODIFIED relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue());
        LOG.info("NEW value " + relation.getExtendedState().getValue() + " OLD value " + m_stateExtension.get(serviceReference));
        if (relation.getExtendedState().getValue().equals(m_stateExtension.get(serviceReference))){
            LOG.error(" Modified is called but last and new extended state are equals");
        }**/
        if(m_stateExtension.get(serviceReference).equals(relation.getExtendedState().getValue())){
            return;
        }
        replaceStateExtensionValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue(), m_stateExtension.get(serviceReference));
        m_stateExtension.put(serviceReference, relation.getExtendedState().getValue());

    }

    @Unbind(id = "context.entity.relation")
    public synchronized void unbindRelations(Relation relation,ServiceReference serviceReference) {
   //     LOG.info("Entity : " + name + " UNBIND relation " + relation.getName() + " remove " + m_stateExtension.get(serviceReference));

        removeStateExtensionValue(relation.getExtendedState().getName(), m_stateExtension.get(serviceReference));
        m_stateExtension.remove(serviceReference);
    }
}
