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
package org.medical.application.device.web.common.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.medical.application.device.web.common.impl.component.DeviceEntry;
import org.medical.application.device.web.common.impl.component.DevicePane;

import fr.liglab.adele.icasa.environment.SimulationManager.Position;

public abstract class DeviceController {

	private DevicePane m_DevicePane;
	
	protected final Map<String, DeviceEntry> m_devices = new HashMap<String, DeviceEntry>();
	
	private static boolean[] BORDER_POSITIONS = new boolean[20];
	
	public void addListener(DevicePane devicePane) {
		m_DevicePane = devicePane;
	}
	
	public void removeListener(DevicePane devicePane){
		m_DevicePane = null;
	}
	
	protected void addDevice(DeviceEntry entry) {
		if (m_DevicePane!=null) {
			if (m_devices.containsKey(entry.serialNumber)) {
				m_DevicePane.showErrorWindow("The device \"" + entry.serialNumber + "\"already exists.");
				return;
			}
			m_devices.put(entry.serialNumber, entry);
			m_DevicePane.addDevice(entry);
		}
	} 
	
	protected void removeDevice(String serialNumber) {
		if (m_DevicePane!=null) {
			if (!m_devices.containsKey(serialNumber))
				return;
			DeviceEntry entry = m_devices.remove(serialNumber);
			m_DevicePane.removeDevice(entry);
		}
		
	}
	
	public void moveDevice(String deviceSerialNumber, Position position) {
		if (m_DevicePane!=null) {
			DeviceEntry entry = m_devices.get(deviceSerialNumber);
			if (entry == null) 
				return;			
			m_DevicePane.moveDevice(entry, position);
		}
   }
	
	protected void changeDevice(DeviceEntry entry) {
		if (m_DevicePane!=null) {
			m_DevicePane.changeDevice(entry);
		}
		/*
		DeviceEntry entry = m_devices.get(deviceSerialNumber);
		if (entry == null)
			return;

		removeDeviceWidget(entry);

		
		if (properties != null) {
			DeviceEntry newEntry = getDeviceEntry(device, properties);
			entry.description = newEntry.description;
			entry.logicPosition = newEntry.logicPosition;
			entry.state = newEntry.state;
			entry.fault = newEntry.fault;
		}
		

		if (isAvailableForSelectedApplication(deviceSerialNumber))
			// addDeviceWidget(device, entry.description, entry.position,
			// deviceSerialNumber, entry);
			addDeviceWidget(entry);

		updateDeviceTable(deviceSerialNumber, device);
		*/
	}
	
	public void refreshDeviceWidgets() {
		if (m_DevicePane!=null) {
			Set<String> keys = m_devices.keySet();
			for (String key : keys) {
				DeviceEntry entry = m_devices.get(key);
				m_DevicePane.updateDeviceWidgetVisibility(entry);
	      }
		}
	}
	
	
	protected static Position generateBorderPosition() {
		// Find the next slot available.
		int i = nextSlotAvailable();
		BORDER_POSITIONS[i] = true;
		return new Position(20, (30 * i) + 20);
	}
	
	private static int nextSlotAvailable() {
		for (int i = 0; i < BORDER_POSITIONS.length; i++) {
			if (!BORDER_POSITIONS[i]) {
				return i;
			}
		}
		return BORDER_POSITIONS.length - 1;
	}
	
}
