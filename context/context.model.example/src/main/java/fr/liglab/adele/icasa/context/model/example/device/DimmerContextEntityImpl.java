package fr.liglab.adele.icasa.context.model.example.device;

import fr.liglab.adele.icasa.context.handler.synchronization.Pull;
import fr.liglab.adele.icasa.context.handler.synchronization.Set;
import fr.liglab.adele.icasa.context.handler.synchronization.State;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component(immediate = true)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State(states = {DimmerLight.DEVICE_SERIAL_NUMBER,DimmerLight.DIMMER_LIGHT_MAX_POWER_LEVEL,DimmerLight.DIMMER_LIGHT_POWER_LEVEL})
public class DimmerContextEntityImpl implements ContextEntity, DeviceListener{

    private static final Logger LOG = LoggerFactory.getLogger(DimmerContextEntityImpl.class);

    @Requires(specification = DimmerLight.class, id = "context.entity.device", optional = false, filter ="(device.serialNumber=${context.entity.id})")
    DimmerLight device;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @Pull(state = DimmerLight.DEVICE_SERIAL_NUMBER)
    private final Function getSerialNumber = (Object obj)->{
        return device.getSerialNumber();
    };

    @Pull(state = DimmerLight.DIMMER_LIGHT_POWER_LEVEL)
    private final Function getLightPowerStatus= (Object obj)->{
        return device.getPowerLevel();
    };

    @Pull(state = DimmerLight.DIMMER_LIGHT_MAX_POWER_LEVEL)
    private final Function getLightMaxPowerLevel = (Object obj)->{
        return device.getMaxPowerLevel();
    };

    @Set(state = DimmerLight.DIMMER_LIGHT_POWER_LEVEL)
    private final Function setLightPowerStatus= (Object obj)->{
        return device.setPowerLevel((Double)obj);
    };

    @Bind(id = "context.entity.device")
    public void bindGenericDevice (DimmerLight device) {
        device.addListener(this);
    }

    @Unbind(id = "context.entity.device")
    public void unbindGenericDevice(DimmerLight device) {
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



    private final Map<String,Object> injectedState = new HashMap<>();

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