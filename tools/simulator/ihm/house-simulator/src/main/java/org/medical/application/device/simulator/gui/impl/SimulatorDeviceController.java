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

	//private SimulationManager m_SimulationManager;
	
	public SimulatorDeviceController(SimulationManager simulationManager) {
		super(simulationManager);
	}
	
	private DeviceEntry createDeviceEntry(ApplicationDevice device, Map<String, Object> properties) {		
		return createDeviceEntry(device.getId(), properties);
	}
	
	public void addDevice(final ApplicationDevice device, Map<String, Object> properties) {
		DeviceEntry entry = createDeviceEntry(device, properties);
		addDevice(entry);
	}

	public void removeDevice(ApplicationDevice device) {
		removeDevice(device.getId());
   }
	
}
