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

import fr.liglab.adele.icasa.device.GenericDevice;

@Component
@Provides
@Instantiate(name = "SimulationManager-1")
public class SimulationManagerImpl implements SimulationManager, ZonePropListener, PersonListener,
      LocatedDeviceListener {

	private Map<String, Zone> zones = new HashMap<String, Zone>();

	private Map<String, LocatedDevice> locatedDevices = new HashMap<String, LocatedDevice>();

	private Map<String, SimulatedDevice> m_simulatedDevices = new HashMap<String, SimulatedDevice>();

	private Map<String, Person> persons = new HashMap<String, Person>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	private List<SimulationListener> listeners = new ArrayList<SimulationListener>();

	@Override
	public Zone createZone(String id, String description, int leftX, int topY, int width, int height) {
		Zone zone = new ZoneImpl(id, leftX, topY, width, height);
		zones.put(id, zone);

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.zoneAdded(zone);
				zone.addListener(listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return zone;
	}

	@Override
	public void removeZone(String id) {
		Zone zone = zones.remove(id);
		if (zone == null)
			return;

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				zone.removeListener(listener);
				listener.zoneRemoved(zone);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void moveZone(String id, int leftX, int topY) throws Exception {
		Zone zone = zones.get(id);
		if (zone == null)
			return;

		Position oldPosition = zone.getLeftTopPosition();
		Position newPosition = new Position(leftX, topY);
		zone.setLeftTopPosition(newPosition);

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.zoneMoved(zone, oldPosition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void resizeZone(String id, int width, int height) throws Exception {
		Zone zone = zones.get(id);
		if (zone == null)
			return;

		zone.resize(width, height);

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.zoneResized(zone);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
   public void addZoneVariable(String zoneId, String variable) {
		Zone zone = zones.get(zoneId);
		if (zone == null)
			return;
		zone.addVariable(variable);
   }
	
	@Override
	public Set<String> getZoneVariables(String zoneId) {
		Zone zone = zones.get(zoneId);
		if (zone == null)
			return null;
		return zone.getVariableNames();
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

		Object oldValue = zone.getVariableValue(variableName);
		zone.setVariableValue(variableName, value);

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.zoneVariableModified(zone, variableName, oldValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<Zone> getZones() {
		synchronized (zones) {
			return Collections.unmodifiableList(new ArrayList<Zone>(zones.values()));
		}
	}

	@Override
	public Set<String> getZoneIds() {
		synchronized (zones) {
			return Collections.unmodifiableSet(new HashSet<String>(zones.keySet()));
		}
	}

	@Override
	public Zone getZone(String zoneId) {
		synchronized (zones) {
			return zones.get(zoneId);
		}
	}

	@Override
	public Zone getZoneFromPosition(Position position) {
		for (Zone zone : zones.values()) {
			if (zone.contains(position))
				return zone;
		}
		return null;
	}

	@Override
	public void setParentZone(String zoneId, String parentId) throws Exception {
		Zone zone = getZone(zoneId);
		// TODO manage case of setting null parent
		Zone parent = getZone(parentId);
		if (zone == null || parent == null)
			return;
		boolean ok = parent.addZone(zone);
		if (!ok)
			throw new Exception("Zone does not fit in its parent");

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.zoneParentModified(zone, parent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Set<String> getDeviceIds() {
		return Collections.unmodifiableSet(new HashSet<String>(locatedDevices.keySet()));
	}

	@Override
	public List<LocatedDevice> getDevices() {
		return new ArrayList<LocatedDevice>(locatedDevices.values());
	}

	@Override
	public Position getDevicePosition(String deviceSerialNumber) {
		LocatedDevice device = locatedDevices.get(deviceSerialNumber);
		if (device != null)
			return device.getAbsolutePosition().clone();
		return null;
	}

	@Override
	public void setDevicePosition(String deviceSerialNumber, Position position) {
		LocatedDevice device = locatedDevices.get(deviceSerialNumber);
		if (device != null)
			device.setAbsolutePosition(position);
	}

	@Override
	public void moveDeviceIntoZone(String deviceSerialNumber, String zoneId) {
		moveLocatedObjectIntoZone(zoneId, getDevice(deviceSerialNumber));
	}

	@Override
	public void setPersonPosition(String userName, Position position) {
		Person person = persons.get(userName);
		if (person != null)
			person.setAbsolutePosition(position);
	}

	@Override
	public void setPersonZone(String userName, String zoneId) {
		moveLocatedObjectIntoZone(zoneId, getPerson(userName));
	}

	@Override
	public void removeAllPersons() {
		synchronized (persons) {
			for (Person person : persons.values()) {
				// Listeners notification
				for (SimulationListener listener : listeners) {
					try {
						person.removeListener(listener);
						listener.personRemoved(person);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			persons.clear();
		}
	}

	@Override
	public void addPerson(String userName) {
		Person person = new PersonImpl(userName, new Position(-1, -1), this);
		persons.put(userName, person);

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.personAdded(person);
				person.addListener(listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void removePerson(String userName) {
		Person person = persons.remove(userName);
		if (person == null)
			return;

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.personRemoved(person);
				person.removeListener(listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<Person> getPersons() {
		synchronized (persons) {
			return Collections.unmodifiableList(new ArrayList<Person>(persons.values()));
		}
	}

	@Override
	public Person getPerson(String personName) {
		synchronized (persons) {
			return persons.get(personName);
		}
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
	public LocatedDevice getDevice(String deviceId) {
		synchronized (locatedDevices) {
			return locatedDevices.get(deviceId);
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
		String sn = dev.getSerialNumber();
		m_simulatedDevices.put(sn, dev);
		if (!locatedDevices.containsKey(sn)) {
			String deviceType = null;
			if (dev instanceof Pojo) {
				try {
					deviceType = ((Pojo) dev).getComponentInstance().getFactory().getFactoryName();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			LocatedDevice device = new LocatedDeviceImpl(sn, new Position(-1, -1), dev, deviceType, this);
			locatedDevices.put(sn, device);

			// Listeners notification
			for (SimulationListener listener : listeners) {
				try {
					listener.deviceAdded(device);
					device.addListener(listener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Unbind(id = "sim-devices")
	public void unbindDevice(SimulatedDevice dev) {
		String sn = dev.getSerialNumber();
		m_simulatedDevices.remove(sn);
		LocatedDevice device = locatedDevices.remove(sn);

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.deviceRemoved(device);
				device.removeListener(listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.environment.SimulatedDevice)")
	public void bindFactory(Factory factory) {
		String deviceType = factory.getName();
		m_factories.put(deviceType, factory);

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.deviceTypeAdded(deviceType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Unbind(id = "factories")
	public void unbindFactory(Factory factory) {
		String deviceType = factory.getName();
		m_factories.remove(deviceType);

		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.deviceTypeRemoved(deviceType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addListener(SimulationListener listener) {
		synchronized (listeners) {
			listeners.add(listener);

			for (Zone zone : zones.values())
				zone.addListener(listener);

			for (Person person : persons.values())
				person.addListener(listener);

			for (LocatedDevice device : locatedDevices.values())
				device.addListener(listener);
		}
	}

	@Override
	public void removeListener(SimulationListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);

			for (Zone zone : zones.values())
				zone.removeListener(listener);

			for (Person person : persons.values())
				person.removeListener(listener);

			for (LocatedDevice device : locatedDevices.values())
				device.removeListener(listener);
		}
	}

	@Override
	public void attachDeviceToPerson(String personName, String deviceId) {
		Person person = getPerson(personName);
		if (person == null)
			return;
		LocatedDevice device = getDevice(deviceId);
		if (device == null)
			return;
		person.attachDevice(device);
	}

	@Override
	public void detachDeviceFromPerson(String personName, String deviceId) {
		Person person = getPerson(personName);
		if (person == null)
			return;
		LocatedDevice device = getDevice(deviceId);
		if (device == null)
			return;
		person.detachDevice(device);
	}

	private int random(int min, int max) {
		final double range = (max - 10) - (min + 10);
		if (range <= 0.0) {
			throw new IllegalArgumentException("min >= max");
		}
		return min + (int) (range * Math.random());
	}

	private void moveLocatedObjectIntoZone(String zoneId, LocatedObject object) {
		Zone zone = getZone(zoneId);
		if (zone == null || object == null)
			return;
		int minX = zone.getAbsoluteLeftTopPosition().x;
		int minY = zone.getAbsoluteLeftTopPosition().y;
		int newX = random(minX, minX + zone.getWidth());
		int newY = random(minY, minY + zone.getHeight());
		object.setAbsolutePosition(new Position(newX, newY));
	}

	/*
	 * Simulation Manager listener methods
	 */

	public void zoneVariableAdded(Zone zone, String variableName) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.zoneVariableAdded(zone, variableName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void zoneVariableRemoved(Zone zone, String variableName) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.zoneVariableRemoved(zone, variableName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void zoneVariableModified(Zone zone, String variableName, Object oldValue) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.zoneVariableModified(zone, variableName, oldValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void personAdded(Person person) {
		// notification already sent, do nothing
	}

	public void personRemoved(Person person) {
		// notification already sent, do nothing
	}

	public void personMoved(Person person, Position oldPosition) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.personMoved(person, oldPosition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void personDeviceAttached(Person person, LocatedDevice device) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.personDeviceAttached(person, device);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void personDeviceDetached(Person person, LocatedDevice device) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.personDeviceDetached(person, device);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void deviceAdded(LocatedDevice device) {
		// notification already sent, do nothing
	}

	public void deviceRemoved(LocatedDevice device) {
		// notification already sent, do nothing
	}

	public void deviceMoved(LocatedDevice device, Position oldPosition) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.deviceMoved(device, oldPosition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.devicePropertyModified(device, propertyName, oldValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void devicePropertyAdded(LocatedDevice device, String propertyName) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.devicePropertyAdded(device, propertyName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void devicePropertyRemoved(LocatedDevice device, String propertyName) {
		// Listeners notification
		for (SimulationListener listener : listeners) {
			try {
				listener.devicePropertyRemoved(device, propertyName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


}
