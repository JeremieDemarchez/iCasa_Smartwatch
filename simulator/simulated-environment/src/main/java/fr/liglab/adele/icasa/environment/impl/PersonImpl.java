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
package fr.liglab.adele.icasa.environment.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.liglab.adele.icasa.environment.LocatedDevice;
import fr.liglab.adele.icasa.environment.Person;
import fr.liglab.adele.icasa.environment.PersonListener;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulationManagerNew;
import fr.liglab.adele.icasa.environment.Zone;

/**
 * TODO
 * 
 * @author Thomas Leveque Date: 10/11/12
 */
public class PersonImpl implements Person {

	private String m_name;
	private Position m_position;

	private Map<String, LocatedDevice> m_devices = new HashMap<String, LocatedDevice>();

	private List<PersonListener> listeners = new ArrayList<PersonListener>();
	
	private SimulationManagerNew manager;

	public PersonImpl(String name, Position position, SimulationManagerNew manager) {
		m_name = name;
		m_position = position.clone();
		this.manager = manager;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String getLocation() {
		Zone zone = manager.getZoneFromPosition(m_position);
		if (zone!=null)			
			return zone.getId();
		return "unknown";
	}

	@Override
	public void addListener(PersonListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(PersonListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setName(String name) {
		m_name = name;
	}

	@Override
	public Position getAbsolutePosition() {
		return m_position.clone();
	}

	@Override
	public void setAbsolutePosition(Position position) {
		Position oldPosition = m_position.clone();
		m_position = position.clone();

		// Listeners notification
		for (PersonListener listener : listeners) {
			listener.personMoved(this, oldPosition);
		}
	}

	@Override
	public String toString() {
		return "Person " + m_name + " - Position " + m_position;
	}

	@Override
	public void attachDevice(LocatedDevice device) {
		if (m_devices.containsKey(device.getSerialNumber()))
			return;
		
		m_devices.put(device.getSerialNumber(), device);

		// Listeners notification
		for (PersonListener listener : listeners)
			listener.personDeviceAttached(this, device);

	}

	@Override
	public void detachDevice(LocatedDevice device) {
		LocatedDevice deviceToDetach = m_devices.remove(device.getSerialNumber());
		// Listeners notification
		if (deviceToDetach != null)
			for (PersonListener listener : listeners)
				listener.personDeviceDetached(this, device);
	}

}
