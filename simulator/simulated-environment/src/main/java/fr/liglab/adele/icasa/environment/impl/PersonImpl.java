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
import java.util.List;

import fr.liglab.adele.icasa.environment.Person;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.Zone;
import fr.liglab.adele.icasa.environment.listener.PersonListener;

/**
 * TODO
 * 
 * @author Thomas Leveque Date: 10/11/12
 */
public class PersonImpl  extends LocatedObjectImpl implements Person {

	private String m_name;

	private List<PersonListener> listeners = new ArrayList<PersonListener>();
	
	private SimulationManager manager;

	public PersonImpl(String name, Position position, SimulationManager manager) {
		super(position);
		m_name = name;
		this.manager = manager;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String getLocation() {
		Zone zone = manager.getZoneFromPosition(getAbsoluteCenterPosition());
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
	public void setAbsoluteCenterPosition(Position position) {
		Position oldPosition = getAbsoluteCenterPosition();
		super.setAbsoluteCenterPosition(position);

		// Listeners notification
		for (PersonListener listener : listeners) {
			listener.personMoved(this, oldPosition);
		}
	}

	@Override
	public String toString() {
		return "Person " + m_name + " - Position " + getAbsoluteCenterPosition();
	}

}
