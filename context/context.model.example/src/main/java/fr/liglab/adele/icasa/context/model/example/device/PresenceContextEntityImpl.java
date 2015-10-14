package fr.liglab.adele.icasa.context.model.example.device;

import fr.liglab.adele.icasa.context.handler.synchronization.Pull;
import fr.liglab.adele.icasa.context.handler.synchronization.Set;
import fr.liglab.adele.icasa.context.handler.synchronization.State;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

@Component(immediate = true)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State(states = {PresenceContextEntityImpl.DEVICE_TYPE,PresenceSensor.DEVICE_SERIAL_NUMBER, PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE})
public class PresenceContextEntityImpl implements ContextEntity, DeviceListener{

    public static final String DEVICE_TYPE = "device.type";

    private static final Logger LOG = LoggerFactory.getLogger(PresenceContextEntityImpl.class);

    @Requires(specification = PresenceSensor.class, id = "context.entity.device", optional = false, filter ="(device.serialNumber=${context.entity.id})")
    PresenceSensor device;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @Pull(state = PresenceContextEntityImpl.DEVICE_TYPE)
    private final Function getDeviceType = (Object obj)->{
        return "PresenceSensor";
    };

    @Pull(state = PresenceSensor.DEVICE_SERIAL_NUMBER)
    private final Function getSerialNumber = (Object obj)->{
        return device.getSerialNumber();
    };

    @Pull(state = PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)
    private final Function getSensedPresence= (Object obj)->{
        return device.getSensedPresence();
    };

    @Bind(id = "context.entity.device")
    public void bindGenericDevice (PresenceSensor device) {
        device.addListener(this);
    }

    @Unbind(id = "context.entity.device")
    public void unbindGenericDevice(PresenceSensor device) {
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
        pushState(propertyName, newValue);
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

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    private final Map<String,Object> injectedState = new HashMap<>();

    private final Map<String,Object> injectedExtensionState =new HashMap<>();

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
        return Collections.unmodifiableMap(injectedState);
    }

    @Override
    public Object getStateExtensionValue(String property) {
        return injectedExtensionState.get(property);
    }

    @Override
    public Map<String, Object> getStateExtensionAsMap() {
        return Collections.unmodifiableMap(injectedExtensionState);
    }

    @Override
    public void pushState(String state, Object value) {

    }
}