package fr.liglab.adele.icasa.smart.plug.monitoring;



import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.power.PowerSwitch;
import fr.liglab.adele.icasa.mode.ModeListener;
import fr.liglab.adele.icasa.mode.ModeService;
import fr.liglab.adele.icasa.mode.ModeUtils;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


@Component
@Instantiate
@Provides(specifications = ModeListener.class)
public class SmartPlugMonitoring  implements ModeListener{

    @Requires
    ModeService modeService;

    private  final Logger m_logger = LoggerFactory.getLogger(SmartPlugMonitoring.class);

    @RequiresDevice(id="powerSwitch", type="field", optional=true)
    PowerSwitch[] powerSwitches;

    private final Object m_lock = new Object();

    @RequiresDevice(id="carbonDioxydeSensors", type="bind")
    public void bindCarbonDioxydeSensor(PowerSwitch powerSwitch, Map properties) {
        synchronized (m_lock){
            if (modeService.getCurrentMode().equals(ModeUtils.AWAY)){
                powerSwitch.switchOff();
            }else if (modeService.getCurrentMode().equals(ModeUtils.NIGHT)){
                powerSwitch.switchOn();
            }else if (modeService.getCurrentMode().equals(ModeUtils.HOME)){
                powerSwitch.switchOn();
            }else if (modeService.getCurrentMode().equals(ModeUtils.HOLIDAYS)){
                powerSwitch.switchOff();
            }
        }
    }

    @RequiresDevice(id="carbonDioxydeSensors", type="unbind")
    public void unbindCarbonDioxydeSensor(PowerSwitch powerSwitch, Map properties) {
        synchronized (m_lock){

        }
    }

    @Override
    public void modeChange(String newMode, String oldMode) {
        if (modeService.getCurrentMode().equals(ModeUtils.AWAY)){
            synchronized (m_lock){
                for(PowerSwitch powerSwitch : powerSwitches){
                    powerSwitch.switchOff();
                }
            }
        }else if (modeService.getCurrentMode().equals(ModeUtils.NIGHT)){
            synchronized (m_lock){
                for(PowerSwitch powerSwitch : powerSwitches){
                    powerSwitch.switchOff();
                }
            }
        }else if (modeService.getCurrentMode().equals(ModeUtils.HOME)){
            synchronized (m_lock){
                for(PowerSwitch powerSwitch : powerSwitches){
                    powerSwitch.switchOff();
                }
            }
        }else if (modeService.getCurrentMode().equals(ModeUtils.HOLIDAYS)){
            synchronized (m_lock){
                for(PowerSwitch powerSwitch : powerSwitches){
                    powerSwitch.switchOff();
                }
            }
        }
    }
}
