package fr.liglab.adele.icasa.context.model.example.device;

import fr.liglab.adele.icasa.context.annotation.Pull;
import fr.liglab.adele.icasa.context.annotation.Set;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/**
@Component(immediate = true)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State(states = {DimmerContextEntityImpl.DEVICE_TYPE,DimmerLight.DEVICE_SERIAL_NUMBER,DimmerLight.DIMMER_LIGHT_MAX_POWER_LEVEL,DimmerLight.DIMMER_LIGHT_POWER_LEVEL})**/
public class DimmerContextEntityImpl implements  DeviceListener{

    public static final String DEVICE_TYPE = "device.type";

    private static final Logger LOG = LoggerFactory.getLogger(DimmerContextEntityImpl.class);

    @Requires(specification = DimmerLight.class, id = "context.entity.device", optional = false, filter ="(device.serialNumber=${context.entity.id})")
    DimmerLight device;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

    @Pull(state = DimmerContextEntityImpl.DEVICE_TYPE)
    private final Function getDeviceType = (Object obj)->{
        return "DimmerLight";
    };

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
        LOG.info("Device : "+device.getSerialNumber()+ " add listener to context entity : "/*t+  his.getId()*/);
    }

    @Override
    public void deviceRemoved(GenericDevice device) {

    }

    @Override
    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
        LOG.info("Device : " + device.getSerialNumber() + " Property modified : " + propertyName + " old " + oldValue + " new " + newValue);
   //     pushState(propertyName, newValue);
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

}