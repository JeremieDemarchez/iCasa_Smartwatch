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

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.LocatedDevice;
import fr.liglab.adele.icasa.environment.LocatedObject;
import fr.liglab.adele.icasa.environment.Person;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.Zone;
import fr.liglab.adele.icasa.environment.impl.util.ZoneComparable;
import fr.liglab.adele.icasa.environment.listener.DeviceTypeListener;
import fr.liglab.adele.icasa.environment.listener.IcasaListener;
import fr.liglab.adele.icasa.environment.listener.LocatedDeviceListener;
import fr.liglab.adele.icasa.environment.listener.PersonListener;
import fr.liglab.adele.icasa.environment.listener.ZoneListener;

@Component
@Provides
@Instantiate(name = "SimulationManager-1")
public class SimulationManagerImpl implements SimulationManager {

	private Map<String, Zone> zones = new HashMap<String, Zone>();

	private Map<String, LocatedDevice> locatedDevices = new HashMap<String, LocatedDevice>();

	private Map<String, SimulatedDevice> m_simulatedDevices = new HashMap<String, SimulatedDevice>();

	private Map<String, Person> persons = new HashMap<String, Person>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	private List<DeviceTypeListener> deviceTypeListeners = new ArrayList<DeviceTypeListener>();
	
	private List<LocatedDeviceListener> deviceListeners = new ArrayList<LocatedDeviceListener>();
	
	private List<PersonListener> personListeners = new ArrayList<PersonListener>();
	
	private List<ZoneListener> zoneListeners = new ArrayList<ZoneListener>();

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
			return device.getAbsoluteCenterPosition().clone();
		return null;
	}

	@Override
	public void setDevicePosition(String deviceId, Position position) {

		LocatedDevice device = locatedDevices.get(deviceId);
		if (device != null) {
			List<Zone> oldZones = getObjectZones(device);
			device.setAbsoluteCenterPosition(position);
			List<Zone> newZones = getObjectZones(device);
			
			// When the zones are different, the device is notified
			if (!oldZones.equals(newZones)) {
				device.leavingZones(oldZones);										
				Collections.sort(newZones, new ZoneComparable());
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
			person.setAbsoluteCenterPosition(position);
	}

	@Override
	public void setPersonZone(String userName, String zoneId) {
		Person person = getPerson(userName);
		if (person != null) {
			Position newPosition = getRandomPositionIntoZone(zoneId);
			if (newPosition != null)
				person.setAbsoluteCenterPosition(newPosition);
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
	public void addPerson(String userName) {
		Person person = new PersonImpl(userName, new Position(-1, -1), this);
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
			for (LocatedDeviceListener listener : deviceListeners) {
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
		for (LocatedDeviceListener listener : deviceListeners) {
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
	   
	   if (device==null || person==null)
	   	return;
	   
	   person.attachObject(device);	  
	}

	@Override
	public void detachDeviceFromPerson(String deviceId,  String personId) {
	   LocatedDevice device = locatedDevices.get(deviceId);
	   Person person = persons.get(personId);
	   
	   if (device==null || person==null)
	   	return;
	   
	   person.detachObject(device);	 
	}

	
	@Override
   public void attachZoneToDevice(String zoneId, String deviceId) {
	   Zone zone = zones.get(zoneId);
	   LocatedDevice device = locatedDevices.get(deviceId);
	   if (zone==null || device==null)
	   	return;
	   device.attachObject(zone);
   }

	@Override
   public void detachZoneFromDevice(String zoneId, String deviceId) {
	   Zone zone = zones.get(zoneId);
	   LocatedDevice device = locatedDevices.get(deviceId);
	   if (zone==null || device==null)
	   	return;
	   device.detachObject(zone);
   }
	
	
	
	@Override
   public void attachDeviceToZone(String deviceId, String zoneId) {
	   Zone zone = zones.get(zoneId);
	   LocatedDevice device = locatedDevices.get(deviceId);
	   if (zone==null || device==null)
	   	return;
	   
	   zone.attachObject(device);
	   
   }

	@Override
   public void detachDeviceFromZone(String deviceId, String zoneId) {
	   Zone zone = zones.get(zoneId);
	   LocatedDevice device = locatedDevices.get(deviceId);
	   if (zone==null || device==null)
	   	return;
	   
	   zone.detachObject(device);	   
   }

	@Override
   public void attachPersonToZone(String personId, String zoneId) {
	   Zone zone = zones.get(zoneId);
	   Person person = persons.get(personId);
	   if (zone==null || person==null)
	   	return;
	   
	   zone.attachObject(person);	 	   
   }

	@Override
   public void detachPersonFromZone(String personId, String zoneId) {
	   Zone zone = zones.get(zoneId);
	   Person person = persons.get(personId);
	   if (zone==null || person==null)
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
		int minX = zone.getAbsoluteCenterPosition().x;
		int minY = zone.getAbsoluteCenterPosition().y;
		int newX = random(minX, minX + zone.getWidth());
		int newY = random(minY, minY + zone.getHeight());
		return new Position(newX, newY);
	}








}
