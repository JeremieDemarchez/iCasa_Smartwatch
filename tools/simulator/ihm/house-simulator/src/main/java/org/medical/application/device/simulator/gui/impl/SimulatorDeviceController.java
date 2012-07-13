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
package org.medical.application.device.simulator.gui.impl;

import java.util.Map;

import nextapp.echo.app.Label;

import org.medical.application.device.web.common.impl.DeviceController;
import org.medical.application.device.web.common.impl.component.DeviceEntry;
import org.medical.device.manager.ApplicationDevice;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;

public class SimulatorDeviceController extends DeviceController {

	private SimulationManager m_SimulationManager;
	
	public SimulatorDeviceController(SimulationManager simulationManager) {
		m_SimulationManager = simulationManager;
	}
	
	private DeviceEntry getDeviceEntry(ApplicationDevice device, Map<String, Object> properties) {
		String description = (String) properties.get(Constants.SERVICE_DESCRIPTION);
		if (description == null) {
			// If service description is not defined, use the device serial number.
			description = device.getId();
		}

		Position position = m_SimulationManager.getDevicePosition(device.getId());

		if (position == null) {
			position = generateBorderPosition();
		}
		final String serialNumber = device.getId();

		String state = (String) properties.get("state");
		if (state == null)
			state = "unknown";

		String fault = (String) properties.get("fault");
		if (fault == null)
			fault = "uknown";

		String logicPosition = m_SimulationManager.getEnvironmentFromPosition(position);
		if (logicPosition == null)
			logicPosition = "unassigned";

		final DeviceEntry entry = new DeviceEntry();
		entry.serialNumber = serialNumber;
		entry.state = state;
		entry.fault = fault;
		entry.label = new Label(description);
		entry.position = position;
		entry.logicPosition = logicPosition;
		entry.description = description;

		return entry;
	}
	
	public void addDevice(final ApplicationDevice device, Map<String, Object> properties) {
		DeviceEntry entry = getDeviceEntry(device, properties);
		addDevice(entry);
	}

	public void removeDevice(ApplicationDevice device) {
		removeDevice(device.getId());
   }
	
	public void changeDevice(String deviceSerialNumber, ApplicationDevice device, Map<String, Object> properties) {
		DeviceEntry entry = m_devices.get(deviceSerialNumber);
		if (entry == null)
			return;
		
		if (properties != null) {
			DeviceEntry newEntry = getDeviceEntry(device, properties);
			entry.description = newEntry.description;
			entry.logicPosition = newEntry.logicPosition;
			entry.state = newEntry.state;
			entry.fault = newEntry.fault;
		}
		
	}
	
}
