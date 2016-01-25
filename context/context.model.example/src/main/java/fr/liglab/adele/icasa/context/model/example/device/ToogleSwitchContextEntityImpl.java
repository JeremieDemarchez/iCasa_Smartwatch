package fr.liglab.adele.icasa.context.model.example.device;

import fr.liglab.adele.icasa.context.annotation.Pull;
import fr.liglab.adele.icasa.context.annotation.Set;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/**
@Component(immediate = true)
@Provides
@fr.liglab.adele.icasa.context.handler.relation.ContextEntity
@State(states = {PowerSwitch.DEVICE_SERIAL_NUMBER, PowerSwitch.POWER_SWITCH_CURRENT_STATUS})**/
public class ToogleSwitchContextEntityImpl implements  DeviceListener{

    public static final String DEVICE_TYPE = "device.type";

    private static final Logger LOG = LoggerFactory.getLogger(ToogleSwitchContextEntityImpl.class);

    @Requires(specification = PowerSwitch.class, id = "context.entity.device", optional = false, filter ="(device.serialNumber=${context.entity.id})")
    PowerSwitch device;

    @ServiceProperty(name = "context.entity.id",mandatory = true)
    String name;

   // @Pull(state = BinaryLight.DEVICE_SERIAL_NUMBER)
    private final Function getSerialNumber = (Object obj)->{
        return device.getSerialNumber();
    };

  //  @Pull(state = ToogleSwitchContextEntityImpl.DEVICE_TYPE)
    private final Function getDeviceType = (Object obj)->{
        return "ToogleSwitch";
    };

  //  @Pull(state = BinaryLight.BINARY_LIGHT_MAX_POWER_LEVEL)
    private final Function getLightMaxPowerLevel = (Object obj)->{
        return device.getStatus();
    };

 //   @Set(state = PowerSwitch.POWER_SWITCH_CURRENT_STATUS)
    private final Function setLightPowerStatus= (Object obj)->{
        if ((boolean)obj == true){
            device.switchOn();
        }else{
            device.switchOff();
        }
        return true;
    };

    @Bind(id = "context.entity.device")
    public void bindGenericDevice (PowerSwitch device) {
        device.addListener(this);
    }

    @Unbind(id = "context.entity.device")
    public void unbindGenericDevice(PowerSwitch device) {
        device.removeListener(this);
    }


    @Override
    public void deviceAdded(GenericDevice device) {
        LOG.info("Device : "+device.getSerialNumber()+ " add listener to context entity : "/**+  this.getId()**/);
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