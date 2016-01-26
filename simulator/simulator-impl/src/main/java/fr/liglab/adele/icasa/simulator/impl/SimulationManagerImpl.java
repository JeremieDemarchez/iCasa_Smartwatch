/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.impl;

import fr.liglab.adele.icasa.clockservice.Clock;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.service.location.PersonLocationService;
import fr.liglab.adele.icasa.simulator.*;
import fr.liglab.adele.icasa.simulator.listener.PersonListener;
import fr.liglab.adele.icasa.simulator.listener.PersonTypeListener;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.Pojo;
import org.apache.felix.ipojo.annotations.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Provides
@Instantiate(name = "SimulationManager-0")
public class SimulationManagerImpl implements SimulationManager, PersonLocationService {


	@Requires(optional = true)
	private PhysicalModel[] _physicalModels;

    @Requires
    private Clock clock;

	private Map<String, Person> persons = new HashMap<String, Person>();

	private Map<String, SimulatedDevice> simulatedDeviceMap = new HashMap<String, SimulatedDevice>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	private List<PersonListener> personListeners = new ArrayList<PersonListener>();

	private List<PersonTypeListener> personTypeListeners = new ArrayList<PersonTypeListener>();

	private Map<String, PersonType> personTypes = new HashMap<String, PersonType>();

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public SimulationManagerImpl() {
        addPersonType("Old man");
        addPersonType("Old woman");
        addPersonType("Woman");
        addPersonType("Man");
		addPersonType("Grandfather");
		addPersonType("Grandmother");
		addPersonType("Father");
		addPersonType("Mother");
		addPersonType("Boy");
		addPersonType("Girl");
	}


	@Override
	public void setPersonPosition(String userName, Position position) {
		Person person = getPerson(userName);
		if (person != null)
			person.setCenterAbsolutePosition(position);
	}

	@Override
	public void setPersonZone(String userName, String zoneId) {
	/**	Person person = getPerson(userName);
		if (person != null) {
			Position newPosition = getRandomPositionIntoZone(zoneId);
			if (newPosition != null)
				person.setCenterAbsolutePosition(newPosition);
		}**/
	}

	@Override
	public void removeAllPersons() {
		List<Person> tempPersons = getPersons();
		for (Person person : tempPersons) {
			removePerson(person.getName());
		}
	}

	@Override
	public Person addPerson(String userName, String personType) {
				
		if (userName==null || personType==null) {		
			System.out.println("Arguments (userName, personType) are null");
			throw new IllegalArgumentException("Arguments (userName, personType) are null");
		}
		if (userName.trim().equals("") || personType.trim().equals("")) {	
			System.out.println("Arguments (userName, personType) are empty");
			throw new IllegalArgumentException("Arguments (userName, personType) are empty");
		}
		
		if (getPerson(userName) != null) {
			throw new IllegalArgumentException("Person " + userName + " already exists");
		}
		PersonType aPersonType = getPersonType(personType);
		if (aPersonType == null) {
			throw new IllegalArgumentException("Person type " + personType + " is not defined");
		}
		Person person = new PersonImpl(userName, new Position(-1, -1), aPersonType, this);
		List<PersonListener> snapshotListener;
		lock.writeLock().lock();
		try {
			snapshotListener = getPersonListeners();
			persons.put(userName, person);
		} finally {
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

		return person;
	}

	@Override
	public void removePerson(String userName) {
		Person person;
		List<PersonListener> snapshotListener;
		lock.writeLock().lock();
		try {
			person = persons.remove(userName);
			snapshotListener = getPersonListeners();
			if (person == null)
				return;
		} finally {
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
		} finally {
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
	/**	LocatedDevice device = getDevice(deviceId);
		if (device == null) {
			throw new NullPointerException("Device do not exist: " + deviceId);
		}
		if (value) {
			device.setPropertyValue(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_YES);
		} else {
			device.setPropertyValue(GenericDevice.FAULT_PROPERTY_NAME, GenericDevice.FAULT_NO);
		}**/

	}


	@Override
	public LocatedDevice createDevice(String factoryName, String deviceId, Map<String, Object> properties) {
		/**if (getDevice(deviceId) != null) {
			throw new IllegalArgumentException("Device " + deviceId + " already exists");
		}
		Factory factory = getFactory(factoryName);
		if (factory == null) {
			throw new IllegalStateException("Unknown device type: " + factoryName);
		}**/

		LocatedDevice device = null;
/**
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

		int count = 0;
		while (count++ < 500 && (device == null)) {
			device = getDevice(deviceId);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
			device = getDevice(deviceId);
		}
		if (device == null) {
			throw new IllegalStateException("Unable to obtain device (" + deviceId + ") after 500 tries ");
		}
		 **/
		return device;
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
		try {
			simulatedDeviceMap.put(sn, simDev);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Unbind(id = "devices")
	public void unbindDevice(SimulatedDevice simDev) {
		String sn = simDev.getSerialNumber();
		lock.writeLock().lock();
		try {
			simulatedDeviceMap.remove(sn);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.simulator.SimulatedDevice)")
	public void bindFactory(Factory factory) {
		String deviceType = factory.getName();
		lock.writeLock().lock();
		try {
			m_factories.put(deviceType, factory);
		} finally {
			lock.writeLock().unlock();
		}

	}

	@Unbind(id = "factories")
	public void unbindFactory(Factory factory) {
		String deviceType = factory.getName();
		lock.writeLock().lock();
		try {
			m_factories.remove(deviceType);
		} finally {
			lock.writeLock().unlock();
		}

	}


	@Override
	public void attachDeviceToPerson(String deviceId, String personId) {
	/**	LocatedDevice device = getDevice(deviceId);
		Person person = getPerson(personId);

		if (device == null || person == null)
			return;

		person.attachObject(device);**/
	}

	@Override
	public void detachDeviceFromPerson(String deviceId, String personId) {
	/**	LocatedDevice device = getDevice(deviceId);
		Person person = getPerson(personId);

		if (device == null || person == null)
			return;

		person.detachObject(device);**/
	}

	@Override
	public void attachZoneToDevice(String zoneId, String deviceId) {
	/**	Zone zone = getZone(zoneId);
		LocatedDevice device = getDevice(deviceId);
		if (zone == null || device == null)
			return;
		device.attachObject(zone);**/
	}

	@Override
	public void detachZoneFromDevice(String zoneId, String deviceId) {
/**		Zone zone = getZone(zoneId);
		LocatedDevice device = getDevice(deviceId);
		if (zone == null || device == null)
			return;
		device.detachObject(zone);**/
	}

	@Override
	public void attachDeviceToZone(String deviceId, String zoneId) {
	/**	Zone zone = getZone(zoneId);
		LocatedDevice device = getDevice(deviceId);
		if (zone == null || device == null)
			return;

		zone.attachObject(device);**/

	}

	@Override
	public void detachDeviceFromZone(String deviceId, String zoneId) {
	/**	Zone zone = getZone(zoneId);
		LocatedDevice device = getDevice(deviceId);
		if (zone == null || device == null)
			return;
		zone.detachObject(device);**/
	}

	@Override
	public void attachPersonToZone(String personId, String zoneId) {
/**		Zone zone = getZone(zoneId);
		Person person = getPerson(personId);
		if (zone == null || person == null)
			return;

		zone.attachObject(person);**/
	}

	@Override
	public void detachPersonFromZone(String personId, String zoneId) {
	/**	Zone zone = getZone(zoneId);
		Person person = getPerson(personId);
		if (zone == null || person == null)
			return;

		zone.detachObject(person);**/
	}

/**	private int random(int min, int max) {
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
		int newX = random(minX, minX + zone.getXLength());
		int newY = random(minY, minY + zone.getYLength());
		return new Position(newX, newY);
	}**/

	@Override
	public PersonType addPersonType(String personType) {
		boolean existsPerson = false;
		List<PersonTypeListener> listenerSnapshot;
		PersonType type = new PersonType(personType);
		lock.writeLock().lock();
		try {
			existsPerson = personTypes.containsKey(type);
			listenerSnapshot = getPersonTypeListeners();
			if (!existsPerson) {
				personTypes.put(personType, type);
			}
		} finally {
			lock.writeLock().unlock();
		}
		if (!existsPerson) {
			for (PersonTypeListener listener : listenerSnapshot) {
				listener.personTypeAdded(type);
			}
		}
		return type;
	}

	@Override
	public PersonType getPersonType(String personType) {
		lock.readLock().lock();
		try {
			if (personTypes.containsKey(personType)) {
				return personTypes.get(personType);
			}
			return null;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
   public Set<Person> getPersonsIntoZone(Zone zone) { 
		Set<Person> contained = new HashSet<Person>();
	/**	List<Person> tempPersons = null;
		lock.readLock().lock();
		try {
			tempPersons = Collections.unmodifiableList(new ArrayList<Person>(persons.values()));
		} finally {
			lock.readLock().unlock();
		}		
		
		for (Person person : tempPersons) {
         if (zone.contains(person)) {
         	contained.add(person);
         }
      }**/
	   return contained;
   }
	
	@Override
   public Set<String> getPersonInZone(String zoneId) {
	   Set<String> personsId = new HashSet<String>();
	   
	/**   Zone zone = getZone(zoneId);
	   Set<Person> persons = getPersonsIntoZone(zone);
	   for (Person person : persons) {
	      personsId.add(person.getName());
      }**/
	   return personsId;
   }

	@Override
	public void removePersonType(String personType) {
		if (personType.contains(personType)) {
			lock.writeLock().lock();
			PersonType removed;
			List<Person> localPersons = getPersons();
			List<PersonTypeListener> listenerSnapshot = getPersonTypeListeners();
			try {
				removed = personTypes.remove(personType);

			} finally {
				lock.writeLock().unlock();
			}
			if (removed == null) {
				return;
			}
			removePersons(localPersons, removed);// remove the persons that are of the removed type.
			for (PersonTypeListener listener : listenerSnapshot) {
				listener.personTypeRemoved(removed);
			}
		}
	}

	private void removePersons(List<Person> localPersons, PersonType type) {
		for (Person person : localPersons) {
			if (person.getPersonType().equals(type)) {
				removePerson(person.getName());
			}
		}
	}

	@Override
	public List<PersonType> getPersonTypes() {
		lock.readLock().lock();
		try {
			return Collections.unmodifiableList(new ArrayList<PersonType>(personTypes.values()));
		} finally {
			lock.readLock().unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.simulator.SimulationManager#getSimulatedDeviceTypes ()
	 */
	@Override
	public Set<String> getSimulatedDeviceTypes() {
		lock.readLock().lock();
		try {
			return Collections.unmodifiableSet(new HashSet<String>(m_factories.keySet()));
		} finally {
			lock.readLock().unlock();
		}
	}


	@Override
	public void removeAllDevices() {
		List<SimulatedDevice> tempDevices;
		lock.readLock().lock();
		try {
			tempDevices = Collections.unmodifiableList(new ArrayList<SimulatedDevice>(simulatedDeviceMap.values()));
		} finally {
			lock.readLock().unlock();
		}

		for (SimulatedDevice simulatedDevice : tempDevices) {
			removeDevice(simulatedDevice.getSerialNumber());
		}
	}


	private List<PersonListener> getPersonListeners() {
		lock.readLock().lock();
		try {
			return new ArrayList<PersonListener>(personListeners);
		} finally {
			lock.readLock().unlock();
		}
	}

	private List<PersonTypeListener> getPersonTypeListeners() {
		lock.readLock().lock();
		try {
			return new ArrayList<PersonTypeListener>(personTypeListeners);
		} finally {
			lock.readLock().unlock();
		}
	}

	private Factory getFactory(String name) {
		lock.readLock().lock();
		try {
			return m_factories.get(name);
		} finally {
			lock.readLock().unlock();
		}
	}

	private SimulatedDevice getSimulatedDevice(String name) {
		lock.readLock().lock();
		try {
			return simulatedDeviceMap.get(name);
		} finally {
			lock.readLock().unlock();
		}
	}




}
