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
package fr.liglab.adele.icasa.application.device.web.common.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nextapp.echo.app.Extent;
import nextapp.echo.app.Label;

import fr.liglab.adele.icasa.application.device.web.common.impl.component.DeviceEntry;
import fr.liglab.adele.icasa.application.device.web.common.impl.component.DevicePane;
import fr.liglab.adele.icasa.application.device.web.common.impl.component.FloatingButton;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;

/**
 * Controller class that is notified in changes of the devices environment.
 * Subclasses have to transform original device model into
 * <code>DeviceEntry</code> model before invoke this class methods
 * 
 * @author Gabriel Pedraza
 * 
 */
public abstract class DeviceController {

	private static boolean[] BORDER_POSITIONS = new boolean[20];
	
	/**
	 * The <code>DevicePane</code> use to show modifications
	 */
	private DevicePane m_DevicePane;

	/**
	 * Map of added devices (<code>DeviceEntry</code>) into the GUI
	 */
	protected final Map<String, DeviceEntry> m_devices = new HashMap<String, DeviceEntry>();

	/**
	 * The iCasa Simulation Manager (used to determine device position)
	 */
	protected SimulationManager m_SimulationManager;

	/**
	 * Default constructor
	 * @param manager iCasa SimulationManager
	 */
	public DeviceController(SimulationManager manager) {
		m_SimulationManager = manager;
	}

	/**
	 * Sets the devicePane used to show device information
	 * @param devicePane the device pane
	 */
	public void setDevicePane(DevicePane devicePane) {
		m_DevicePane = devicePane;
	}

	/**
	 * Adds a device representation into the GUI
	 * @param entry the device representation
	 */
	protected void addDevice(DeviceEntry entry) {
		if (m_DevicePane != null) {
			if (m_devices.containsKey(entry.serialNumber)) {
				m_DevicePane.showErrorWindow("The device \"" + entry.serialNumber + "\"already exists.");
				return;
			}
			m_devices.put(entry.serialNumber, entry);
			m_DevicePane.addDevice(entry);
		}
	}

	/**
	 * Removes a device representation from the GUI
	 * @param serialNumber the serial number of device to remove 
	 */
	protected void removeDevice(String serialNumber) {
		if (m_DevicePane != null) {
			if (!m_devices.containsKey(serialNumber))
				return;
			DeviceEntry entry = m_devices.remove(serialNumber);
			m_DevicePane.removeDevice(entry);
		}

	}

	/**
	 * Move the device into a new position
	 * @param deviceSerialNumber the serial number of device to move 
	 * @param position the new position (x, y coordinates)
	 */
	public void moveDevice(String deviceSerialNumber, Position position) {
		if (m_DevicePane != null) {
			DeviceEntry entry = m_devices.get(deviceSerialNumber);
			if (entry == null)
				return;
			
			if (((Extent) entry.widget.get(FloatingButton.PROPERTY_POSITION_X)).getValue() == 0 && position != null) { 			
				int y = ((Extent) entry.widget.get(FloatingButton.PROPERTY_POSITION_Y)).getValue();
				BORDER_POSITIONS[y / 20] = false;
			}
			if (position == null) {
				position = generateBorderPosition();
			}
			String logicPosition = m_SimulationManager.getEnvironmentFromPosition(position);
			if (logicPosition == null)
				logicPosition = "unassigned";

			if (entry.position.equals(position) && logicPosition.equals(entry.logicPosition))
				return;

			entry.position = position;
			entry.logicPosition = logicPosition;
			
			m_DevicePane.moveDevice(entry, position);
		}
	}

	/**
	 * Change the device information (properties)
	 * @param deviceSerialNumber the serial number of device to change
	 * @param properties the new properties
	 */
	public void changeDevice(String deviceSerialNumber, final Map<String, Object> properties) {
		DeviceEntry entry = m_devices.get(deviceSerialNumber);
		if (entry == null)
			return;

		DeviceEntry newEntry = createDeviceEntry(deviceSerialNumber, properties);

		if (newEntry != null) {
			entry.description = newEntry.description;
			entry.logicPosition = newEntry.logicPosition;
			entry.state = newEntry.state;
			entry.fault = newEntry.fault;
			
			if (m_DevicePane != null) 
				m_DevicePane.changeDevice(entry);
			
		}
		
	}

	/**
	 * Creates a new device representation (<code>DeviceEntry</code>) using its serial number and properties
	 * @param deviceSerialNumber the device serial number
	 * @param properties the device properties
	 * @return the new entry
	 */
	public DeviceEntry createDeviceEntry(String deviceSerialNumber, final Map<String, Object> properties) {
		if (properties != null) {
			Position position = m_SimulationManager.getDevicePosition(deviceSerialNumber);

			if (position == null) {
				position = generateBorderPosition();
			}

			String description = (String) properties.get(Constants.SERVICE_DESCRIPTION);
			if (description == null)
				description = deviceSerialNumber;

			String state = (String) properties.get("state");
			if (state == null)
				state = "unknown";

			String fault = (String) properties.get("fault");
			if (fault == null)
				fault = "unknown";

			String logicPosition = m_SimulationManager.getEnvironmentFromPosition(position);
			if (logicPosition == null)
				logicPosition = "unassigned";

			final DeviceEntry entry = new DeviceEntry();
			entry.serialNumber = deviceSerialNumber;
			entry.state = state;
			entry.fault = fault;
			entry.label = new Label(description);
			entry.position = position;
			entry.logicPosition = logicPosition;
			entry.description = description;

			return entry;
		}

		return null;
	}

	/**
	 * Refresh the device GUI representation
	 */
	public void refreshDeviceWidgets() {
		if (m_DevicePane != null) {
			Set<String> keys = m_devices.keySet();
			for (String key : keys) {
				DeviceEntry entry = m_devices.get(key);
				m_DevicePane.changeDevice(entry);
			}
		}
	}

	/**
	 * Generates a position in the left side of GUI
	 * @return a new Position (x, y coordinates)
	 */
	private static Position generateBorderPosition() {
		// Find the next slot available.
		int i = nextSlotAvailable();
		BORDER_POSITIONS[i] = true;
		return new Position(20, (30 * i) + 20);
	}

	/**
	 * Calculates the next slot (site) available into the left side of GUI
	 * @return
	 */
	private static int nextSlotAvailable() {
		for (int i = 0; i < BORDER_POSITIONS.length; i++) {
			if (!BORDER_POSITIONS[i]) {
				return i;
			}
		}
		return BORDER_POSITIONS.length - 1;
	}

}
