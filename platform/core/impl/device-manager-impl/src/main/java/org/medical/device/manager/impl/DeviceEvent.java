/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
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
package org.medical.device.manager.impl;

import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.Device;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.ProvidedDevice;

/**
 * Internal events representing an addition, removal or update of a device.
 * 
 * @author Thomas Leveque
 *
 */
public class DeviceEvent {
	
	private final EventType _type;
	
	private final Device _device;
	
	private final Object _proxy;
	
	public DeviceEvent(EventType type, Device device) {
		_device = device;
		_type = type;
		_proxy = null;
	}
	
	public DeviceEvent(EventType type, Device device, Object proxy) {
		_device = device;
		_type = type;
		_proxy = proxy;
	}
	
	public Object getProxy() {
		return _proxy;
	}
	
	public Device getDevice() {
		return _device;
	}
	
	public EventType getType() {
		return _type;
	}
	
	public boolean isAdd() {
		return EventType.ADD.equals(_type);
	}
	
	public boolean isRemove() {
		return EventType.REMOVE.equals(_type);
	}
	
	public boolean isStateUpdate() {
		return EventType.STATE_UPDATE.equals(_type);
	}
	
	public boolean isStructureUpdate() {
		return EventType.STRUCTURE_UPDATE.equals(_type);
	}
	
	public boolean isKnownDevice() {
		return _device instanceof KnownDevice;
	}
	
	public boolean isApplicationDevice() {
		return !isDeviceProxy() && (_device instanceof ApplicationDevice);
	}
	
	public boolean isProvidedDevice() {
		return _device instanceof ProvidedDevice;
	}
	
	public boolean isDeviceProxy() {
		return _proxy != null;
	}
	
	public boolean isAvailableDevice() {
		return _device instanceof AvailableDevice;
	}
	
	public boolean isAvailabityChange() {
		return false; //TODO 
	}

	public boolean isUpdate() {
		return isStateUpdate() || isStructureUpdate();
	}
}
