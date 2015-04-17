package fr.liglab.adele.icasa.device.doorWindow;

import fr.liglab.adele.icasa.device.GenericDevice;

public interface DoorWindowSensor extends GenericDevice {

	String DOOR_WINDOW_SENSOR_OPENING_DETECTCION = "doorWindowSensor.opneningDetection";

	boolean isOpened ();

}
