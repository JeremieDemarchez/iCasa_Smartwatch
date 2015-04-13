package fr.liglab.adele.icasa.gas.alarm.sys;


import fr.liglab.adele.icasa.alarm.AlarmService;
import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.gasSensor.CarbonDioxydeSensor;
import fr.liglab.adele.icasa.notification.NotificationService;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


@Component(name = "GasAlarmSystemApplication")
@Instantiate(name = "GasAlarmSystemApplicationImpl-0")
@Provides(specifications = {PeriodicRunnable.class})
@CommandProvider(namespace = "GasAlarm")
public class GasAlarmSystemApplication implements DeviceListener,PeriodicRunnable {

    private  final Logger m_logger = LoggerFactory
            .getLogger(GasAlarmSystemApplication.class);

    private double _CO2Threshold = 3.8;

    private final Object m_lock;

    @Requires
    NotificationService notificationService;

    @Requires
    AlarmService alarmService;

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
        if (checkCo2()) {
            m_logger.info("CO2 is too hight !  ");
            notificationService.sendNotification("[ICASA] CO2 Alert", " CO2 is too hight in the house.");
            alarmService.fireAlarm();
        }
    }


    @Command
    public  void setGasLimit(double value) {
        synchronized (m_lock){
            _CO2Threshold = value;
            m_logger.info(" New Threshold gas value " + _CO2Threshold);
        }

    }

}
