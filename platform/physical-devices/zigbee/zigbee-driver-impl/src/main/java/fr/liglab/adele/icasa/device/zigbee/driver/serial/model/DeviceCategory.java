/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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
