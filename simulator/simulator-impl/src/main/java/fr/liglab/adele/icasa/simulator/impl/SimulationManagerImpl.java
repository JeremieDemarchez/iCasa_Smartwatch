/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.Pojo;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.listener.IcasaListener;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;
import fr.liglab.adele.icasa.simulator.listener.PersonTypeListener;

@Component
@Provides
@Instantiate(name = "SimulationManager-1")
public class SimulationManagerImpl implements SimulationManager {

	@Requires
	ContextManager manager;


	private Map<String, Person> persons = new HashMap<String, Person>();

	private Map<String, SimulatedDevice> m_devices = new HashMap<String, SimulatedDevice>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	private List<PersonListener> personListeners = new ArrayList<PersonListener>();

	private List<PersonTypeListener> personTypeListeners = new ArrayList<PersonTypeListener>();

	private List<String> personTypes = new ArrayList<String>();

	public SimulationManagerImpl() {
		addPersonType("Grandfather");
		addPersonType("Grandmother");
		addPersonType("Father");
		addPersonType("Mother");
		addPersonType("Boy");
		addPersonType("Girl");
	}

	@Override
	public Zone createZone(String id, int leftX, int topY, int width, int height) {
		return manager.createZone(id, leftX,topY, width, height);
	}
	@Override
	public Zone createZone(String id, Position center, int detectionScope) {
		return manager.createZone(id, center, detectionScope);
	}

	@Override
	public void removeZone(String id) {
		manager.removeZone(id);
	}

	@Override
	public void moveZone(String id, int leftX, int topY) throws Exception {
		manager.moveZone(id, leftX, topY);
	}

	@Override
	public void resizeZone(String id, int width, int height) throws Exception {
		manager.resizeZone(id, width, height);
	}

	@Override
	public void addZoneVariable(String zoneId, String variable) {
		manager.addZoneVariable(zoneId, variable);
	}

	@Override
	public Set<String> getZoneVariables(String zoneId) {
		return manager.getZoneVariables(zoneId);
	}

	@Override
	public Object getZoneVariableValue(String zoneId, String variable) {
		return manager.getZoneVariableValue(zoneId, variable);
	}

	@Override
	public void setZoneVariable(String zoneId, String variableName, Object value) {
		manager.setZoneVariable(zoneId, variableName, value);
	}

	@Override
	public List<Zone> getZones() {
		return manager.getZones();
	}

	@Override
	public Set<String> getZoneIds() {
		return manager.getZoneIds();
	}

	@Override
	public Zone getZone(String zoneId) {
		return manager.getZone(zoneId);
	}

	@Override
	public Zone getZoneFromPosition(Position position) {
		return manager.getZoneFromPosition(position);
	}

	@Override
	public void setParentZone(String zoneId, String parentId) throws Exception {
		manager.setParentZone(zoneId, parentId);
	}

	@Override
	public Set<String> getDeviceIds() {
		return manager.getDeviceIds();
	}

	@Override
	public List<LocatedDevice> getDevices() {
		return manager.getDevices();
	}

	@Override
	public Position getDevicePosition(String deviceId) {
		return manager.getDevicePosition(deviceId);
	}

	@Override
	public void setDevicePosition(String deviceId, Position position) {
		manager.setDevicePosition(deviceId, position);
	}

	@Override
	public void moveDeviceIntoZone(String deviceId, String zoneId) {
		manager.moveDeviceIntoZone(deviceId, zoneId);
	}



	@Override
	public void setPersonPosition(String userName, Position position) {
		Person person = persons.get(userName);
		if (person != null)
			person.setCenterAbsolutePosition(position);
	}

	@Override
	public void setPersonZone(String userName, String zoneId) {
		Person person = getPerson(userName);
		if (person != null) {
			Position newPosition = getRandomPositionIntoZone(zoneId);
			if (newPosition != null)
				person.setCenterAbsolutePosition(newPosition);
		}
	}

	@Override
	public void removeAllPersons() {
		synchronized (persons) {
			for (Person person : persons.values()) {
				// Listeners notification
				for (PersonListener listener : personListeners) {
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
	public void addPerson(String userName, String personType) {
		String aPersonType = getPersonType(personType);
		if (aPersonType == null)
			return;

		Person person = new PersonImpl(userName, new Position(-1, -1), aPersonType, this);
		persons.put(userName, person);

		// Listeners notification
		for (PersonListener listener : personListeners) {
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
		for (PersonListener listener : personListeners) {
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
		LocatedDevice device = manager.getDevice(deviceId);

		if (value) {
			device.setPropertyValue(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_YES);
		}
		else {
			device.setPropertyValue(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_YES);
		}

	}

	@Override
	public void setDeviceState(String deviceId, boolean value) {
		manager.setDeviceState(deviceId, value);
	}

	@Override
	public void createDevice(String factoryName, String deviceId, Map<String, Object> properties) {
		Factory factory = m_factories.get(factoryName);
		if (factory != null) {
			// Create the device
			Dictionary<String, String> configProperties = new Hashtable<String, String>();
			configProperties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);
			// configProperties.put(GenericDevice.STATE_PROPERTY_NAME,
			// GenericDevice.STATE_ACTIVATED);
			// configProperties.put(GenericDevice.FAULT_PROPERTY_NAME,
			// GenericDevice.FAULT_NO);
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
		return manager.getDevice(deviceId);
	}

	@Override
	public void removeDevice(String deviceId) {
		GenericDevice device = m_devices.get(deviceId);

		if (device == null && !(device instanceof SimulatedDevice))
			return;

		if (device instanceof Pojo) {
			Pojo pojo = (Pojo) device;
			pojo.getComponentInstance().dispose();
		}
	}



	@Bind(id = "devices", aggregate = true, optional = true)
	public void bindDevice(SimulatedDevice simDev) {
		String sn = simDev.getSerialNumber();
		m_devices.put(sn, simDev);

	}

	@Unbind(id = "devices")
	public void unbindDevice(SimulatedDevice simDev) {
		String sn = simDev.getSerialNumber();
		m_devices.remove(sn);
	}

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.simulator.SimulatedDevice)")
	public void bindFactory(Factory factory) {
		String deviceType = factory.getName();
		m_factories.put(deviceType, factory);

	}

	@Unbind(id = "factories")
	public void unbindFactory(Factory factory) {
		String deviceType = factory.getName();
		m_factories.remove(deviceType);

	}

	@Override
	public void addListener(IcasaListener listener) {


		if (listener instanceof PersonListener) {
			PersonListener personListener = (PersonListener) listener;
			synchronized (personListeners) {
				personListeners.add(personListener);
				for (Person person : persons.values())
					person.addListener(personListener);
			}
		}

		if (listener instanceof PersonTypeListener) {
			PersonTypeListener personTypeListener = (PersonTypeListener) listener;
			synchronized (personTypeListeners) {
				personTypeListeners.add(personTypeListener);
			}
		}
		manager.addListener(listener);

	}

	@Override
	public void removeListener(IcasaListener listener) {


		if (listener instanceof PersonListener) {
			PersonListener personListener = (PersonListener) listener;
			synchronized (personListeners) {
				personListeners.remove(personListener);
				for (Person person : persons.values())
					person.removeListener(personListener);
			}
		}

		if (listener instanceof PersonTypeListener) {
			PersonTypeListener personTypeListener = (PersonTypeListener) listener;
			synchronized (personTypeListeners) {
				personTypeListeners.remove(personTypeListener);
			}
		}

		manager.removeListener(listener);
	}

	@Override
	public void attachDeviceToPerson(String deviceId, String personId) {
		LocatedDevice device = manager.getDevice(deviceId);
		Person person = persons.get(personId);

		if (device == null || person == null)
			return;

		person.attachObject(device);
	}

	@Override
	public void detachDeviceFromPerson(String deviceId, String personId) {
		LocatedDevice device = manager.getDevice(deviceId);
		Person person = persons.get(personId);

		if (device == null || person == null)
			return;

		person.detachObject(device);
	}

	@Override
	public void attachZoneToDevice(String zoneId, String deviceId) {
		Zone zone = manager.getZone(zoneId);
		LocatedDevice device = manager.getDevice(deviceId);
		if (zone == null || device == null)
			return;
		device.attachObject(zone);
	}

	@Override
	public void detachZoneFromDevice(String zoneId, String deviceId) {
		Zone zone = manager.getZone(zoneId);
		LocatedDevice device = manager.getDevice(deviceId);
		if (zone == null || device == null)
			return;
		device.detachObject(zone);
	}

	@Override
	public void attachDeviceToZone(String deviceId, String zoneId) {
		Zone zone = manager.getZone(zoneId);
		LocatedDevice device = manager.getDevice(deviceId);
		if (zone == null || device == null)
			return;

		zone.attachObject(device);

	}

	@Override
	public void detachDeviceFromZone(String deviceId, String zoneId) {
		Zone zone = manager.getZone(zoneId);
		LocatedDevice device = manager.getDevice(deviceId);
		if (zone == null || device == null)
			return;
		zone.detachObject(device);
	}

	@Override
	public void attachPersonToZone(String personId, String zoneId) {
		Zone zone = manager.getZone(zoneId);
		Person person = persons.get(personId);
		if (zone == null || person == null)
			return;

		zone.attachObject(person);
	}

	@Override
	public void detachPersonFromZone(String personId, String zoneId) {
		Zone zone = manager.getZone(zoneId);
		Person person = persons.get(personId);
		if (zone == null || person == null)
			return;

		zone.detachObject(person);
	}

	private int random(int min, int max) {
		final double range = (max - 10) - (min + 10);
		if (range <= 0.0) {
			throw new IllegalArgumentException("min >= max");
		}
		return min + (int) (range * Math.random());
	}

	private Position getRandomPositionIntoZone(String zoneId) {
		Zone zone = getZone(zoneId);
		if (zone == null)
			return null;
		int minX = zone.getLeftTopAbsolutePosition().x;
		int minY = zone.getLeftTopAbsolutePosition().y;
		int newX = random(minX, minX + zone.getWidth());
		int newY = random(minY, minY + zone.getHeight());
		return new Position(newX, newY);
	}

	@Override
	public void addPersonType(String personType) {
		if (!personTypes.contains(personType)) {
			personTypes.add(personType);
			for (PersonTypeListener listener : personTypeListeners)
				listener.personTypeAdded(personType);
		}
	}

	@Override
	public String getPersonType(String personType) {
		if (personTypes.contains(personType))
			return personType;
		return null;
	}

	@Override
	public void removePersonType(String personType) {
		if (personType.contains(personType)) {
			personTypes.remove(personType);
			for (PersonTypeListener listener : personTypeListeners)
				listener.personTypeRemoved(personType);
		}
	}

	@Override
	public List<String> getPersonTypes() {
		return Collections.unmodifiableList(personTypes);
	}

	@Override
	public Set<String> getDeviceTypes() {
		return manager.getDeviceTypes();
	}

	/* (non-Javadoc)
	 * @see fr.liglab.adele.icasa.simulator.SimulationManager#getSimulatedDeviceTypes()
	 */
	@Override
	public Set<String> getSimulatedDeviceTypes() {
		return Collections.unmodifiableSet(new HashSet<String>(m_factories.keySet()));
	}


}
