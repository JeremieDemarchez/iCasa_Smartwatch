/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa.context.impl;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import fr.liglab.adele.icasa.TechnicalService;
import fr.liglab.adele.icasa.Variable;
import fr.liglab.adele.icasa.device.DeviceListener;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.Pojo;
import org.apache.felix.ipojo.annotations.*;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.DeviceTypeListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.listener.IcasaListener;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.LocatedDeviceListener;
import fr.liglab.adele.icasa.location.LocatedObject;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.ZoneListener;
import fr.liglab.adele.icasa.location.impl.LocatedDeviceImpl;
import fr.liglab.adele.icasa.location.impl.ZoneImpl;

@Component
@Provides
@Instantiate(name = "ContextManager-1")
public class ContextManagerImpl implements ContextManager {

	@Requires(optional = true)
	private TechnicalService[] _technicalServices;

	private Map<String, Zone> zones = new HashMap<String, Zone>();

	private Map<String, LocatedDevice> locatedDevices = new HashMap<String, LocatedDevice>();

	private Map<String, GenericDevice> m_devices = new HashMap<String, GenericDevice>();

	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	private List<DeviceTypeListener> deviceTypeListeners = new ArrayList<DeviceTypeListener>();

	private List<DeviceListener> deviceListeners = new ArrayList<DeviceListener>();

	private List<LocatedDeviceListener> locatedDeviceListeners = new ArrayList<LocatedDeviceListener>();

	private List<ZoneListener> zoneListeners = new ArrayList<ZoneListener>();

	private ReadWriteLock lock = new ReentrantReadWriteLock();

	private Lock readLock = lock.readLock();

	private Lock writeLock = lock.writeLock();

	public ContextManagerImpl() {
		// do nothing
	}

	@Override
	public Zone createZone(String id, int leftX, int topY, int bottomZ, int width, int height, int depth) {
		Zone zone = new ZoneImpl(id, leftX, topY, bottomZ, width, height, depth);
		List<ZoneListener> snapshotZoneListener;
		boolean exists = false;
		readLock.lock();
		try {
			exists = zones.containsKey(id);
		} finally {
			readLock.unlock();
		}
		if (exists) {
			throw new IllegalArgumentException("Zone already exist.");
		}
		writeLock.lock();
		try {
			zones.put(id, zone);
			snapshotZoneListener = getZoneListeners();
		} finally {
			writeLock.unlock();
		}

		// Listeners notification
		for (ZoneListener listener : snapshotZoneListener) {
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
		return createZone(id, center.x - detectionScope, center.y - detectionScope, center.z - detectionScope,
		      detectionScope * 2, detectionScope * 2, detectionScope * 2);
	}

	@Override
	public void removeZone(String id) {
		Zone zone;
		List<ZoneListener> snapshotZoneListener;
		writeLock.lock();
		try {
			zone = zones.remove(id);
			snapshotZoneListener = getZoneListeners();
		} finally {
			writeLock.unlock();
		}
		if (zone == null)
			return;

		// Listeners notification
		for (ZoneListener listener : snapshotZoneListener) {
			try {
				zone.removeListener(listener);
				listener.zoneRemoved(zone);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void moveZone(String id, int leftX, int topY, int bottomZ) throws Exception {
		Zone zone = getZone(id);
		if (zone == null) {
			return;
		}
		Position newPosition = new Position(leftX, topY, bottomZ);
		zone.setLeftTopRelativePosition(newPosition);
	}

	@Override
	public void resizeZone(String id, int width, int height, int depth) throws Exception {
		Zone zone = getZone(id);
		if (zone == null)
			return;
		zone.resize(width, height, depth);
	}

	@Override
	public void removeAllZones() {
		List<Zone> tempZones = getZones();
		for (Zone zone : tempZones) {
			removeZone(zone.getId());
		}
	}

	@Override
	public void addZoneVariable(String zoneId, String variable) {
		Zone zone = getZone(zoneId);
		if (zone == null)
			return;
		zone.addVariable(variable);
	}

	@Override
	public Set<String> getZoneVariables(String zoneId) {
		Zone zone = getZone(zoneId);
		if (zone == null)
			return null;
		return zone.getVariableNames();
	}

	@Override
	public Object getZoneVariableValue(String zoneId, String variable) {
		Zone zone = getZone(zoneId);
		if (zone == null)
			return null;
		return zone.getVariableValue(variable);
	}

	@Override
	public void setZoneVariable(String zoneId, String variableName, Object value) {
		Zone zone = getZone(zoneId);
		if (zone == null)
			return;
		zone.setVariableValue(variableName, value);
	}

	@Override
	public List<Zone> getZones() {
		readLock.lock();
		try {
			return Collections.unmodifiableList(new ArrayList<Zone>(zones.values()));
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Set<String> getZoneIds() {
		readLock.lock();
		try {
			return Collections.unmodifiableSet(new HashSet<String>(zones.keySet()));
		} finally {
			readLock.unlock();
		}

	}

	@Override
	public Zone getZone(String zoneId) {
		readLock.lock();
		try {
			return zones.get(zoneId);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Zone getZoneFromPosition(Position position) {
		List<Zone> tempList = new ArrayList<Zone>();
		List<Zone> zonesSnapshot = getZones();
		for (Zone zone : zonesSnapshot) {
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
		lock.readLock().lock();
		Zone zone = getZone(zoneId);
		Zone parent = getZone(parentId);
		lock.readLock().unlock();
		if (zone == null || parent == null)
			return;
		boolean ok = parent.addZone(zone);
		if (!ok)
			throw new Exception("Zone does not fit in its parent");
	}

	@Override
	public Set<String> getDeviceIds() {
		lock.readLock().lock();
		try {
			return Collections.unmodifiableSet(new HashSet<String>(locatedDevices.keySet()));
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<LocatedDevice> getDevices() {
		lock.readLock().lock();
		try {
			return new ArrayList<LocatedDevice>(locatedDevices.values());
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Position getDevicePosition(String deviceId) {
		LocatedDevice device = getDevice(deviceId);
		if (device != null)
			return device.getCenterAbsolutePosition().clone();
		return null;
	}

	@Override
	public void setDevicePosition(String deviceId, Position position) {

		LocatedDevice device = getDevice(deviceId);

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
	public void setDeviceState(String deviceId, boolean value) {

		GenericDevice device = getGenericDevice(deviceId);

		if (device == null && !(device instanceof GenericDevice))
			return;

		if (value)
			device.setState(GenericDevice.STATE_ACTIVATED);
		else
			device.setState(GenericDevice.STATE_DEACTIVATED);
	}

	@Override
	public LocatedDevice getDevice(String deviceId) {
		lock.readLock().lock();
		try {
			return locatedDevices.get(deviceId);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public GenericDevice getGenericDevice(String deviceId) {
		lock.readLock().lock();
		try {
			return m_devices.get(deviceId);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getDeviceTypes() {
		lock.readLock().lock();
		try {
			return Collections.unmodifiableSet(new HashSet<String>(m_factories.keySet()));
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getProvidedServices(String deviceType) {
		Factory factory = getFactory(deviceType);
		if (factory == null) {
			return null;
		}
		String[] specifications = factory.getComponentDescription().getprovidedServiceSpecification();
		if (specifications == null) {
			return null;
		}
		return new HashSet(Arrays.asList(specifications));
	}

	@Override
	public Set<Variable> getGlobalVariables() {
		return null; // TODO implement it
	}

	@Override
	public Object getGlobalVariableValue(String variableName) {
		return null; // TODO implement it
	}

	@Override
	public void addGlobalVariable(String variableName) {
		// TODO implement it
	}

	@Override
	public void setGlobalVariable(String variableName, Object value) {
		// TODO implement it
	}

	private Factory getFactory(String deviceType) {
		lock.readLock().lock();
		try {
			return m_factories.get(deviceType);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Bind(id = "devices", aggregate = true, optional = true)
	public void bindDevice(GenericDevice genericDevice) {
		
		String sn = genericDevice.getSerialNumber();
		if (m_devices.containsKey(sn)) {
			try {
				throw new IllegalArgumentException("A device with the same ID: " + sn + " was already registered");
         } catch (Exception e) {
         	e.printStackTrace();
         }
			return;
		}
		
		boolean contained = false;
		String deviceType = null;
		LocatedDevice locatedDevice = null;
		List<LocatedDeviceListener> snapshotLocatedDeviceListeners = null;
		List<DeviceListener> snapshotDeviceListeners = null;
		if (genericDevice instanceof Pojo) {
			try {
				deviceType = ((Pojo) genericDevice).getComponentInstance().getFactory().getFactoryName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		lock.writeLock().lock();
		try {
			m_devices.put(sn, genericDevice);
			contained = locatedDevices.containsKey(sn);
			if (!contained) {
				locatedDevice = new LocatedDeviceImpl(sn, new Position(-1, -1), genericDevice, deviceType, this);
				locatedDevices.put(sn, locatedDevice);
				snapshotLocatedDeviceListeners = getLocatedDeviceListeners();
				snapshotDeviceListeners = getDeviceListeners();
			}

		} finally {
			lock.writeLock().unlock();
		}
		// notify only if not already added.
		if (!contained) {
			// Listeners notification
			for (DeviceListener listener : snapshotDeviceListeners) {
				try {
					listener.deviceAdded(genericDevice);
					genericDevice.addListener(listener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (LocatedDeviceListener listener : snapshotLocatedDeviceListeners) {
				try {
					listener.deviceAdded(locatedDevice);
					locatedDevice.addListener(listener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// SimulatedDevice listener added
			genericDevice.addListener((LocatedDeviceImpl) locatedDevice);
		}
	}

	@Unbind(id = "devices")
	public void unbindDevice(GenericDevice genericDevice) {
		String sn = genericDevice.getSerialNumber();
		List<LocatedDeviceListener> snapshotLocatedDeviceListeners = null;
		List<DeviceListener> snapshotDeviceListeners = null;
		LocatedDevice locatedDevice;
		lock.writeLock().lock();
		try {
			m_devices.remove(sn);
			locatedDevice = locatedDevices.remove(sn);
			snapshotLocatedDeviceListeners = getLocatedDeviceListeners();
			snapshotDeviceListeners = getDeviceListeners();
		} finally {
			lock.writeLock().unlock();
		}

		// Listeners notification
		for (DeviceListener listener : snapshotDeviceListeners) {
			try {
				listener.deviceRemoved(genericDevice);
				genericDevice.removeListener(listener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (LocatedDeviceListener listener : snapshotLocatedDeviceListeners) {
			try {
				// If two devices with the same ID where registered the locatedDevice can be null in one of unBinds callbacks
				if (locatedDevice!=null) {
					listener.deviceRemoved(locatedDevice);
					locatedDevice.removeListener(listener);					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// SimulatedDevice listener removed
		genericDevice.removeListener((LocatedDeviceImpl) locatedDevice);
	}

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.device.GenericDevice)")
	public void bindFactory(Factory factory) {
		String deviceType = factory.getName();
		List<DeviceTypeListener> snapshotListeners = null;
		lock.writeLock().lock();
		try {
			m_factories.put(deviceType, factory);
			snapshotListeners = getDeviceTypeListeners();
		} finally {
			lock.writeLock().unlock();
		}

		// Listeners notification
		for (DeviceTypeListener listener : snapshotListeners) {
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
		List<DeviceTypeListener> snapshotListeners = null;
		lock.writeLock().lock();
		try {
			m_factories.remove(deviceType);
			snapshotListeners = getDeviceTypeListeners();
		} finally {
			lock.writeLock().unlock();
		}
		// Listeners notification
		for (DeviceTypeListener listener : snapshotListeners) {
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
			List<Zone> snapshotZones = null;
			lock.writeLock().lock();
			try {
				zoneListeners.add(zoneListener);
				snapshotZones = getZones();
			} finally {
				lock.writeLock().unlock();
			}
			for (Zone zone : snapshotZones)
				zone.addListener(zoneListener);
		}

		if (listener instanceof DeviceListener) {
			DeviceListener deviceListener = (DeviceListener) listener;
			List<LocatedDevice> snapshotDevices;
			writeLock.lock();
			try {
				deviceListeners.add(deviceListener);
				snapshotDevices = getDevices();
			} finally {
				writeLock.unlock();
			}
			for (LocatedDevice device : snapshotDevices) {
				GenericDevice deviceObj = device.getDeviceObject();
				if (deviceObj != null)
					deviceObj.addListener(deviceListener);
			}
		}

		if (listener instanceof LocatedDeviceListener) {
			LocatedDeviceListener deviceListener = (LocatedDeviceListener) listener;
			List<LocatedDevice> snapshotDevices;
			writeLock.lock();
			try {
				locatedDeviceListeners.add(deviceListener);
				snapshotDevices = getDevices();
			} finally {
				writeLock.unlock();
			}
			for (LocatedDevice device : snapshotDevices) {
				device.addListener(deviceListener);
			}
		}

		if (listener instanceof DeviceTypeListener) {
			DeviceTypeListener deviceTypeListener = (DeviceTypeListener) listener;
			writeLock.lock();
			try {
				deviceTypeListeners.add(deviceTypeListener);
			} finally {
				writeLock.unlock();
			}
		}

	}

	@Override
	public void removeListener(IcasaListener listener) {
		if (listener instanceof ZoneListener) {
			ZoneListener zoneListener = (ZoneListener) listener;
			List<Zone> zoneListSnapshot;
			writeLock.lock();
			try {
				zoneListSnapshot = getZones();
				zoneListeners.remove(zoneListener);
			} finally {
				writeLock.unlock();
			}
			for (Zone zone : zoneListSnapshot) {
				zone.removeListener(zoneListener);
			}
		}

		if (listener instanceof DeviceListener) {
			DeviceListener deviceListener = (DeviceListener) listener;
			List<LocatedDevice> locatedDeviceListSnapshot;
			writeLock.lock();
			try {
				locatedDeviceListSnapshot = getDevices();
				deviceListeners.remove(deviceListener);
			} finally {
				writeLock.unlock();
			}
			for (LocatedDevice device : locatedDeviceListSnapshot) {
				GenericDevice deviceObj = device.getDeviceObject();
				if (deviceObj != null)
					deviceObj.removeListener(deviceListener);
			}
		}

		if (listener instanceof LocatedDeviceListener) {
			LocatedDeviceListener deviceListener = (LocatedDeviceListener) listener;
			List<LocatedDevice> locatedDeviceListSnapshot;
			writeLock.lock();
			try {
				locatedDeviceListSnapshot = getDevices();
				locatedDeviceListeners.remove(deviceListener);
			} finally {
				writeLock.unlock();
			}
			for (LocatedDevice device : locatedDeviceListSnapshot) {
				device.removeListener(deviceListener);
			}
		}

		if (listener instanceof DeviceTypeListener) {
			DeviceTypeListener deviceTypeListener = (DeviceTypeListener) listener;
			writeLock.lock();
			try {
				deviceTypeListeners.remove(deviceTypeListener);
			} finally {
				writeLock.unlock();
			}
		}
	}

	private int random(int min, int max) {
		// final double range = (max - 10) - (min + 10);
		final double range = max - min;
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

		int newX = random(minX + GenericDevice.DEFAULT_WIDTH, minX + zone.getXLength() - GenericDevice.DEFAULT_WIDTH);
		int newY = random(minY + GenericDevice.DEFAULT_HEIGHT, minY + zone.getYLength() - GenericDevice.DEFAULT_HEIGHT);

		return new Position(newX, newY);
	}

	@Override
	public void resetContext() {
		removeAllZones();
	}

	private List<ZoneListener> getZoneListeners() {
		readLock.lock();
		try {
			return new ArrayList<ZoneListener>(zoneListeners);
		} finally {
			readLock.unlock();
		}
	}

	private List<DeviceTypeListener> getDeviceTypeListeners() {
		readLock.lock();
		try {
			return new ArrayList<DeviceTypeListener>(deviceTypeListeners);
		} finally {
			readLock.unlock();
		}
	}

	private List<LocatedDeviceListener> getLocatedDeviceListeners() {
		readLock.lock();
		try {
			return new ArrayList<LocatedDeviceListener>(locatedDeviceListeners);
		} finally {
			readLock.unlock();
		}
	}

	private List<DeviceListener> getDeviceListeners() {
		readLock.lock();
		try {
			return new ArrayList<DeviceListener>(deviceListeners);
		} finally {
			readLock.unlock();
		}
	}

	private class ZoneComparable implements Comparator<Zone> {
		@Override
		public int compare(Zone zone0, Zone zone1) {
			return zone1.getLayer() - zone0.getLayer();
		}
	}

}
