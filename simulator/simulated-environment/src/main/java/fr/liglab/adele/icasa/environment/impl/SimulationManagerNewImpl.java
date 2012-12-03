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
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.liglab.adele.icasa.environment.*;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.Pojo;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.LocatedDevice;

@Component(name = "SimulationManagerNew")
@Provides(specifications=SimulationManagerNew.class)
@Instantiate(name = "SimulationManagerNew-0")
public class SimulationManagerNewImpl implements SimulationManagerNew {

	private Map<String, Zone> zones = new HashMap<String, Zone>();

	private Map<String, LocatedDevice> devices = new HashMap<String, LocatedDevice>();

	private Map<String, SimulatedDevice> m_simulatedDevices = new HashMap<String, SimulatedDevice>();

	private Map<String, Person> persons = new HashMap<String, Person>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	private List<SimulationListener> listeners = new ArrayList<SimulationListener>();

	@Override
	public Zone createZone(String id, String description, int leftX, int topY, int width, int height) {
		Zone zone = new ZoneImpl(id, leftX, topY, width, height);
		zones.put(id, zone);

		// Listeners notification
		for (SimulationListener listener : listeners)
			listener.zoneAdded(zone);

		return zone;
	}

	@Override
	public void removeZone(String id) {
		Zone zone = zones.remove(id);
		if (zone == null)
			return;

		// Listeners notification
		for (SimulationListener listener : listeners)
			listener.zoneRemoved(zone);
	}

	@Override
	public void moveZone(String id, int leftX, int topY) {
		Zone zone = zones.get(id);
		if (zone == null)
			return;

		Position newPosition = new Position(leftX, topY);
		zone.setLeftTopPosition(newPosition);
	}

	@Override
	public void resizeZone(String id, int width, int height) {
		Zone zone = zones.get(id);
		if (zone == null)
			return;

		zone.resize(height, width);
	}

	@Override
	public Set<String> getZoneVariables(String zoneId) {
		Zone zone = zones.get(zoneId);
		if (zone == null)
			return null;
		return zone.getVariableList();
	}

	@Override
	public Object getZoneVariableValue(String zoneId, String variable) {
		Zone zone = zones.get(zoneId);
		if (zone == null)
			return null;
		return zone.getVariableValue(variable);
	}

	@Override
	public void setZoneVariable(String zoneId, String variableName, Object value) {
		Zone zone = zones.get(zoneId);
		if (zone == null)
			return;
		
		zone.setVariableValue(variableName, value);
	}

	@Override
	public List<Zone> getZones() {
		return new ArrayList<Zone>(zones.values());
	}
	
	@Override
	public Zone getZone(String zoneId) {
		return zones.get(zoneId);
	}

	@Override
	public List<LocatedDevice> getDevices() {
		return new ArrayList<LocatedDevice>(devices.values());
	}

	@Override
	public Position getDevicePosition(String deviceSerialNumber) {
		LocatedDevice device = devices.get(deviceSerialNumber);
		if (device != null)
			return device.getAbsolutePosition().clone();
		return null;
	}

	@Override
	public void setDevicePosition(String deviceSerialNumber, Position position) {
		LocatedDevice device = devices.get(deviceSerialNumber);
		if (device != null)
			device.setAbsolutePosition(position);
	}

	@Override
	public void moveDeviceIntoZone(String deviceSerialNumber, String zoneId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPersonPosition(String userName, Position position) {
		Person person = persons.get(userName);
		if (person != null)
			person.setAbsolutePosition(position);
	}

	@Override
	public void setPersonZone(String userName, String environmentId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAllPersons() {
		persons.clear();
	}

	@Override
	public void addPerson(String userName) {
		Person person = new PersonImpl(userName, null, null);
		persons.put(userName, person);
		
		// Listeners notification
		for (SimulationListener listener : listeners)
			listener.personAdded(person);
		
		
	}

	@Override
	public void removePerson(String userName) {
		Person person = persons.remove(userName);
		if (person==null)
			return;
		// Listeners notification
		for (SimulationListener listener : listeners)
			listener.personRemoved(person);
	}

	@Override
	public List<Person> getPersons() {
		return new ArrayList<Person>(persons.values());
	}

	@Override
	public void setDeviceFault(String deviceId, boolean value) {
		SimulatedDevice device = m_simulatedDevices.get(deviceId);
		if (device != null) {
			if (value)
				device.setFault(SimulatedDevice.FAULT_YES);
			else
				device.setFault(SimulatedDevice.FAULT_NO);
		}
	}

	@Override
	public void setDeviceState(String deviceId, boolean value) {
		SimulatedDevice device = m_simulatedDevices.get(deviceId);
		if (device != null) {
			if (value)
				device.setState(SimulatedDevice.STATE_ACTIVATED);
			else
				device.setState(SimulatedDevice.STATE_DEACTIVATED);
		}
	}

	@Override
	public void createDevice(String factoryName, String deviceId, Map<String, Object> properties) {
		Factory factory = m_factories.get(factoryName);
		if (factory != null) {
			// Create the device
			Dictionary<String, String> configProperties = new Hashtable<String, String>();
			configProperties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);
			configProperties.put(GenericDevice.STATE_PROPERTY_NAME, GenericDevice.STATE_ACTIVATED);
			configProperties.put(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_NO);
			if (properties.get("description") != null)
				configProperties.put(Constants.SERVICE_DESCRIPTION, properties.get("description").toString());
			configProperties.put("instance.name", factoryName + "-" + deviceId);
			try {
				factory.createComponentInstance(configProperties);
			} catch (UnacceptableConfiguration e) {
				e.printStackTrace();
			} catch (MissingHandlerException e) {
				e.printStackTrace();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void removeDevice(String deviceId) {
		SimulatedDevice device = m_simulatedDevices.get(deviceId);
		if ((device != null) && (device instanceof Pojo)) {
			Pojo pojo = (Pojo) device;
			pojo.getComponentInstance().dispose();
		}
	}

	@Override
	public Set<String> getDeviceTypes() {
		return Collections.unmodifiableSet(new HashSet<String>(m_factories.keySet()));
	}



	@Bind(id = "sim-devices", aggregate = true, optional = true)
	public void bindDevice(SimulatedDevice dev) {
		System.out.println("+--------  LocatedDevice " + dev.getSerialNumber());
		m_simulatedDevices.put(dev.getSerialNumber(), dev);
	}

	@Unbind(id = "sim-devices")
	public void unbindDevice(ServiceReference reference) {

	}

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.environment.SimulatedDevice)")
	public void bindFactory(Factory factory) {
		m_factories.put(factory.getName(), factory);
		System.out.println("+--------");
	}

	@Unbind(id = "factories")
	public void unbindFactory(Factory factory) {
		m_factories.remove(factory.getName());
	}

	@Override
	public void addListener(SimulationListener listener) {
		listeners.add(listener);

		for (Zone zone : zones.values())
			zone.addListener(listener);

		for (Person person : persons.values())
			person.addListener(listener);

		for (LocatedDevice device : devices.values())
			device.addListener(listener);
	}

	@Override
	public void removeListener(SimulationListener listener) {
		listeners.remove(listener);

		for (Zone zone : zones.values())
			zone.removeListener(listener);

		for (Person person : persons.values())
			person.removeListener(listener);

		for (LocatedDevice device : devices.values())
			device.removeListener(listener);
	}

}
