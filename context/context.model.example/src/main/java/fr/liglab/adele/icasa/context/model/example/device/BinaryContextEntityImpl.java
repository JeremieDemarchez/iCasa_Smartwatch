package fr.liglab.adele.icasa.context.model.example.device;


import fr.liglab.adele.icasa.context.annotation.Entity;
import fr.liglab.adele.icasa.context.annotation.Pull;
import fr.liglab.adele.icasa.context.annotation.Set;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
//@Entity(spec = ContextEntity.class )
@Component(immediate = true)
@Provides
public class BinaryContextEntityImpl implements ContextEntity, DeviceListener{

    public static final String DEVICE_TYPE = "device.type";

    private static final Logger LOG = LoggerFactory.getLogger(BinaryContextEntityImpl.class);

    @Requires(specification = BinaryLight.class, id = "context.entity.device", optional = false, filter ="(device.serialNumber=${context.entity.id})")
    BinaryLight device;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @Pull(state = BinaryLight.DEVICE_SERIAL_NUMBER)
    private final Function getSerialNumber = (Object obj)->{
        return device.getSerialNumber();
    };

    @Pull(state = BinaryContextEntityImpl.DEVICE_TYPE)
    private final Function getDeviceType = (Object obj)->{
        return "BinaryLight";
    };

    @Pull(state = BinaryLight.BINARY_LIGHT_POWER_STATUS)
    private final Function getLightPowerStatus= (Object obj)->{
        return device.getPowerStatus();
    };

    @Pull(state = BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL)
    private final Function getLightMaxPowerLevel = (Object obj)->{
        return device.getMaxPowerLevel();
    };

    @Set(state = BinaryLight.BINARY_LIGHT_POWER_STATUS)
    private final Function setLightPowerStatus= (Object obj)->{
        if ((boolean)obj == true){
            device.turnOn();
        } else {
            device.turnOff();
        }
        return true;
    };

    @Bind(id = "context.entity.device")
    public void bindGenericDevice (BinaryLight device) {
        device.addListener(this);
    }

    @Unbind(id = "context.entity.device")
    public void unbindGenericDevice(BinaryLight device) {
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
        return injectedExtensionState;
    }

    @Override
    public void pushState(String state, Object value) {

    }
}