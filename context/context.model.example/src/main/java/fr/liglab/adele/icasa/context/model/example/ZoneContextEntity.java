package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true)
@Provides
public class ZoneContextEntity implements ContextEntity{

    private static final Logger LOG = LoggerFactory.getLogger(ZoneContextEntity.class);


    @Requires(specification = Relation.class, id = "context.entity.relation", optional = true,
            filter = "(relation.end.id=${context.entity.id})")
    List<Relation> relations;

    @Property(name = "context.entity.id",mandatory = true)
    String name;


    @ServiceProperty(name = "context.entity.state", mandatory = true)
    List<List<Object>> state;

    @ServiceProperty(name = "context.entity.state.extension")
    List<List<Object>> stateExtensions;

    private final Map<ServiceReference,Object> m_stateExtension = new HashMap<>();

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    @Override
    public String getId() {
        return name;
    }

    protected synchronized void addStateExtensionValue(String property, Object value, boolean isAggregated) {
        /**     List<List<Object>> state = new ArrayList<>();
         state.addAll(this.stateExtensions);**/

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

//        this.stateExtensions = state;
    }

    protected synchronized void removeStateExtensionValue(String property, Object value) {
        /**   List<List<Object>> state = new ArrayList<>();
         state.addAll(this.stateExtensions);
         **/
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

        //    this.stateExtensions = state;
    }


    public List<Object> getStateValue(String property) {
        List<Object> value = new ArrayList<>();

        for (List<Object> property_array : state) {
            if (property_array.get(0) == property) {
                Collections.copy(value, property_array);
            }
        }
        return value;
    }

    @Override
    //TODO : return a copy of the list
    public List<List<Object>> getState() {
        return state;
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
                Collections.copy(value,property_array);
            }
        }
        return value;
    }

    @Override
    //TODO : Return a copy of the stateextension list
    public List<List<Object>> getStateExtension() {
        return stateExtensions;
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
        LOG.info("Entity : " + name + " BIND relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );
        /*state actualisation*/
        m_stateExtension.put(serviceReference,relation.getExtendedState().getValue());
        addStateExtensionValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue(), relation.getExtendedState().isAggregate());

    }

    @Modified(id = "context.entity.relation")
    public synchronized void modifiedRelations(Relation relation,ServiceReference serviceReference) {
        LOG.info("Modified !!");
        LOG.info("Entity : " + name + " modified relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );
        m_stateExtension.put(serviceReference,relation.getExtendedState().getValue());
        addStateExtensionValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue(), relation.getExtendedState().isAggregate());
    }

    @Unbind(id = "context.entity.relation")
    public synchronized void unbindRelations(Relation relation,ServiceReference serviceReference) {
        LOG.info("Entity : " + name + " UNBIND relation " + relation.getName() + " remove " + m_stateExtension.get(relation.getId()) );

        removeStateExtensionValue(relation.getExtendedState().getName(), m_stateExtension.get(serviceReference));
        m_stateExtension.remove(serviceReference);
    }
}