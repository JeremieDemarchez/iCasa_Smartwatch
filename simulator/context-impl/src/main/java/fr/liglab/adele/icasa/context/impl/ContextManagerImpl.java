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
package fr.liglab.adele.icasa.context.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.Pojo;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;

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
import fr.liglab.adele.icasa.location.util.ZoneComparable;

@Component
@Provides
@Instantiate(name = "ContextManager-1")
public class ContextManagerImpl implements ContextManager {

	private Map<String, Zone> zones = new HashMap<String, Zone>();

	private Map<String, LocatedDevice> locatedDevices = new HashMap<String, LocatedDevice>();

	private Map<String, GenericDevice> m_devices = new HashMap<String, GenericDevice>();


	private Map<String, Factory> m_factories = new HashMap<String, Factory>();

	private List<DeviceTypeListener> deviceTypeListeners = new ArrayList<DeviceTypeListener>();

	private List<LocatedDeviceListener> deviceListeners = new ArrayList<LocatedDeviceListener>();


	private List<ZoneListener> zoneListeners = new ArrayList<ZoneListener>();


	public ContextManagerImpl() {

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
	public void setDeviceState(String deviceId, boolean value) {
		
		GenericDevice device = m_devices.get(deviceId);

		if (device == null && !(device instanceof GenericDevice))
			return;

		if (value)
			device.setState(GenericDevice.STATE_ACTIVATED);
		else
			device.setState(GenericDevice.STATE_DEACTIVATED);
	}


	@Override
	public LocatedDevice getDevice(String deviceId) {
		synchronized (locatedDevices) {
			return locatedDevices.get(deviceId);
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

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.device.GenericDevice)")
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


		if (listener instanceof DeviceTypeListener) {
			DeviceTypeListener deviceTypeListener = (DeviceTypeListener) listener;
			synchronized (deviceTypeListeners) {
				deviceTypeListeners.remove(deviceTypeListener);
			}
		}
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




}
