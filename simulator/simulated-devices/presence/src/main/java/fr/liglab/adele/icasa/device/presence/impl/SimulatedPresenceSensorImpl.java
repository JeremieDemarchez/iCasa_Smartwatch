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
package fr.liglab.adele.icasa.device.presence.impl;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.DeviceEvent;
import fr.liglab.adele.icasa.device.DeviceEventType;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.LocatedDevice;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.Position;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.Zone;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;

/**
 * Implementation of a simulated presence sensor device.
 * 
 * @author Gabriel Pedraza Ferreira
 */
@Component(name="iCASA.PresenceSensor")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedPresenceSensorImpl extends AbstractDevice implements PresenceSensor, SimulatedDevice,
	PersonListener {

	@ServiceProperty(name = PresenceSensor.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@ServiceProperty(name = "state", value = "deactivated")
	private String state;

	@ServiceProperty(name = "fault", value = "no")
	private String fault;

	@Requires
	private SimulationManager manager;
	
	
	/**
	 * Influence zone corresponding to the zone with highest level where the device is located
	 */
	private volatile Zone m_zone;
	
	public SimulatedPresenceSensorImpl() {
		setPropertyValue(PRESENCE_SENSOR_SENSED_PRESENCE, false);
	}
	
	@Override
	public String getSerialNumber() {
		return m_serialNumber;
	}

	
	
	@Override
	public synchronized boolean getSensedPresence() {
		Boolean presence = (Boolean) getPropertyValue(PRESENCE_SENSOR_SENSED_PRESENCE);
		if (presence!=null)
			return presence;
		return false;
	}


	/**
	 * sets the state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the fault
	 */
	public String getFault() {
		return fault;
	}

	/**
	 * @param fault
	 *           the fault to set
	 */
	public void setFault(String fault) {
		this.fault = fault;
	}

	@Override
   public void enterInZones(List<Zone> zones) {
		if (!zones.isEmpty()) {
			m_zone = zones.get(0);
			peopleInZone();
		}						
   }

	@Override
   public void leavingZones(List<Zone> zones) {
	   m_zone = null;	   
   }

	@Override
   public void personAdded(Person person) {
	   peopleInZone();	   
   }

	@Override
   public void personRemoved(Person person) {
		peopleInZone();	   
   }

	@Override
   public void personMoved(Person person, Position oldPosition) {
		peopleInZone();	   
   }

	@Override
   public void personDeviceAttached(Person person, LocatedDevice device) {
		// Nothing to do	   
   }

	@Override
   public void personDeviceDetached(Person person, LocatedDevice device) {
	   // Nothing to do	   
   }
	
	/**
	 * Calculates if a person is found in the detection zone of this device. 
	 * When there is a change of previous detection a event is sent to listeners 
	 */
	private void peopleInZone() {
		if (m_zone!=null) {
			
			boolean detected = false;
			List<Person> persons = manager.getPersons();
			for (Person person : persons) {
	         if (m_zone.contains(person)) {
	         	detected = true;
	         	break;
	         }
         }
						
			Boolean previousDetection = (Boolean) getPropertyValue(PRESENCE_SENSOR_SENSED_PRESENCE);			
			if (previousDetection==null)
				previousDetection = false;
			
			if (previousDetection!=detected) {
				setPropertyValue(PRESENCE_SENSOR_SENSED_PRESENCE, detected);
			}

		}
	}
		
	@Validate
	protected void start() {
		manager.addListener(this);
	}

	@Invalidate
	protected void stop() {
		manager.removeListener(this);
	}
}
