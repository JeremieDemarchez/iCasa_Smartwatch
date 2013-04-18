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
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    /*
     * WARNING : UPDATE following filter if you change the componant instance name !!!
     */
	@Requires(filter = "(!(instance.name=SimulationManager-1))")
	private ContextManager manager;

	private Map<String, Person> persons = new HashMap<String, Person>();

	private Map<String, SimulatedDevice> simulatedDeviceMap = new HashMap<String, SimulatedDevice>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	private List<PersonListener> personListeners = new ArrayList<PersonListener>();

	private List<PersonTypeListener> personTypeListeners = new ArrayList<PersonTypeListener>();

	private List<String> personTypes = new ArrayList<String>();

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
		return manager.createZone(id, leftX, topY, width, height);
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
		Person person = getPerson(userName);
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
		List<Person> tempPersons = getPersons();
		for (Person person : tempPersons) {
			removePerson(person.getName());
		}
	}

	@Override
	public void addPerson(String userName, String personType) {
		String aPersonType = getPersonType(personType);
		if (aPersonType == null)
			return;

		Person person = new PersonImpl(userName, new Position(-1, -1), aPersonType, this);
        List<PersonListener> snapshotListener;
        lock.writeLock().lock();
        try {
            snapshotListener = getPersonListeners();
            persons.put(userName, person);
        }finally {
            lock.writeLock().unlock();
        }

		// Listeners notification
		for (PersonListener listener : snapshotListener) {
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
        Person person;
        List<PersonListener> snapshotListener;
        lock.writeLock().lock();
        try{
		    person = persons.remove(userName);
            snapshotListener = getPersonListeners();
		    if (person == null)
			    return;
        }finally {
            lock.writeLock().unlock();
        }

		// Listeners notification
		for (PersonListener listener : snapshotListener) {
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
		lock.readLock().lock();
        try {
			return Collections.unmodifiableList(new ArrayList<Person>(persons.values()));
		}finally {
            lock.readLock().unlock();
        }
	}

	@Override
	public Person getPerson(String personName) {
        lock.readLock().lock();
		try {
			return persons.get(personName);
		} finally {
            lock.readLock().unlock();
        }
	}

	@Override
	public void setDeviceFault(String deviceId, boolean value) {
		LocatedDevice device = getDevice(deviceId);

		if (value) {
			device.setPropertyValue(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_YES);
		} else {
			device.setPropertyValue(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_YES);
		}

	}

	@Override
	public void setDeviceState(String deviceId, boolean value) {
		manager.setDeviceState(deviceId, value);
	}

	@Override
	public LocatedDevice createDevice(String factoryName, String deviceId, Map<String, Object> properties) {
		Factory factory = getFactory(factoryName);
        LocatedDevice device =  null;
        int count = 0;
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
        while (count++ < 500 && (device == null)){
            device = getDevice(deviceId);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
            device = getDevice(deviceId);
        }
        if (device == null){
            throw new IllegalStateException("Unable to obtain device (" + deviceId + ") after 500 tries ");
        }

        return device;
	}

	@Override
	public LocatedDevice getDevice(String deviceId) {
		return manager.getDevice(deviceId);
	}

	@Override
	public void removeDevice(String deviceId) {
		GenericDevice device = getSimulatedDevice(deviceId);

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
        lock.writeLock().lock();
        try{
		    simulatedDeviceMap.put(sn, simDev);
        }finally {
            lock.writeLock().unlock();
        }
	}

	@Unbind(id = "devices")
	public void unbindDevice(SimulatedDevice simDev) {
		String sn = simDev.getSerialNumber();
        lock.writeLock().lock();
        try{
		    simulatedDeviceMap.remove(sn);
        }finally {
            lock.writeLock().unlock();
        }
	}

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.simulator.SimulatedDevice)")
	public void bindFactory(Factory factory) {
		String deviceType = factory.getName();
        lock.writeLock().lock();
        try{
		    m_factories.put(deviceType, factory);
        }finally {
            lock.writeLock().unlock();
        }

	}

	@Unbind(id = "factories")
	public void unbindFactory(Factory factory) {
		String deviceType = factory.getName();
        lock.writeLock().lock();
        try{
		    m_factories.remove(deviceType);
        }finally {
            lock.writeLock().unlock();
        }

	}

	@Override
	public void addListener(IcasaListener listener) {

		if (listener instanceof PersonListener) {
			PersonListener personListener = (PersonListener) listener;
            List<Person> snapshotPersons;
            lock.writeLock().lock();
            try{
			    personListeners.add(personListener);
                snapshotPersons = getPersons();
            }finally {
                lock.writeLock().unlock();
            }
			for (Person person : snapshotPersons){
				person.addListener(personListener);
            }
		}

		if (listener instanceof PersonTypeListener) {
			PersonTypeListener personTypeListener = (PersonTypeListener) listener;
            lock.writeLock().lock();
    		personTypeListeners.add(personTypeListener);
            lock.writeLock().unlock();
		}
		manager.addListener(listener);

	}

	@Override
	public void removeListener(IcasaListener listener) {

		if (listener instanceof PersonListener) {
			PersonListener personListener = (PersonListener) listener;
            List<Person> snapshotPersons;
            lock.writeLock().lock();
			try {
                snapshotPersons = getPersons();
				personListeners.remove(personListener);
            }finally {
                lock.writeLock().unlock();
            }
		    for (Person person : snapshotPersons){
				person.removeListener(personListener);
            }

		}

		if (listener instanceof PersonTypeListener) {
			PersonTypeListener personTypeListener = (PersonTypeListener) listener;
            lock.writeLock().lock();
			try {
				personTypeListeners.remove(personTypeListener);
			}finally {
                lock.writeLock().unlock();
            }
		}
		manager.removeListener(listener);
	}

	@Override
	public void attachDeviceToPerson(String deviceId, String personId) {
		LocatedDevice device = getDevice(deviceId);
		Person person = getPerson(personId);

		if (device == null || person == null)
			return;

		person.attachObject(device);
	}

	@Override
	public void detachDeviceFromPerson(String deviceId, String personId) {
		LocatedDevice device = getDevice(deviceId);
		Person person = getPerson(personId);

		if (device == null || person == null)
			return;

		person.detachObject(device);
	}

	@Override
	public void attachZoneToDevice(String zoneId, String deviceId) {
		Zone zone = getZone(zoneId);
		LocatedDevice device = getDevice(deviceId);
		if (zone == null || device == null)
			return;
		device.attachObject(zone);
	}

	@Override
	public void detachZoneFromDevice(String zoneId, String deviceId) {
		Zone zone = getZone(zoneId);
		LocatedDevice device = getDevice(deviceId);
		if (zone == null || device == null)
			return;
		device.detachObject(zone);
	}

	@Override
	public void attachDeviceToZone(String deviceId, String zoneId) {
		Zone zone = getZone(zoneId);
		LocatedDevice device = getDevice(deviceId);
		if (zone == null || device == null)
			return;

		zone.attachObject(device);

	}

	@Override
	public void detachDeviceFromZone(String deviceId, String zoneId) {
		Zone zone = getZone(zoneId);
		LocatedDevice device = getDevice(deviceId);
		if (zone == null || device == null)
			return;
		zone.detachObject(device);
	}

	@Override
	public void attachPersonToZone(String personId, String zoneId) {
		Zone zone = getZone(zoneId);
		Person person = getPerson(personId) ;
		if (zone == null || person == null)
			return;

		zone.attachObject(person);
	}

	@Override
	public void detachPersonFromZone(String personId, String zoneId) {
		Zone zone = getZone(zoneId);
		Person person = getPerson(personId);
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
        boolean existsPerson = false;
        List<PersonTypeListener> listenerSnapshot;
        lock.writeLock().lock();
        try{
            existsPerson = personTypes.contains(personType);
            listenerSnapshot = getPersonTypeListeners();
            if (!existsPerson) {
                personTypes.add(personType);
            }
        }finally {
            lock.writeLock().unlock();
        }
        if (!existsPerson){
			for (PersonTypeListener listener : listenerSnapshot){
				listener.personTypeAdded(personType);
	    	}
        }
	}

	@Override
	public String getPersonType(String personType) {
        lock.readLock().lock();
        try{
            if (personTypes.contains(personType)) {
                return personType;
            }
            return null;
        }finally {
            lock.readLock().unlock();
        }
	}

	@Override
	public void removePersonType(String personType) {
		if (personType.contains(personType)) {
            lock.writeLock().lock();
            List<PersonTypeListener> listenerSnapshot = getPersonTypeListeners();
            try{
			    personTypes.remove(personType);
            }finally {
                lock.writeLock().unlock();
            }
			for (PersonTypeListener listener : listenerSnapshot)
				listener.personTypeRemoved(personType);
		}
	}

	@Override
	public List<String> getPersonTypes() {
        lock.readLock().lock();
        try{
		    return Collections.unmodifiableList(personTypes);
        }finally {
            lock.readLock().unlock();
        }
	}

	@Override
	public Set<String> getDeviceTypes() {
		return manager.getDeviceTypes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.icasa.simulator.SimulationManager#getSimulatedDeviceTypes
	 * ()
	 */
	@Override
	public Set<String> getSimulatedDeviceTypes() {
        lock.readLock().lock();
        try{
		    return Collections.unmodifiableSet(new HashSet<String>(m_factories.keySet()));
        }finally {
            lock.readLock().unlock();
        }
	}

	@Override
	public void removeAllZones() {
		manager.removeAllZones();
	}

	@Override
	public void removeAllDevices() {
		List<SimulatedDevice> tempDevices;
		lock.readLock().lock();
        try{
			tempDevices = Collections.unmodifiableList(new ArrayList<SimulatedDevice>(simulatedDeviceMap.values()));
		}finally {
            lock.readLock().unlock();
        }

		for (SimulatedDevice simulatedDevice : tempDevices) {
			removeDevice(simulatedDevice.getSerialNumber());
		}
	}

	@Override
	public void resetContext() {
		manager.resetContext();
		removeAllPersons();
		removeAllDevices();
	}

    private List<PersonListener> getPersonListeners() {
        lock.readLock().lock();
        try{
            return new ArrayList<PersonListener>(personListeners);
        }finally {
            lock.readLock().unlock();
        }
    }

    private List<PersonTypeListener> getPersonTypeListeners() {
        lock.readLock().lock();
        try{
            return new ArrayList<PersonTypeListener>(personTypeListeners);
        } finally {
            lock.readLock().unlock();
        }
    }

    private Factory getFactory(String name){
      lock.readLock().lock();
        try{
            return m_factories.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    private SimulatedDevice getSimulatedDevice(String name){
        lock.readLock().lock();
        try{
            return simulatedDeviceMap.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }
}
