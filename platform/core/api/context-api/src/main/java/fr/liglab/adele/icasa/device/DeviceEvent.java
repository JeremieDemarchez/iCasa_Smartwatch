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
package fr.liglab.adele.icasa.device;

/**
 * Represents an event related to a device.
 * 
 * @author Thomas Leveque
 */
public class DeviceEvent {

	private final DeviceEventType _type;
	private final GenericDevice _device;


	/**
	 * Creates a DeviceEvent.
	 * 
	 * @param device Device associated to the event
	 * @param type Type of the event
	 */
	public DeviceEvent(GenericDevice device, DeviceEventType type) {
		_device = device;
		_type = type;
	}


	/**
	 * Gets the event type
	 * 
	 * @return The event type
	 */
	public DeviceEventType getType() {
		return _type;
	}

	/**
	 * Gets the event associated device
	 * 
	 * @return The event associated device
	 */
	public GenericDevice getDevice() {
		return _device;
	}
}
