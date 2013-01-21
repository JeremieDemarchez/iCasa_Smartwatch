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
package fr.liglab.adele.icasa.device.bathroomscale.impl;

import java.util.List;
import java.util.Random;

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
import fr.liglab.adele.icasa.device.bathroomscale.rest.api.BathroomScaleRestAPI;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.simulator.LocatedDevice;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.Position;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.Zone;
import fr.liglab.adele.icasa.simulator.listener.LocatedDeviceListener;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;

@Component(name = "iCASA.BathroomScale")
@Provides(properties = { @StaticServiceProperty(type = "java.lang.String", name = Constants.SERVICE_DESCRIPTION) })
public class SimulatedBathroomScaleImpl extends AbstractDevice implements BathroomScale, SimulatedDevice, PersonListener, LocatedDeviceListener {

	@ServiceProperty(name = GenericDevice.DEVICE_SERIAL_NUMBER, mandatory = true)
	private String m_serialNumber;

	@ServiceProperty(name = "state", value = "deactivated")
	private String state;

	@ServiceProperty(name = "fault", value = "no")
	@Property(name = "fault", value = "no")
	private String fault;

	@Requires
	private SimulationManager manager;

	@Requires(optional = true)
	private BathroomScaleRestAPI restAPI;
	
	Zone detectionZone = null;

	public SimulatedBathroomScaleImpl() {
		setPropertyValue(WEIGHT_PROPERTY, 0.0);
		setPropertyValue(PRESENCE_DETECTED_PROPERTY, false);
		setPropertyValue(DETECTION_SCOPE, 33);
	}

	@Validate
	protected void start() {
		manager.addListener(this);
	}

	@Invalidate
	protected void stop() {
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

	public void deviceAdded(LocatedDevice device) {
		// Creates its own detection zone
		if (device.getSerialNumber().equals(getSerialNumber())) {
			String zoneId = getSerialNumber() + "#zone";
			Position center = device.getCenterAbsolutePosition();
			int detectionScope = (Integer) getPropertyValue(DETECTION_SCOPE);
			detectionZone = manager.createZone(zoneId, center, detectionScope);
			device.attachObject(detectionZone);
		}
	}
	
	private void updateState() {
		System.out.println("UPDATE STATE : state=" + getState() + " falt=" + getFault());

		if (getState().equals(AbstractDevice.STATE_ACTIVATED) && getFault().equals(AbstractDevice.FAULT_NO)) {
			for (Person person : manager.getPersons()) {
				Position pos = person.getCenterAbsolutePosition();
				
				
				if (detectionZone.contains(person)) {
					boolean previousDetection = (Boolean) getPropertyValue(PRESENCE_DETECTED_PROPERTY);
					
					if (!previousDetection) {
						System.out.println(" - person " + person.getName() + " " + pos.x + "x" + pos.y);
						setPropertyValue(PRESENCE_DETECTED_PROPERTY, true);						
						float weight = computeWeight();
						setPropertyValue(WEIGHT_PROPERTY, weight);
						if (restAPI != null) {
							try {
								restAPI.sendMeasure(weight);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					return;
				}
			}
		}

		// Default behavior when 
		//  * Device is activated, but nobody next to it
		//  * Device is not activated 
		//  * Device is in error state
		setPropertyValue(PRESENCE_DETECTED_PROPERTY, false);
		setPropertyValue(WEIGHT_PROPERTY, 0.0);
	}

	public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue) {
		System.out.println("Device property modified : " + propertyName + " modified to " + device.getPropertyValue(propertyName));
		if (device.getSerialNumber().equals(getSerialNumber()))
			System.out.println("DEBUG : property " + propertyName + " modified to " + device.getPropertyValue(propertyName));
	}

	public float getCurrentWeight() {
		Boolean presence = (Boolean) getPropertyValue(PRESENCE_DETECTED_PROPERTY);
		Float weight = (Float) getPropertyValue(WEIGHT_PROPERTY);
		if (weight!=null)
			return weight;
		return 0.0f;
	}
	
	private float computeWeight() {
		return (float) 55 + ((float) new Random().nextInt(100)) / 10;
	}
	
	public void deviceRemoved(LocatedDevice device) {
		// do nothing
	}

	public void deviceMoved(LocatedDevice device, Position oldPosition) {
		if (device.getSerialNumber().equals(getSerialNumber()))
			updateState();
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

	public void enterInZones(List<Zone> zones) {
		// do nothing
	}

	public void leavingZones(List<Zone> zones) {
		// do nothing
	}
}
