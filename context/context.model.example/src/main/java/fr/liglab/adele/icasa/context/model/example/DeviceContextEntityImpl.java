package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true)
@Provides
public class DeviceContextEntityImpl implements ContextEntity, DeviceListener{

    private static final Logger LOG = LoggerFactory.getLogger(DeviceContextEntityImpl.class);

    @Requires(specification = GenericDevice.class, id = "context.entity.device", optional = false, filter ="(device.serialNumber=${context.entity.id})")
    GenericDevice device;

    @Requires(specification = Relation.class, id = "context.entity.relation", optional = true,
            filter = "(relation.end.id=${context.entity.id})")
    List<Relation> relations;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @ServiceProperty(name = "context.entity.state", mandatory = true)
    List<List<Object>> state;

    @ServiceProperty(name = "context.entity.state.extension", mandatory = true)
    List<List<Object>> stateExtensions;

    private final Map<Long,Object> m_stateExtension = new HashMap<>();

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
    public void setState(String state, Object value) {
        device.setPropertyValue(state,value);
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
        //      LOG.info("Entity : " + name + " BIND relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue() );
        /*state actualisation*/
        m_stateExtension.put((Long)serviceReference.getProperty(Constants.SERVICE_ID),relation.getExtendedState().getValue());
        addStateExtensionValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue(), relation.getExtendedState().isAggregate());
    }

    @Modified(id = "context.entity.relation")
    public synchronized void modifiedRelations(Relation relation,ServiceReference serviceReference) {
        /**    LOG.info("Entity : " + name + " MODIFIED relation " + relation.getName() + " provides State Extension " + relation.getExtendedState().getName() + " value " + relation.getExtendedState().getValue());
         LOG.info("NEW value " + relation.getExtendedState().getValue() + " OLD value " + m_stateExtension.get(serviceReference));
         if (relation.getExtendedState().getValue().equals(m_stateExtension.get(serviceReference))){
         LOG.error(" Modified is called but last and new extended state are equals");
         }**/
        if(m_stateExtension.get((Long)serviceReference.getProperty(Constants.SERVICE_ID)).equals(relation.getExtendedState().getValue())){
            return;
        }
        replaceStateExtensionValue(relation.getExtendedState().getName(), relation.getExtendedState().getValue(), m_stateExtension.get((Long)serviceReference.getProperty(Constants.SERVICE_ID)));
        m_stateExtension.put((Long) serviceReference.getProperty(Constants.SERVICE_ID), relation.getExtendedState().getValue());

    }

    @Unbind(id = "context.entity.relation")
    public synchronized void unbindRelations(Relation relation,ServiceReference serviceReference) {
        //       LOG.info("Entity : " + name + " UNBIND relation " + relation.getName() + " remove " + m_stateExtension.get(serviceReference));

        removeStateExtensionValue(relation.getExtendedState().getName(), m_stateExtension.get((Long)serviceReference.getProperty(Constants.SERVICE_ID)));
        m_stateExtension.remove((Long)serviceReference.getProperty(Constants.SERVICE_ID));
    }

    //TODO : To verify
    public void addStateValue(String property, Object value, boolean isAggregated) {
        List<List<Object>> state = new ArrayList<>(this.state);

        boolean property_exists = false;
        for (List<Object> property_array : state){
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
            state.add(property_array);
        }

        this.state = new ArrayList<>(state);
    }

    /** public void replaceStateValue(String property, Object newValue, Object oldValue, boolean isAggregated) {
     this.removeStateValue(property, oldValue);
     this.addStateValue(property, newValue, isAggregated);
     }**/

    private synchronized void replaceStateValue(String property,Object newValue,Object oldValue){
        List<List<Object>> stateCopy = new ArrayList<>(this.state);
        List<List<Object>> stateCopyShuffle = new ArrayList<>();

        for (List<Object> property_array : stateCopy){
            //TODO : to verify if there are several parameters in state
            if (property_array.get(0)==property){
                property_array.set(1,newValue);
            }
        }

        //HACK : IF THE LIST IS NOT SHUFFLE EVENT ISN4T PROPAGED TO OSGI REGISTRY
        int size = stateCopy.size();
        for(int i=0;i<size;i++){
            stateCopyShuffle.add(new ArrayList<>());
        }
        for(int i=0;i<size;i++){
            stateCopyShuffle.set(i,stateCopy.get(size-i-1));
        }

        this.state = new ArrayList<>(stateCopyShuffle);
    }


    public void removeStateValue(String property, Object value) {
        List<List<Object>> state = new ArrayList<>(this.state);

        int index = -1;
        for (List<Object> property_array : state){
            if (property_array.get(0)==property){
                index = state.indexOf(property_array);
                if (property_array.contains(value)){

                    property_array.remove(value);
                }
            }
        }

        /*If the property hasn't value any more, it is cleared*/
        if (index>=0){
            if(state.get(index).size()==1){
                state.remove(index);
            }
        }

        this.state = new ArrayList<>(state);
    }

    @Bind(id = "context.entity.device")
    public void bindGenericDevice (GenericDevice device) {
        device.addListener(this);
    }

    @Unbind(id = "context.entity.device")
    public void unbindGenericDevice(GenericDevice device) {
        device.removeListener(this);
    }


    @Override
    public void deviceAdded(GenericDevice device) {
        LOG.info("Device : "+device.getSerialNumber()+ " add listener to context entity : "+  this.getId());
    }

    @Override
    public void deviceRemoved(GenericDevice device) {

    }

    @Override
    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
        LOG.info("Device : " + device.getSerialNumber() + " Property modified : " + propertyName + " old " + oldValue + " new " + newValue);
        replaceStateValue(propertyName, newValue, oldValue);
    }

    @Override
    public void devicePropertyAdded(GenericDevice device, String propertyName) {

    }

    @Override
    public void devicePropertyRemoved(GenericDevice device, String propertyName) {

    }

    @Override
    public void deviceEvent(GenericDevice device, Object data) {

    }
}