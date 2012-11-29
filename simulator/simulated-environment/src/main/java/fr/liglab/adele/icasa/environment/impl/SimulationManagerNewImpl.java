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
import fr.liglab.adele.icasa.environment.Device;
import fr.liglab.adele.icasa.environment.Person;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulationEnvironmentListener;
import fr.liglab.adele.icasa.environment.SimulationManagerNew;
import fr.liglab.adele.icasa.environment.Zone;

@Component(name="SimulationManagerNew")
@Provides
@Instantiate(name="SimulationManagerNew-0")
public class SimulationManagerNewImpl implements SimulationManagerNew {

	private Map<String, Zone> zones = new HashMap<String, Zone>();

	private Map<String, Device> devices = new HashMap<String, Device>();

	private Map<String, SimulatedDevice> m_simulatedDevices = new HashMap<String, SimulatedDevice>();

	private Map<String, Person> persons = new HashMap<String, Person>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();
	
	private List<SimulationEnvironmentListener> listeners = new ArrayList<SimulationEnvironmentListener>();

	@Override
	public void createZone(String id, String description, int leftX, int topY, int width, int height) {
		Zone zone = new ZoneImpl(leftX, topY, width, height);
		zones.put(id, zone);
		
		for (SimulationEnvironmentListener listener : listeners) {
	      listener.zoneAdded(zone);
      }		
	}
	
	@Override
	public void removeZone(String id) {
		Zone zone = zones.remove(id);
		if (zone!=null) {
			for (SimulationEnvironmentListener listener : listeners) {
		      listener.zoneRemoved(zone);
	      }	
		}
	}
	
	@Override
	public void moveZone(String id, int leftX, int topY) {
		Zone zone = zones.get(id);
		if (zone!=null) {
			Position position = new Position(leftX, topY);
			zone.setLeftTopPosition(position);
			
			for (SimulationEnvironmentListener listener : listeners) {
		      listener.zoneMoved(zone);
	      }	
		}
	}
	
	@Override
	public void resizeZone(String id, int width, int height) {
		Zone zone = zones.get(id);
		if (zone!=null) {			
			zone.setHeight(height);
			zone.setWidth(width);			
			for (SimulationEnvironmentListener listener : listeners) {
		      listener.zoneResized(zone);
	      }	
		}
	}
	
	@Override
	public Set<String> getZoneVariables(String zoneId) {
		Zone zone = zones.get(zoneId);
		return zone.getVariableList();
	}

	@Override
	public Double getZoneVariableValue(String zoneId, String variable) {
		Zone zone = zones.get(zoneId);		
		return zone.getVariableValue(variable);
	}

	@Override
	public void setZoneVariable(String zoneId, String variable, Double value) {
		Zone zone = zones.get(zoneId);
		if (zone!=null) {
			Double oldValue = zone.getVariableValue(variable);
			zone.setVariableValue(variable, value);
			
			for (SimulationEnvironmentListener listener : listeners) {
		      listener.zoneVariableModified(zone, variable, oldValue, value);
	      }	
		}

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

	/*
	@Bind(id = "devices", aggregate = true, optional = true)
	public synchronized void bindDevice(SimulatedDevice dev) {
		m_simulatedDevices.put(dev.getSerialNumber(), dev);
	}

	@Unbind(id = "devices")
	public synchronized void unbindDevice(SimulatedDevice dev) {
		m_simulatedDevices.remove(dev.getSerialNumber());
	}
	
	*/
	
	
	@Bind(id = "sim-devices", aggregate = true, optional = true)
	public void bindDevice(SimulatedDevice dev) {
		/*
		System.out.println("+--------");
		String[] props = reference.getPropertyKeys();
				for (String prop : props) {
	      System.out.println("\t +------ Property " + prop);
      }
		*/

		System.out.println("+--------  Device " + dev.getSerialNumber());
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
   public void addListener(SimulationEnvironmentListener listener) {
		listeners.add(listener);
   }

	@Override
   public void removeListener(SimulationEnvironmentListener listener) {
		listeners.remove(listener);
   }
	
}
