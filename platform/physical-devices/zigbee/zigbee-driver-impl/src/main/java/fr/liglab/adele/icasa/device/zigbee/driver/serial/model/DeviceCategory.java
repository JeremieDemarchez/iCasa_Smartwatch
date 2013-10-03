/**
 * 
 */
package fr.liglab.adele.icasa.device.zigbee.driver.serial.model;

/**
 * Enum class for zigbee devices categories.
 * @author medical
 * 
 */
public enum DeviceCategory {

	ACTUATOR('A'), SENSOR('C');

	private char value;

	private DeviceCategory(char value) {
		this.value = value;
	}

	@Override
	public String toString() {
		switch (this) {
		case ACTUATOR:
			System.out.println("Actuator : " + value);
			break;
		case SENSOR:
			System.out.println("Sensor : " + value);
		}
		return super.toString();
	}

	public char getValue() {
		return this.value;
	}
}
