package fr.liglab.adele.icasa.gas.alarm.sys;


import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.gasSensor.CarbonDioxydeSensor;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.mail.service.MailSender;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.apache.felix.ipojo.annotations.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Component(name = "GasAlarmSystemApplication")
@Instantiate(name = "GasAlarmSystemApplicationImpl-0")
@Provides(specifications = {PeriodicRunnable.class,GasAlarmService.class})
@CommandProvider(namespace = "GasAlarm")
public class GasAlarmSystemApplication implements DeviceListener,PeriodicRunnable,GasAlarmService {


    private double _CO2Threshold = 3.8;

    private boolean state = false;

    private final Object m_lock;

    private boolean alarmRunning = false;

    private Set<GasAlarmListener> setOfListener = new HashSet<GasAlarmListener>();



    @RequiresDevice(id="binaryLights", type="field", optional=true)
    private BinaryLight[] binaryLights;

    @RequiresDevice(id="dimmerLights", type="field", optional=true)
    private DimmerLight[] dimmerLights;

    @RequiresDevice(id="carbonDioxydeSensors", type="field", optional=true)
    private CarbonDioxydeSensor[] carbonDioxydeSensors;

    public GasAlarmSystemApplication() {
        m_lock = new Object();
    }


    @Invalidate
    public void stop() {
        System.out.println(" Gas alarm component stop ... ");
        for (CarbonDioxydeSensor sensor : carbonDioxydeSensors) {
            sensor.removeListener(this);
        }
    }


    @Validate
    public void start() {
        System.out.println(" Gas alarm component start ... ");
    }

    @RequiresDevice(id="carbonDioxydeSensors", type="bind")
    public void bindCarbonDioxydeSensor(CarbonDioxydeSensor carbonDioxydeSensor, Map properties) {
        carbonDioxydeSensor.addListener(this);
    }

    @RequiresDevice(id="carbonDioxydeSensors", type="unbind")
    public void unbindCarbonDioxydeSensor(CarbonDioxydeSensor carbonDioxydeSensor, Map properties) {
        carbonDioxydeSensor.removeListener(this);
    }

    @Override
    public void deviceAdded(GenericDevice arg0) {
        // do nothing
    }

    @Override
    public void devicePropertyAdded(GenericDevice arg0, String arg1) {
        // do nothing
    }

    @Override
    public void devicePropertyModified(GenericDevice device,String propertyName, Object oldValue, Object newValue) {

    }

    public boolean checkCo2() {
        for(CarbonDioxydeSensor sensor : carbonDioxydeSensors){
            synchronized (m_lock){
                if(sensor.getCO2Concentration() > _CO2Threshold ){
                    return true;
                }
            }
        }
        return false;
    }



    @Override
    public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
        // do nothing
    }

    @Override
    public void deviceEvent(GenericDevice device, Object data) {

    }

    @Override
    public void deviceRemoved(GenericDevice arg0) {
        // do nothing
    }


    @Override
    public long getPeriod() {
        return 1000*30;

    }

    @Override
    public String getGroup() {
        return "Gas-Alarm-Thread-0";
    }

    @Override
    public void run() {
        if (checkCo2()){
            if (!state){
                for(BinaryLight light : binaryLights){
                    light.turnOn();
                }
                for(DimmerLight light : dimmerLights){
                    light.setPowerLevel(1);
                }
                state = true;
            }else{
                for(BinaryLight light : binaryLights){
                    light.turnOff();
                }
                for(DimmerLight light : dimmerLights){
                    light.setPowerLevel(0);
                }
                state = false;
            }
            if(alarmRunning == false){
                for(GasAlarmListener listener:setOfListener){
                    listener.thresholdCrossUp();
                }
                alarmRunning = true;
            }
        }else{
            if(alarmRunning == true){

                for(GasAlarmListener listener:setOfListener){
                    listener.thresholdCrossUp();
                }
                ;
                alarmRunning = false;
            }
        }
    }


    @Command
    public  void setGasLimit(double value) {
        synchronized (m_lock){
            _CO2Threshold = value;
        }
        System.out.println(" New Threshold gas value " + _CO2Threshold);
    }

    @Override
    public synchronized void addListener(GasAlarmListener listener) {
        setOfListener.add(listener);
    }

    @Override
    public synchronized void removeListener(GasAlarmListener listener) {
        setOfListener.remove(listener);
    }
}
