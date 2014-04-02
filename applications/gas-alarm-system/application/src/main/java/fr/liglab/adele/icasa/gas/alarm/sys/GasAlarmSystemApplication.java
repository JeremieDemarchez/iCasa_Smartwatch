package fr.liglab.adele.icasa.gas.alarm.sys;


import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.gasSensor.CarbonDioxydeSensor;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.apache.felix.ipojo.annotations.*;

import java.util.Map;


@Component(name = "GasAlarmSystemApplication")
@Instantiate
@Provides(specifications = PeriodicRunnable.class)
public class GasAlarmSystemApplication implements DeviceListener,PeriodicRunnable {


    public static final double SEUIL_CO2_CRITIC = 3.8;

    private boolean state = false;

    private boolean alarmRunning = false;

    private final Object m_lock;



    @RequiresDevice(id="binaryLights", type="field", optional=true)
    private BinaryLight[] binaryLights;

    @RequiresDevice(id="dimmerLights", type="field", optional=true)
    private DimmerLight[] dimmerLights;

    @RequiresDevice(id="carbonDioxydeSensors", type="field", optional=true)
    private CarbonDioxydeSensor[] carbonDioxydeSensors;

    public GasAlarmSystemApplication() {
        this.m_lock = new Object();
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
            if(sensor.getCO2Concentration() > SEUIL_CO2_CRITIC ){
                return true;
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
        synchronized (m_lock){
            if (alarmRunning){
                return 1000*60;
            }else{
                return 1000*60*10  ;
            }
        }
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
            if(alarmRunning = false){

                synchronized(m_lock){
                    alarmRunning =true;
                }
            }
        }
        if(alarmRunning = true){

            synchronized(m_lock){
                alarmRunning =false;
            }
        }

    }

}

/*
@Component(name = "GasAlarm")
@Instantiate
@Provides
public class GasAlarmSystemApplication implements DeviceListener,PeriodicRunnable {



    public static final double SEUIL_CO2_CRITIC = 3.8;

    private boolean state = false;

    private boolean alarmRunning = false;

    private final Object m_lock;



    @RequiresDevice(id="binaryLights", type="field", optional=true)
    private BinaryLight[] binaryLights;

    @RequiresDevice(id="dimmerLights", type="field", optional=true)
    private DimmerLight[] dimmerLights;

    @RequiresDevice(id="carbonDioxydeSensors", type="field", optional=true)
    private CarbonDioxydeSensor[] carbonDioxydeSensors;

    public GasAlarmSystemApplication() {
        this.m_lock = new Object();
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
            if(sensor.getCO2Concentration() > SEUIL_CO2_CRITIC ){
                return true;
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
        synchronized (m_lock){
            if (alarmRunning){
                return 1000*60;
            }else{
                return 1000*60*10  ;
            }
        }
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
            if(alarmRunning = false){

                synchronized(m_lock){
                    alarmRunning =true;
                }
            }
        }
        if(alarmRunning = true){

            synchronized(m_lock){
                alarmRunning =false;
            }
        }

    }
}
*/