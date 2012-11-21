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
import fr.liglab.adele.icasa.environment.Device;
import fr.liglab.adele.icasa.environment.Person;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulationManagerNew;
import fr.liglab.adele.icasa.environment.Zone;

@Component
@Provides
@Instantiate
public class SimulationManagerNewImpl implements SimulationManagerNew {

	private Map<String, Zone> zones = new HashMap<String, Zone>();

	private Map<String, Device> devices = new HashMap<String, Device>();

	private Map<String, SimulatedDevice> m_simulatedDevices = new HashMap<String, SimulatedDevice>();

	private Map<String, Person> persons = new HashMap<String, Person>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	@Override
	public void createZone(String id, String description, int leftX, int topY, int width, int height) {
		Zone zone = new ZoneImpl(leftX, topY, width, height);
		System.out.println("Zone created " + zone);
		zones.put(id, zone);
	}

	@Override
	public List<Zone> getZones() {
		return new ArrayList<Zone>(zones.values());
	}

	@Override
	public List<Device> getDevices() {
		return new ArrayList<Device>(devices.values());
	}

	@Override
	public Zone getZone(String zoneId) {
		return zones.get(zoneId);
	}

	@Override
	public Position getDevicePosition(String deviceSerialNumber) {
		Device device = devices.get(deviceSerialNumber);
		if (device != null)
			return device.getPosition().clone();
		return null;
	}

	@Override
	public void setDevicePosition(String deviceSerialNumber, Position position) {
		Device device = devices.get(deviceSerialNumber);
		if (device != null)
			device.setPosition(position);
	}

	@Override
	public void setDeviceZone(String deviceSerialNumber, String zoneId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPersonPosition(String userName, Position position) {
		Person person = persons.get(userName);
		if (person != null)
			person.setPosition(position);
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
	}

	@Override
	public void removePerson(String userName) {
		persons.remove(userName);
	}

	@Override
	public List<Person> getPersons() {
		return new ArrayList<Person>(persons.values());
	}

	@Override
	public Set<String> getEnvironmentVariables(String zoneId) {
		Zone zone = zones.get(zoneId);
		return zone.getVariableList();
	}

	@Override
	public Double getVariableValue(String zoneId, String variable) {
		Zone zone = zones.get(zoneId);
		return zone.getVariableValue(variable);
	}

	@Override
	public void setEnvironmentVariable(String zoneId, String variable, Double value) {
		Zone zone = zones.get(zoneId);
		zone.setVariableValue(variable, value);
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
	public void createDevice(String factoryName, String deviceId, String description) {
		Factory factory = m_factories.get(factoryName);
		if (factory != null) {
			// Create the device
			Dictionary<String, String> properties = new Hashtable<String, String>();
			properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);
			properties.put(GenericDevice.STATE_PROPERTY_NAME, GenericDevice.STATE_ACTIVATED);
			properties.put(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_NO);
			if (description != null)
				properties.put(Constants.SERVICE_DESCRIPTION, description);
			properties.put("instance.name", factoryName + "-" + deviceId);
			try {
				factory.createComponentInstance(properties);
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
	public Set<String> getDeviceFactories() {
		return Collections.unmodifiableSet(new HashSet<String>(m_factories.keySet()));		
	}

	@Bind(id = "devices", aggregate = true, optional = true)
	public synchronized void bindDevice(SimulatedDevice dev) {
		m_simulatedDevices.put(dev.getSerialNumber(), dev);
	}

	@Unbind(id = "devices")
	public synchronized void unbindDevice(SimulatedDevice dev) {
		m_simulatedDevices.remove(dev.getSerialNumber());
	}

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.environment.SimulatedDevice)")
	public synchronized void bindFactory(Factory factory) {
		m_factories.put(factory.getName(), factory);
	}

	@Unbind(id = "factories")
	public synchronized void unbindFactory(Factory factory) {
		m_factories.remove(factory.getName());
	}

}
