package fr.liglab.adele.icasa.flood.alarm;


import fr.liglab.adele.icasa.alarm.AlarmService;
import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.security.FloodSensor;
import fr.liglab.adele.icasa.notification.NotificationService;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


@Component
@Instantiate
@Provides
public class FloodAlarm implements DeviceListener{

    private  final Logger m_logger = LoggerFactory.getLogger(FloodAlarm.class);

    protected static final String APPLICATION_ID = "flood.alarm";

    @Requires
    AlarmService alarmService;

    @Requires
    NotificationService notificationService;

    @RequiresDevice(id="floodSensors", type="field", optional=true)
    FloodSensor[] floodSensors;

    @RequiresDevice(id="floodSensors", type="bind")
    public void bindBinaryLight(FloodSensor floodSensor, Map<Object, Object> properties) {
        floodSensor.addListener(this);
    }

    @RequiresDevice(id="floodSensors", type="unbind")
    public void unbindBinaryLight(FloodSensor floodSensor, Map<Object, Object> properties) {
        floodSensor.removeListener(this);
    }

    @Invalidate
    public void stop() {
        m_logger.info(" Flood Alarm Component component stop ... ");
        for (FloodSensor sensor : floodSensors) {
            sensor.removeListener(this);
        }
    }


    @Validate
    public void start() {
        m_logger.info(" Flood Alarm Component  start ... ");
    }
    private final Object m_lock = new Object();

    @Override
    public void deviceAdded(GenericDevice device) {

    }

    @Override
    public void deviceRemoved(GenericDevice device) {

    }

    @Override
    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
        if(device instanceof FloodSensor){
            FloodSensor sensor = (FloodSensor) device;
            if (propertyName.equals(FloodSensor.FLOOD_SENSOR_ALARM)){
                if (sensor.getAlarmStatus()){
                    alarmService.fireAlarm();
                    notificationService.sendNotification("[iCasa] Flood Alarm ", " Flood Detected in " + sensor.getPropertyValue(FloodSensor.LOCATION_PROPERTY_NAME));
                }
            }
        }
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
