package fr.liglab.adele.icasa.intrusion.alarm;

import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.alarm.AlarmService;
import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.doorWindow.DoorWindowSensor;
import fr.liglab.adele.icasa.device.motion.MotionSensor;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.notification.NotificationService;


@Component(name = "IntrusionAlarmApplication")
@Instantiate(name = "IntrusionAlarmApplicationImpl-0")
public class IntrusionAlarmApplication implements DeviceListener {

	private  final Logger m_logger = LoggerFactory.getLogger(IntrusionAlarmApplication.class);

	@Requires
	NotificationService notificationService;

	@Requires
	AlarmService alarmService;

	@RequiresDevice(id="MotionSensors", type="field", optional=true)
	private MotionSensor[] motionSensors;

	@RequiresDevice(id="PresenceSensors", type="field", optional=true)
	private PresenceSensor[] presenceSensors;

	@RequiresDevice(id="DoorWindowSensors", type="field", optional=true)
	private DoorWindowSensor[] doorWindowSensors;

	@Validate
	public void start() {
		m_logger.info(" Intrusion Alarm Component  start ... ");
	}

	@Invalidate
	public void stop() {
		m_logger.info(" Intrusion Alarm Component component stop ... ");
		for (MotionSensor sensor : motionSensors) {
			sensor.removeListener(this);
		}
		for (PresenceSensor sensor :presenceSensors) {
			sensor.removeListener(this);
		}
		for (DoorWindowSensor sensor :doorWindowSensors) {
			sensor.removeListener(this);
		}
	}

	@RequiresDevice(id="MotionSensors", type="bind")
	public void bindMotionSensor(MotionSensor motionSensor, Map properties) {
		motionSensor.addListener(this);
	}

	@RequiresDevice(id="MotionSensors", type="unbind")
	public void unbindMotionSensor(MotionSensor motionSensor, Map properties) {
		motionSensor.removeListener(this);
	}

	@RequiresDevice(id="PresenceSensors", type="bind")
	public void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		presenceSensor.addListener(this);
		if (presenceSensor.getSensedPresence()){
			alarmService.fireAlarm();
			notificationService.sendNotification("[iCasa] Intrusion Alarm ", " Intrusion is detected ");
		}
	}

	@RequiresDevice(id="PresenceSensors", type="unbind" )
	public void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		presenceSensor.removeListener(this);

	}

	@RequiresDevice(id="DoorWindowSensors", type="bind")
	public void bindDoorWindowSensor(DoorWindowSensor doorWindowSensor, Map properties) {
		doorWindowSensor.addListener(this);
		if (doorWindowSensor.isOpened()){
			alarmService.fireAlarm();
			notificationService.sendNotification("[iCasa] Intrusion Alarm ", " Intrusion is detected ");
		}

	}

	@RequiresDevice(id="DoorWindowSensors", type="unbind")
	public void unbindDoorWindowSensor(DoorWindowSensor doorWindowSensor, Map properties) {
		doorWindowSensor.removeListener(this);
		
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
		if(device instanceof PresenceSensor) {
			PresenceSensor activSensor = (PresenceSensor) device;;
			if(activSensor != null && propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
				if (activSensor.getSensedPresence()){
					alarmService.fireAlarm();
					notificationService.sendNotification("[iCasa] Intrusion Alarm ", " Intrusion is detected ");

				}
			}
		}
		else if (device instanceof DoorWindowSensor) {
			DoorWindowSensor sensor = (DoorWindowSensor) device;;
			if(sensor != null && propertyName.equals(DoorWindowSensor.DOOR_WINDOW_SENSOR_OPENING_DETECTCION)) {
				if (sensor.isOpened()){
					alarmService.fireAlarm();
					notificationService.sendNotification("[iCasa] Intrusion Alarm ", " Intrusion is detected ");

				}
			}
		}

	}
	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// do nothing
	}

	@Override
	public void deviceEvent(GenericDevice device, Object data) {
		if (device instanceof MotionSensor ){
			if ((data != null) && (data instanceof Boolean)) {
				boolean movementDetected = (Boolean) data;
				if (movementDetected){
					alarmService.fireAlarm();
					notificationService.sendNotification("[iCasa] Intrusion Alarm ", " Intrusion is detected ");
				}
			}
		}
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// do nothing
	}

}

