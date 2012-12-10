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
package fr.liglab.adele.icasa.device.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.bathroomscale.BathroomScale;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.environment.LocatedDevice;
import fr.liglab.adele.icasa.environment.Person;
import fr.liglab.adele.icasa.environment.PersonListener;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulationListener;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.Zone;

@Component(name = "iCASA.BathroomScale")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedBathroomScaleImpl extends AbstractDevice implements BathroomScale, SimulatedDevice, PersonListener, SimulationListener {

	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@ServiceProperty(name = "state", value = "deactivated")
	private String state;

	@ServiceProperty(name = "fault", value = "no")
	@Property(name = "fault", value = "no")
	private String fault;

	@Requires
	private SimulationManager manager;

	
	// TODO ADD PROPRIETES
	// TODO is there someone on the bathroom scale ?
	// TODO compute weight
	
	public SimulatedBathroomScaleImpl() {
		setPropertyValue("poids", 0.0);
		setPropertyValue("ya_qqun?", 0.0);
	}
	
	@Validate
	private void start() {
		manager.addListener(this);
	}
	
	@Invalidate
	private void stop() {
		manager.removeListener(this);
	}
	
	public String getSerialNumber() {
		return m_serialNumber;
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
	 *            the fault to set
	 */
	public void setFault(String fault) {
		this.fault = fault;
	}
	
	private void updateState() {
		long distanceMin2 = 1100; 
	
		if (getState().equals(AbstractDevice.STATE_ACTIVATED) && getFault().equals(AbstractDevice.FAULT_NO)) {
			Position deviceLoc = manager.getDevicePosition(getSerialNumber());	
			
			for (Person person : manager.getPersons()) {
				Position personLoc = person.getAbsolutePosition();
				long deltaX2 = (((long) personLoc.x) - ((long) deviceLoc.x)) * (((long) personLoc.x) - ((long) deviceLoc.x));
				long deltaY2 = (((long) personLoc.y) - ((long) deviceLoc.y)) * (((long) personLoc.y) - ((long) deviceLoc.y));
				long distance2 = deltaX2 + deltaY2;

				if (distance2 < distanceMin2) {
					System.out.println("Présence de " + person.getName() + " détectée sur le device (" + distance2 + ")");
					return;
				}
			}
		} else {
			System.out.println("Device non fonctionnel\n");
			return;
		}

		System.out.println("Device fonctionnel, mais personne à proximité");
	}

	public void deviceAdded(LocatedDevice device) {
		// do nothing
	}

	public void deviceRemoved(LocatedDevice device) {
		// do nothing		
	}

	public void deviceMoved(LocatedDevice device, Position oldPosition) {
		if (device.getSerialNumber().equals(getSerialNumber()))
			updateState();
	}

	public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue) {
		if (device.getSerialNumber().equals(getSerialNumber()))
			System.out.println("DEBUG : property " + propertyName + " modified to " + device.getPropertyValue(propertyName));
	}

	public void devicePropertyAdded(LocatedDevice device, String propertyName) {
		// do nothing		
	}

	public void devicePropertyRemoved(LocatedDevice device, String propertyName) {
		// do nothing
	}

	public void personAdded(Person person) {
		updateState();
	}

	public void personRemoved(Person person) {
		updateState();
	}

	public void personMoved(Person person, Position oldPosition) {
		updateState();		
	}

	public void personDeviceAttached(Person person, LocatedDevice device) {
		updateState();
	}

	public void personDeviceDetached(Person person, LocatedDevice device) {
		updateState();
	}

	public float getCurrentWeight() {
		// TODO regarder s'il y a quelqu'un sur la balance
		// TODO calculer le poids
		
		return 0;
	}

	public void zoneAdded(Zone zone) {
		// TODO to be revoved as soos as api will be updated
	}

	public void zoneRemoved(Zone zone) {
		// TODO to be revoved as soos as api will be updated
	}

	public void zoneMoved(Zone zone, Position oldPosition) {
		// TODO to be revoved as soos as api will be updated
	}

	public void zoneResized(Zone zone) {
		// TODO to be revoved as soos as api will be updated
	}

	public void zoneParentModified(Zone zone, Zone oldParentZone) {
		// TODO to be revoved as soos as api will be updated
	}

	public void zoneVariableAdded(Zone zone, String variableName) {
		// TODO to be revoved as soos as api will be updated
	}

	public void zoneVariableRemoved(Zone zone, String variableName) {
		// TODO to be revoved as soos as api will be updated
	}

	public void zoneVariableModified(Zone zone, String variableName, Object oldValue) {
		// TODO to be revoved as soos as api will be updated
	}

	public void deviceTypeAdded(String deviceType) {
		// TODO to be revoved as soos as api will be updated
	}

	public void deviceTypeRemoved(String deviceType) {
		// TODO to be revoved as soos as api will be updated
	}
}
