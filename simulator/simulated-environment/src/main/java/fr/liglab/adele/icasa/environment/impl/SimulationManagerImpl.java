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

import static fr.liglab.adele.icasa.environment.SimulatedEnvironment.PRESENCE;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
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

import fr.liglab.adele.icasa.context.Context;
import fr.liglab.adele.icasa.context.ContextEvent;
import fr.liglab.adele.icasa.context.ContextListener;
import fr.liglab.adele.icasa.context.ContextService;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulatedEnvironment;
import fr.liglab.adele.icasa.environment.SimulationManager;

/**
 * Implementation of the simulated environment manager component, that manage
 * the links between simulated devices and simulated environments.
 * 
 * @author bourretp
 */
@Component
@Provides
@Instantiate
public class SimulationManagerImpl implements SimulationManager {

	private final Random m_random = new Random();

	private ContextService m_contextService;

	private final Map<String, EnvironmentEntry> m_environments = new HashMap<String, EnvironmentEntry>();

	private final Map<String, SimulatedDevice> m_devices = new HashMap<String, SimulatedDevice>();

	private final Map<String, Factory> m_factories = new Hashtable<String, Factory>();

	private final List<DevicePositionListener> m_devicePositionListeners = new LinkedList<DevicePositionListener>();

	private final List<UserPositionListener> m_userPositionListeners = new LinkedList<UserPositionListener>();

	@Requires(filter = "(&(factory.name=fr.liglab.adele.icasa.environment.impl.SimulatedEnvironmentImpl)(factory.state="
	      + Factory.VALID + "))")
	private Factory m_envFactory;

	@Bind(id = "contextService")
	public synchronized void bindContextService(final ContextService contextService) {
		m_contextService = contextService;
		m_contextService.addContextListener(new DeviceContextListener(), "/device/*");
		m_contextService.addContextListener(new UserContextListener(), "/user/*");
	}

	@Unbind(id = "contextService")
	public synchronized void unbindContextService(final ContextService contextService) {
		m_contextService = null;
	}

	@Bind(id = "environments", aggregate = true, optional = true)
	public synchronized void bindEnvironment(SimulatedEnvironment env, Map<String, ?> properties) {
		final int leftX = (Integer) properties.get("leftX");
		final int rightX = (Integer) properties.get("rightX");
		final int topY = (Integer) properties.get("topY");
		final int bottomY = (Integer) properties.get("bottomY");
		final Position topLeft = new Position(leftX, topY);
		final Position bottomRight = new Position(rightX, bottomY);
		final Zone zone = new Zone(topLeft, bottomRight);
		final EnvironmentEntry entry = new EnvironmentEntry();
		entry.service = env;
		entry.zone = zone;
		m_environments.put(env.getEnvironmentId(), entry);
	}

	@Unbind(id = "environments")
	public synchronized void unbindEnvironment(SimulatedEnvironment env) {
		m_environments.remove(env.getEnvironmentId());
		// Unbind devices that were bound to the leaving environment
		for (SimulatedDevice dev : m_devices.values()) {
			if (env.getEnvironmentId().equals(dev.getEnvironmentId())) {
				dev.unbindSimulatedEnvironment(env);
			}
		}
	}

	@Bind(id = "devices", aggregate = true, optional = true)
	public synchronized void bindDevice(SimulatedDevice dev) {
		m_devices.put(dev.getSerialNumber(), dev);
	}

	@Unbind(id = "devices")
	public synchronized void unbindDevice(SimulatedDevice dev) {
		m_devices.remove(dev.getSerialNumber());
		// Unbind device if it were bound to an environment
		if (dev.getEnvironmentId() != null) {
			SimulatedEnvironment env = m_environments.get(dev.getEnvironmentId()).service;
			dev.unbindSimulatedEnvironment(env);
		}
	}

	@Bind(id = "factories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.environment.SimulatedDevice)")
	public synchronized void bindFactory(Factory factory) {
		m_factories.put(factory.getName(), factory);
	}

	@Unbind(id = "factories")
	public synchronized void unbindFactory(Factory factory) {
		m_factories.remove(factory.getName());
	}

	@Override
	public synchronized Set<String> getEnvironments() {
		return Collections.unmodifiableSet(new HashSet<String>(m_environments.keySet()));
	}

	@Override
	public synchronized Set<String> getDevices() {
		return Collections.unmodifiableSet(new HashSet<String>(m_devices.keySet()));
	}

	@Override
	public synchronized Zone getEnvironmentZone(final String environmentId) {
		final EnvironmentEntry entry = m_environments.get(environmentId);
		if (entry != null) {
			return entry.zone;
		} else {
			// No such environment!
			return null;
		}
	}

	@Override
	public synchronized String getEnvironmentFromPosition(final Position position) {
		if (position == null) {
			return null;
		}
		for (Entry<String, EnvironmentEntry> e : m_environments.entrySet()) {
			if (e.getValue().zone.contains(position)) {
				return e.getKey();
			}
		}
		// Unknown position!
		return null;
	}

	@Override
	public synchronized Position getDevicePosition(final String deviceSerialNumber) {
		if (deviceSerialNumber == null) {
			throw new NullPointerException("deviceSerialNumber");
		}
		final Context device = getDeviceContext(deviceSerialNumber);
		final Object x = device.getProperty("positionX");
		final Object y = device.getProperty("positionY");
		if (x == null || y == null || !(x instanceof Integer) || !(y instanceof Integer)) {
			return null;
		}
		return new Position((Integer) x, (Integer) y);
	}

	@Override
	public synchronized void setDevicePosition(final String deviceSerialNumber, final Position position) {
		if (deviceSerialNumber == null) {
			throw new NullPointerException("deviceSerialNumber");
		}
		// 1. Update the context.
		final Context device = getDeviceContext(deviceSerialNumber);
		if (position != null) {
			device.setProperty("positionX", position.x);
			device.setProperty("positionY", position.y);
		} else {
			device.setProperty("positionX", null);
			device.setProperty("positionY", null);
		}
		// 2. Rebind the device to the environment containing the new position.
		final String environmentId = getEnvironmentFromPosition(position);
		unbindDeviceFromEnvironment(deviceSerialNumber);
		bindDeviceToEnvironment(deviceSerialNumber, environmentId);
	}

	@Override
	public synchronized void setDeviceLocation(final String deviceSerialNumber, final String environmentId) {
		// 1. get environment zone
		final Zone zone;
		if (environmentId != null) {
			zone = getEnvironmentZone(environmentId);
		} else {
			zone = null;
		}
		// 2. generate a random position.
		final Position position;
		if (zone != null) {
			final int x = random(zone.leftX, zone.rightX);
			final int y = random(zone.topY, zone.bottomY);
			position = new Position(x, y);
		} else {
			position = null;
		}
		// Set device position.
		setDevicePosition(deviceSerialNumber, position);
	}

	@Override
	public synchronized Position getUserPosition(final String userName) {
		if (userName == null) {
			throw new NullPointerException("userName");
		}
		final Context userContext = getUserContext(userName);
		if (userContext != null) {
			final Object x = userContext.getProperty("positionX");
			final Object y = userContext.getProperty("positionY");
			if (x == null || y == null || !(x instanceof Integer) || !(y instanceof Integer)) {
				return null;
			}
			return new Position((Integer) x, (Integer) y);
		}
		return null;
	}

	@Override
	public synchronized void setUserPosition(final String userName, final Position position) {
		if (userName == null) {
			throw new NullPointerException("userName");
		}
		// 1. Update the context.
		final Context userContext = getUserContext(userName);
		final String previousLocation = getEnvironmentFromPosition(getUserPosition(userName));
		if (userContext != null && position != null) {
			userContext.setProperty("positionX", position.x);
			userContext.setProperty("positionY", position.y);
		} else {
			userContext.setProperty("positionX", null);
			userContext.setProperty("positionY", null);
		}
		// 2. Unset/set presence
		final String newLocation = getEnvironmentFromPosition(position);
		calculatePrensenceVariable(previousLocation, newLocation);
		/*
		 * if ((prevLoc == null && newLoc == null) || (prevLoc != null &&
		 * prevLoc.equals(newLoc))) { // Same location => nothing to do! return; }
		 * 
		 * if (prevLoc != null) { final SimulatedEnvironment env =
		 * m_environments.get(prevLoc).service; env.lock(); try { double presence
		 * = env.getProperty(PRESENCE); env.setProperty(PRESENCE, presence -
		 * 1.0d); } finally { env.unlock(); } } if (newLoc != null) { final
		 * SimulatedEnvironment env = m_environments.get(newLoc).service;
		 * env.lock(); try { double presence = env.getProperty(PRESENCE);
		 * env.setProperty(PRESENCE, presence + 1.0d); } finally { env.unlock(); }
		 * }
		 */
	}

	@Override
	public void setDeviceFault(String deviceId, boolean value) {
		SimulatedDevice device = m_devices.get(deviceId);
		if (device != null) {
			if (value)
				device.setFault(SimulatedDevice.FAULT_YES);
			else
				device.setFault(SimulatedDevice.FAULT_NO);
		}
	}

	@Override
	public void setDeviceState(String deviceId, boolean value) {
		SimulatedDevice device = m_devices.get(deviceId);
		if (device != null) {
			if (value)
				device.setState(SimulatedDevice.STATE_ACTIVATED);
			else
				device.setState(SimulatedDevice.STATE_DEACTIVATED);
		}
	}

	private void calculatePrensenceVariable(String previousLocation, String newLocation) {
		if ((previousLocation == null && newLocation == null)
		      || (previousLocation != null && previousLocation.equals(newLocation))) {
			// Same location => nothing to do!
			return;
		}
		if (previousLocation != null) {
			final SimulatedEnvironment env = m_environments.get(previousLocation).service;
			env.lock();
			try {
				double presence = env.getProperty(PRESENCE);
				env.setProperty(PRESENCE, presence - 1.0d);
			} finally {
				env.unlock();
			}
		}
		if (newLocation != null) {
			final SimulatedEnvironment env = m_environments.get(newLocation).service;
			env.lock();
			try {
				double presence = env.getProperty(PRESENCE);
				env.setProperty(PRESENCE, presence + 1.0d);
			} finally {
				env.unlock();
			}
		}
	}

	@Override
	public synchronized void setUserLocation(final String userName, final String environmentId) {
		// 1. get environment zone
		final Zone zone;
		if (environmentId != null) {
			zone = getEnvironmentZone(environmentId);
		} else {
			zone = null;
		}
		// 2. generate a random position.
		final Position position;
		if (zone != null) {
			final int x = random(zone.leftX, zone.rightX);
			final int y = random(zone.topY, zone.bottomY);
			position = new Position(x, y);
		} else {
			position = null;
		}
		// Set user position.
		setUserPosition(userName, position);
	}

	@Override
	public void addUser(String userName) {
		final Context root = m_contextService.getRootContext();
		Context usersContext = root.getChild("user");
		if (usersContext != null) {
			final Context user = usersContext.getChild(userName);
			if (user == null)
				usersContext.createChild(userName);
		} else {
			usersContext = root.createChild("user");
			usersContext.createChild(userName);
		}
	}

	@Override
	public void removeUser(String userName) {
		final Context root = m_contextService.getRootContext();
		Context usersContext = root.getChild("user");
		if (usersContext != null) {
			final String previousLocation = getEnvironmentFromPosition(getUserPosition(userName));
			calculatePrensenceVariable(previousLocation, null);
			usersContext.removeChild(userName);
		}
	}

	@Override
	public synchronized void addDevicePositionListener(final DevicePositionListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		m_devicePositionListeners.add(listener);
	}

	@Override
	public synchronized void removeDevicePositionListener(final DevicePositionListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		m_devicePositionListeners.remove(listener);
	}

	@Override
	public synchronized void addUserPositionListener(final UserPositionListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		m_userPositionListeners.add(listener);
	}

	@Override
	public synchronized void removeUserPositionListener(final UserPositionListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		m_userPositionListeners.remove(listener);
	}

	@Override
	public synchronized void removeAllUsers() {
		// Remove the "/user" context
		final Context root = m_contextService.getRootContext();
		if (root.getChild("user") != null) {
			root.removeChild("user");
		}
	}

	@Override
	public Double getVariableValue(String environmentId, String variable) {
		if (environmentId == null) {
			// Nothing to bind to => nothing to do!
			return 0.0;
		}
		final SimulatedEnvironment env = m_environments.get(environmentId).service;
		return env.getProperty(variable);
	}

	@Override
	public Set<String> getEnvironmentVariables(String environmentId) {
		if (environmentId == null) {
			return new HashSet<String>();
		}
		final SimulatedEnvironment env = m_environments.get(environmentId).service;
		return Collections.unmodifiableSet(env.getPropertyNames());
	}

	@Override
	public void setEnvironmentVariable(String environmentId, String variable, Double value) {
		if (environmentId == null) {
			// Nothing to bind to => nothing to do!
			return;
		}
		final SimulatedEnvironment env = m_environments.get(environmentId).service;
		env.lock();
		env.setProperty(variable, value);
		env.unlock();

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
			if (description!=null)
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
		SimulatedDevice device = m_devices.get(deviceId);
		if ((device != null) && (device instanceof Pojo)) {
			Pojo pojo = (Pojo) device;
			pojo.getComponentInstance().dispose();
		}
	}

	@Override
	public synchronized Set<String> getDeviceFactories() {
		return Collections.unmodifiableSet(new HashSet<String>(m_factories.keySet()));
	}

	private static class EnvironmentEntry {
		private SimulatedEnvironment service;
		private Zone zone;
	}

	private Context getDeviceContext(final String deviceSerialNumber) {
		final Context root = m_contextService.getRootContext();
		Context allDevices = root.getChild("device");
		if (allDevices == null) {
			allDevices = root.createChild("device");
		}
		final Context device = allDevices.getChild(deviceSerialNumber);
		if (device != null) {
			return device;
		} else {
			return allDevices.createChild(deviceSerialNumber);
		}
	}

	private Context getUserContext(final String userName) {
		final Context root = m_contextService.getRootContext();
		Context allUsers = root.getChild("user");
		if (allUsers != null)
			return allUsers.getChild(userName);
		return null;
	}

	private int random(int min, int max) {
		final double range = (max - 15) - (min + 15);
		if (range <= 0.0) {
			throw new IllegalArgumentException("min >= max");
		}
		return min + (int) (range * m_random.nextDouble());
	}

	private void unbindDeviceFromEnvironment(final String deviceSerialNumber) {
		final SimulatedDevice dev = m_devices.get(deviceSerialNumber);
		if (dev == null) {
			// No device => nothing to do!
			return;
		}
		final String environmentId = dev.getEnvironmentId();
		if (environmentId == null) {
			// Device was not bound => nothing to do!
			return;
		}
		final SimulatedEnvironment env = m_environments.get(environmentId).service;
		dev.unbindSimulatedEnvironment(env);
	}

	private void bindDeviceToEnvironment(final String deviceSerialNumber, final String environmentId) {
		if (environmentId == null) {
			// Nothing to bind to => nothing to do!
			return;
		}
		final SimulatedDevice dev = m_devices.get(deviceSerialNumber);
		if (dev == null) {
			// No device => nothing to do!
			return;
		}
		final SimulatedEnvironment env = m_environments.get(environmentId).service;
		dev.bindSimulatedEnvironment(env);
	}

	private class DeviceContextListener implements ContextListener {

		@Override
		public void contextChanged(final ContextEvent event) {
			if (event.getType() != ContextEvent.MODIFIED) {
				// We do not care about context creation/removal.
				return;
			}
			final String propertyName = event.getPropertyName();
			if (!propertyName.equals("positionX") && !propertyName.equals("positionY")) {
				// We only care about position properties.
				return;
			}
			synchronized (SimulationManagerImpl.this) {
				final Context root = m_contextService.getRootContext();
				final Context allDevices = root.getChild("device");
				final Context context = event.getContext();
				if (event.getContext().getParent() != allDevices) {
					// We only care about direct children of the "/device/"
					// context.
					return;
				}
				// Retrieve position information
				final Object x = context.getProperty("positionX");
				final Object y = context.getProperty("positionY");
				final Position position;
				if (x == null || y == null || !(x instanceof Integer) || !(y instanceof Integer)) {
					position = null;
				} else {
					position = new Position((Integer) x, (Integer) y);
				}
				// Notify listeners
				final String deviceSerialNumber = context.getName();
				for (DevicePositionListener listener : m_devicePositionListeners) {
					listener.devicePositionChanged(deviceSerialNumber, position);
				}
			}
		}

	}

	private class UserContextListener implements ContextListener {

		@Override
		public void contextChanged(ContextEvent event) {

			if (event.getType() == ContextEvent.CREATED) {
				synchronized (SimulationManagerImpl.this) {
					final Context root = m_contextService.getRootContext();
					final Context allUsers = root.getChild("user");
					final Context context = event.getContext();
					if (context.getParent() == allUsers) {
						final String userName = context.getName();
						for (UserPositionListener listener : m_userPositionListeners) {
							listener.userAdded(userName);
						}
					}
				}
			}

			if (event.getType() == ContextEvent.REMOVED) {
				synchronized (SimulationManagerImpl.this) {
					final Context root = m_contextService.getRootContext();
					final Context allUsers = root.getChild("user");
					final Context context = event.getContext();
					if (context.getParent() == allUsers) {
						final String userName = context.getName();
						for (UserPositionListener listener : m_userPositionListeners) {
							listener.userRemoved(userName);
						}
					}
				}
			}

			if (event.getType() == ContextEvent.MODIFIED) {
				final String propertyName = event.getPropertyName();
				if (!propertyName.equals("positionX") && !propertyName.equals("positionY")) {
					// We only care about position properties.
					return;
				}
				synchronized (SimulationManagerImpl.this) {
					final Context root = m_contextService.getRootContext();
					final Context allUsers = root.getChild("user");
					final Context context = event.getContext();
					if (context.getParent() == allUsers) {
						final Object x = context.getProperty("positionX");
						final Object y = context.getProperty("positionY");
						final Position position;
						if (x == null || y == null || !(x instanceof Integer) || !(y instanceof Integer)) {
							position = null;
						} else {
							position = new Position((Integer) x, (Integer) y);
						}
						// Notify listeners
						final String userName = context.getName();
						for (UserPositionListener listener : m_userPositionListeners) {
							listener.userPositionChanged(userName, position);
						}
					}
				}
			}
		}

	}

	@Override
   public void createEnvironment(String id, String description, int leftX, int topY, int rightX, int bottomY) {
		System.out.println("Simulated environment :" + id);
		// Create the simulated environment
		Dictionary<String, Object> config = new Hashtable<String, Object>();
		config.put(SimulatedEnvironment.ENVIRONMENT_ID, id);
		// Force instance name.
		config.put("instance.name",
		      "fr.liglab.adele.icasa.environment.impl.SimulatedEnvironmentImpl" + "-" + id);
		if (description != null) {
			config.put(Constants.SERVICE_DESCRIPTION, description);
		} else {
			config.put(Constants.SERVICE_DESCRIPTION, id);
		}
		// Add environment coordinates.
		config.put("leftX", leftX);
		config.put("topY", topY);
		config.put("rightX", rightX);
		config.put("bottomY", bottomY);
		try {
	      m_envFactory.createComponentInstance(config);
      } catch (UnacceptableConfiguration e) {
	      e.printStackTrace();
      } catch (MissingHandlerException e) {
	      e.printStackTrace();
      } catch (ConfigurationException e) {
	      e.printStackTrace();
      }
   }

}
