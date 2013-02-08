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
package fr.liglab.adele.icasa.simulator.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.felix.ipojo.annotations.Unbind;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.simulator.LocatedDevice;
import fr.liglab.adele.icasa.simulator.LocatedObject;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.Position;
import fr.liglab.adele.icasa.simulator.SimulatedDevice;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.Zone;
import fr.liglab.adele.icasa.simulator.impl.util.ZoneComparable;
import fr.liglab.adele.icasa.simulator.listener.DeviceTypeListener;
import fr.liglab.adele.icasa.simulator.listener.IcasaListener;
import fr.liglab.adele.icasa.simulator.listener.LocatedDeviceListener;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;
import fr.liglab.adele.icasa.simulator.listener.PersonTypeListener;
import fr.liglab.adele.icasa.simulator.listener.ZoneListener;

@Component
@Provides
@Instantiate(name = "SimulationManager-1")
public class SimulationManagerImpl implements SimulationManager {

	private Map<String, Zone> zones = new HashMap<String, Zone>();

	private Map<String, LocatedDevice> locatedDevices = new HashMap<String, LocatedDevice>();

	private Map<String, GenericDevice> m_devices = new HashMap<String, GenericDevice>();

	private Map<String, Person> persons = new HashMap<String, Person>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	private List<DeviceTypeListener> deviceTypeListeners = new ArrayList<DeviceTypeListener>();

	private List<LocatedDeviceListener> deviceListeners = new ArrayList<LocatedDeviceListener>();

	private List<PersonListener> personListeners = new ArrayList<PersonListener>();

	private List<PersonTypeListener> personTypeListeners = new ArrayList<PersonTypeListener>();

	private List<ZoneListener> zoneListeners = new ArrayList<ZoneListener>();

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
		Zone zone = new ZoneImpl(id, leftX, topY, width, height);
		zones.put(id, zone);

		// Listeners notification
		for (ZoneListener listener : zoneListeners) {
			try {
				listener.zoneAdded(zone);
				zone.addListener(listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return zone;
	}

	public Zone createZone(String id, Position center, int detectionScope) {
		return createZone(id, center.x - detectionScope, center.y - detectionScope, detectionScope * 2,
		      detectionScope * 2);
	}

	@Override
	public void removeZone(String id) {
		Zone zone = zones.remove(id);
		if (zone == null)
			return;

		// Listeners notification
		for (ZoneListener listener : zoneListeners) {
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
		Position newPosition = new Position(leftX, topY);
		zone.setLeftTopRelativePosition(newPosition);
	}

	@Override
	public void resizeZone(String id, int width, int height) throws Exception {
		Zone zone = zones.get(id);
		if (zone == null)
			return;
		zone.resize(width, height);
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
		zone.setVariableValue(variableName, value);
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
		List<Zone> tempList = new ArrayList<Zone>();
		for (Zone zone : zones.values()) {
			if (zone.contains(position)) {
				tempList.add(zone);
			}
		}
		if (tempList.size() > 0) {
			Collections.sort(tempList, new ZoneComparable());
			return tempList.get(0);
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
	public Position getDevicePosition(String deviceId) {
		LocatedDevice device = locatedDevices.get(deviceId);
		if (device != null)
			return device.getCenterAbsolutePosition().clone();
		return null;
	}

	@Override
	public void setDevicePosition(String deviceId, Position position) {

		LocatedDevice device = locatedDevices.get(deviceId);
		if (device != null) {
			List<Zone> oldZones = getObjectZones(device);
			device.setCenterAbsolutePosition(position);
			List<Zone> newZones = getObjectZones(device);

			// When the zones are different, the device is notified
			if (!oldZones.equals(newZones)) {
				// System.out.println("Old zones --> " + oldZones.size());
				// System.out.println("New zones --> " + newZones.size());
				Collections.sort(newZones, new ZoneComparable());
				device.leavingZones(oldZones);
				device.enterInZones(newZones);
			}
		}
	}

	@Override
	public void moveDeviceIntoZone(String deviceId, String zoneId) {
		Position newPosition = getRandomPositionIntoZone(zoneId);
		if (newPosition != null) {
			setDevicePosition(deviceId, newPosition);
		}
	}

	// TODO: Maybe a public method in the interface
	private List<Zone> getObjectZones(LocatedObject object) {
		if (object == null)
			return null;
		List<Zone> allZones = getZones();
		List<Zone> zones = new ArrayList<Zone>();
		for (Zone zone : allZones) {
			if (zone.contains(object))
				zones.add(zone);
		}
		return zones;
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
		GenericDevice device = m_devices.get(deviceId);

		if (device == null && !(device instanceof SimulatedDevice))
			return;

		if (value)
			device.setFault(SimulatedDevice.FAULT_YES);
		else
			device.setFault(SimulatedDevice.FAULT_NO);

	}

	@Override
	public void setDeviceState(String deviceId, boolean value) {
		
		GenericDevice device = m_devices.get(deviceId);

		if (device == null && !(device instanceof SimulatedDevice))
			return;

		if (value)
			device.setState(SimulatedDevice.STATE_ACTIVATED);
		else
			device.setState(SimulatedDevice.STATE_DEACTIVATED);
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
		synchronized (locatedDevices) {
			return locatedDevices.get(deviceId);
		}
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

	@Override
	public Set<String> getDeviceTypes() {
		return Collections.unmodifiableSet(new HashSet<String>(m_factories.keySet()));
	}

	@Bind(id = "devices", aggregate = true, optional = true)
	public void bindDevice(GenericDevice simDev) {
		String sn = simDev.getSerialNumber();
		m_devices.put(sn, simDev);
		if (!locatedDevices.containsKey(sn)) {
			String deviceType = null;
			if (simDev instanceof Pojo) {
				try {
					deviceType = ((Pojo) simDev).getComponentInstance().getFactory().getFactoryName();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			LocatedDevice device = new LocatedDeviceImpl(sn, new Position(-1, -1), simDev, deviceType, this);

			locatedDevices.put(sn, device);

			// Listeners notification
			for (LocatedDeviceListener listener : deviceListeners) {
				try {
					listener.deviceAdded(device);
					device.addListener(listener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// SimulatedDevice listener added
			simDev.addListener((LocatedDeviceImpl) device);
		}
	}

	@Unbind(id = "devices")
	public void unbindDevice(GenericDevice simDev) {
		String sn = simDev.getSerialNumber();
		m_devices.remove(sn);
		LocatedDevice device = locatedDevices.remove(sn);

		// Listeners notification
		for (LocatedDeviceListener listener : deviceListeners) {
			try {
				listener.deviceRemoved(device);
				device.removeListener(listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// SimulatedDevice listener removed
		simDev.removeListener((LocatedDeviceImpl) device);
	}

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.simulator.SimulatedDevice)")
	public void bindFactory(Factory factory) {
		String deviceType = factory.getName();
		m_factories.put(deviceType, factory);

		// Listeners notification
		for (DeviceTypeListener listener : deviceTypeListeners) {
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
		for (DeviceTypeListener listener : deviceTypeListeners) {
			try {
				listener.deviceTypeRemoved(deviceType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addListener(IcasaListener listener) {

		if (listener instanceof ZoneListener) {
			ZoneListener zoneListener = (ZoneListener) listener;
			synchronized (zoneListeners) {
				zoneListeners.add(zoneListener);
				for (Zone zone : zones.values())
					zone.addListener(zoneListener);
			}
		}

		if (listener instanceof LocatedDeviceListener) {
			LocatedDeviceListener deviceListener = (LocatedDeviceListener) listener;
			synchronized (deviceListeners) {
				deviceListeners.add(deviceListener);
				for (LocatedDevice device : locatedDevices.values())
					device.addListener(deviceListener);
			}
		}

		if (listener instanceof PersonListener) {
			PersonListener personListener = (PersonListener) listener;
			synchronized (personListeners) {
				personListeners.add(personListener);
				for (Person person : persons.values())
					person.addListener(personListener);
			}
		}

		if (listener instanceof DeviceTypeListener) {
			DeviceTypeListener deviceTypeListener = (DeviceTypeListener) listener;
			synchronized (deviceTypeListeners) {
				deviceTypeListeners.add(deviceTypeListener);
			}
		}

		if (listener instanceof PersonTypeListener) {
			PersonTypeListener personTypeListener = (PersonTypeListener) listener;
			synchronized (personTypeListeners) {
				personTypeListeners.add(personTypeListener);
			}
		}

	}

	@Override
	public void removeListener(IcasaListener listener) {
		if (listener instanceof ZoneListener) {
			ZoneListener zoneListener = (ZoneListener) listener;
			synchronized (zoneListeners) {
				zoneListeners.remove(zoneListener);
				for (Zone zone : zones.values())
					zone.removeListener(zoneListener);
			}
		}

		if (listener instanceof LocatedDeviceListener) {
			LocatedDeviceListener deviceListener = (LocatedDeviceListener) listener;
			synchronized (deviceListeners) {
				deviceListeners.remove(deviceListener);
				for (LocatedDevice device : locatedDevices.values())
					device.removeListener(deviceListener);
			}
		}

		if (listener instanceof PersonListener) {
			PersonListener personListener = (PersonListener) listener;
			synchronized (personListeners) {
				personListeners.remove(personListener);
				for (Person person : persons.values())
					person.removeListener(personListener);
			}
		}

		if (listener instanceof DeviceTypeListener) {
			DeviceTypeListener deviceTypeListener = (DeviceTypeListener) listener;
			synchronized (deviceTypeListeners) {
				deviceTypeListeners.remove(deviceTypeListener);
			}
		}
	}

	@Override
	public void attachDeviceToPerson(String deviceId, String personId) {
		LocatedDevice device = locatedDevices.get(deviceId);
		Person person = persons.get(personId);

		if (device == null || person == null)
			return;

		person.attachObject(device);
	}

	@Override
	public void detachDeviceFromPerson(String deviceId, String personId) {
		LocatedDevice device = locatedDevices.get(deviceId);
		Person person = persons.get(personId);

		if (device == null || person == null)
			return;

		person.detachObject(device);
	}

	@Override
	public void attachZoneToDevice(String zoneId, String deviceId) {
		Zone zone = zones.get(zoneId);
		LocatedDevice device = locatedDevices.get(deviceId);
		if (zone == null || device == null)
			return;
		device.attachObject(zone);
	}

	@Override
	public void detachZoneFromDevice(String zoneId, String deviceId) {
		Zone zone = zones.get(zoneId);
		LocatedDevice device = locatedDevices.get(deviceId);
		if (zone == null || device == null)
			return;
		device.detachObject(zone);
	}

	@Override
	public void attachDeviceToZone(String deviceId, String zoneId) {
		Zone zone = zones.get(zoneId);
		LocatedDevice device = locatedDevices.get(deviceId);
		if (zone == null || device == null)
			return;

		zone.attachObject(device);

	}

	@Override
	public void detachDeviceFromZone(String deviceId, String zoneId) {
		Zone zone = zones.get(zoneId);
		LocatedDevice device = locatedDevices.get(deviceId);
		if (zone == null || device == null)
			return;

		zone.detachObject(device);
	}

	@Override
	public void attachPersonToZone(String personId, String zoneId) {
		Zone zone = zones.get(zoneId);
		Person person = persons.get(personId);
		if (zone == null || person == null)
			return;

		zone.attachObject(person);
	}

	@Override
	public void detachPersonFromZone(String personId, String zoneId) {
		Zone zone = zones.get(zoneId);
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

	public void saveSimulationState(String fileName) {
		FileWriter outFile;
		PrintWriter out;
		try {
			outFile = new FileWriter("load" + System.getProperty("file.separator") + fileName);
			out = new PrintWriter(outFile);

			out.println("<behavior startdate=\"2011.10.27.00.00.00\" factor=\"1440\">");
			out.println();
			out.println("\t<!-- Zone Section -->");
			out.println();

			for (Zone zone : getZones()) {
				String id = zone.getId();
				int leftX = zone.getLeftTopAbsolutePosition().x;
				int topY = zone.getLeftTopAbsolutePosition().y;
				int width = zone.getWidth();
				int height = zone.getHeight();
				out.println("\t<create-zone id=\"" + id + "\" leftX=\"" + leftX + "\" topY=\"" + topY + "\" width=\""
				      + width + "\" height=\"" + height + "\" />");
				out.println();

				for (String variable : zone.getVariableNames()) {
					Object value = zone.getVariableValue(variable);

					out.println("\t<add-zone-variable zoneId=\"" + id + "\" variable=\"" + variable + "\" />");
					out.println("\t<modify-zone-variable zoneId=\"" + id + "\" variable=\"" + variable + "\" value=\""
					      + value + "\" />");
				}
				out.println();
			}

			out.println("\t<!-- Device Section -->");
			out.println();

			for (LocatedDevice device : getDevices()) {
				String id = device.getSerialNumber();
				String type = device.getType();

				out.println("\t<create-device id=\"" + id + "\" type=\"" + type + "\" />");

				String location = (String) device.getPropertyValue(SimulationManager.LOCATION_PROP_NAME);
				if (location != null)
					out.println("\t<move-device-zone deviceId=\"" + id + "\" zoneId=\"" + location + "\" />");

				out.println();

			}

			out.println();
			out.println("\t<!-- Person Section -->");
			out.println();

			for (Person person : getPersons()) {
				String id = person.getName();
				String type = person.getPersonType();

				out.println("\t<create-person id=\"" + id + "\" type=\"" + type + "\" />");

				Zone zone = getZoneFromPosition(person.getCenterAbsolutePosition());

				if (zone != null)
					out.println("\t<move-person-zone personId=\"" + id + "\" zoneId=\"" + zone.getId() + "\" />");

				out.println();

			}

			out.println("</behavior>");

			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
