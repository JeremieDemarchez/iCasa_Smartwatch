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
package fr.liglab.adele.icasa.script.impl;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.Position;
import fr.liglab.adele.icasa.environment.SimulatedEnvironment;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Zone;
import fr.liglab.adele.icasa.script.Interpreter;
import fr.liglab.adele.icasa.script.Script;
import fr.liglab.adele.icasa.script.impl.model.DeviceModel;
import fr.liglab.adele.icasa.script.impl.model.EnvironmentModel;
import fr.liglab.adele.icasa.script.impl.parser.IcasaScript;

/**
 * Implementation of the iCASA script interpreter.
 * 
 * @author bourretp
 */
// TODO replace stdout by logs
@Component
@Provides
@Instantiate
public class InterpreterImpl implements Interpreter {

	private final BundleContext m_context;

	@Property(name = "timeout", value = "1000")
	private long m_timeout;

	@Requires(filter = "(&(factory.name=fr.liglab.adele.icasa.environment.impl.SimulatedEnvironmentImpl)(factory.state="
	      + Factory.VALID + "))")
	private Factory m_envFactory;

	@Requires
	private SimulationManager m_manager;

	public InterpreterImpl(BundleContext context) {
		m_context = context;
	}

	public Script parse(InputStream in) throws Exception {
		return new IcasaScript(this, in);
	}

	public void start(IcasaScript script) throws UnacceptableConfiguration, MissingHandlerException,
	      ConfigurationException, InterruptedException, InvalidSyntaxException, TimeoutException, Exception {
		for (EnvironmentModel env : script.getDeclaredEnvironments()) {
			createEnvironment(script.getInstances(), env);
		}
		System.out.println("!!!");
	}

	public void stop(IcasaScript script) throws UnacceptableConfiguration, MissingHandlerException,
	      ConfigurationException, InterruptedException, InvalidSyntaxException, TimeoutException, Exception {
		for (EnvironmentModel env : script.getDeclaredEnvironments()) {
			destroyEnvironment(script.getInstances(), env);
		}
		System.out.println("!!!");
	}

	private void createEnvironment(Map<String, ComponentInstance> instances, EnvironmentModel model)
	      throws UnacceptableConfiguration, MissingHandlerException, ConfigurationException, InterruptedException,
	      InvalidSyntaxException, TimeoutException {
		System.out.println("Simulated environment :" + model.getId());
		// Create the simulated environment
		Dictionary<String, Object> config = new Hashtable<String, Object>();
		config.put(SimulatedEnvironment.ENVIRONMENT_ID, model.getId());
		// Force instance name.
		config.put("instance.name",
		      "fr.liglab.adele.icasa.environment.impl.SimulatedEnvironmentImpl" + "-" + model.getId());
		if (model.getDescription() != null) {
			config.put(Constants.SERVICE_DESCRIPTION, model.getDescription());
		} else {
			config.put(Constants.SERVICE_DESCRIPTION, model.getId());
		}
		// Add environment coordinates.
		config.put("leftX", model.getTopLeftX());
		config.put("topY", model.getTopLeftY());
		config.put("rightX", model.getBottomRightX());
		config.put("bottomY", model.getBottomRightY());
		instances.put(model.getId(), m_envFactory.createComponentInstance(config));
		System.out.println("\tComponent created (" + model.getTopLeftX() + ' ' + model.getTopLeftY() + ' '
		      + model.getBottomRightX() + ' ' + model.getBottomRightY() + ')');
		// Wait for the simulated environment service
		System.out.println("\tWaiting for service...");
		ServiceTracker envTracker = new ServiceTracker(m_context, createEnvironmentFilter(model.getId()), null);
		envTracker.open();
		SimulatedEnvironment env = (SimulatedEnvironment) envTracker.waitForService(m_timeout);

		if (env != null) {
			System.out.println("\tEnvironment Service available !");
			// Set environment properties
			for (Map.Entry<String, Double> entry : model.getProperties().entrySet()) {
				System.out.println("\tSetting property : " + entry.getKey() + " = " + entry.getValue());
				env.setProperty(entry.getKey(), entry.getValue());
			}
			envTracker.close();
			// Create the devices
			for (DeviceModel device : model.getDeclaredDevices()) {
				createDevice(instances, device);
				setDeviceLocation(device, model.getId());
				System.out.println("\t\tDevice is now bound to environment " + model.getId());
			}
		} else {
			System.out.println("\tEnvironment Service unavailable !");
		}
	}

	private void destroyEnvironment(Map<String, ComponentInstance> instances, EnvironmentModel model)
	      throws UnacceptableConfiguration, MissingHandlerException, ConfigurationException, InterruptedException,
	      InvalidSyntaxException, TimeoutException {
		System.out.println("Simulated environment :" + model.getId());
		// First unbind and destroy the devices of the environment
		for (DeviceModel device : model.getDeclaredDevices()) {
			System.out.println("\tSimulated device :" + device.getId());
			m_manager.setDeviceLocation(device.getId(), null);
			System.out.println("\t\tDevice is now unbound from environment " + model.getId());
			ComponentInstance instance = instances.remove(device.getId());
			if (instance!=null) {
				instance.dispose();
				System.out.println("\t\tDevice destroyed");				
			}
		}
		// TODO unbind external devices that are bound to the environment (need
		// to add methods to envManager)
		// Now we can destroy the environment
		ComponentInstance instance = instances.remove(model.getId());
		if (instance!=null) {
			instance.dispose();
			System.out.println("\tEnvironment destroyed");
		}
	}

	private void createDevice(Map<String, ComponentInstance> instances, DeviceModel model)
	      throws InvalidSyntaxException, InterruptedException, UnacceptableConfiguration, MissingHandlerException,
	      ConfigurationException, TimeoutException {

		System.out.println("\tSimulated device :" + model.getId());
		// Find the iPOJO factory
		System.out.println("\t\tWaiting for iPOJO factory '" + model.getIpojoFactory() + "'...");
		ServiceTracker factoryTracker = new ServiceTracker(m_context, createFactoryFilter(model.getIpojoFactory()), null);
		factoryTracker.open();
		Factory factory = (Factory) factoryTracker.waitForService(m_timeout);

		if (factory != null) {
			System.out.println("\t\tFactory available !");
			// Configure the component to be created
			Dictionary<String, String> config = new Hashtable<String, String>();
			for (Map.Entry<String, String> entry : model.getConfiguration().entrySet()) {
				config.put(entry.getKey(), entry.getValue());
				System.out.println("\t\tConfigured property : " + entry.getKey() + " = " + entry.getValue());
			}
			config.put(GenericDevice.DEVICE_SERIAL_NUMBER, model.getId());
			// Service have to be configured with ConfigAdmin. Using this property
			// iPOJO property handler made it automatically
			config.put("managed.service.pid", model.getId());
			config.put("instance.name", model.getIpojoFactory() + "-" + model.getId());
			// Create the component
			instances.put(model.getId(), factory.createComponentInstance(config));
			System.out.println("\t\tComponent created");
			factoryTracker.close();
			// Wait for the simulated device service
			System.out.println("\t\tWaiting for service...");
			ServiceTracker deviceTracker = new ServiceTracker(m_context, createDeviceFilter(model.getId()), null);
			deviceTracker.open();
			Object o = deviceTracker.waitForService(m_timeout);
			if (o == null) {
				throw new TimeoutException();
			}
			System.out.println("\t\tService available !");
			deviceTracker.close();
		} else {
			System.out.println("\t\tFactory unavailable !");
		}

	}

	private void setDeviceLocation(DeviceModel device, String location) {
		final int x = device.getPositionX();
		final int y = device.getPositionY();
		final Position position;
		if (x != -1 && y != -1) {
			position = new Position(x, y);
		} else {
			position = null;
		}
		final Zone allowedZone = m_manager.getEnvironmentZone(location);
		if (position != null && allowedZone.contains(position)) {
			m_manager.setDevicePosition(device.getId(), position);
		} else {
			m_manager.setDeviceLocation(device.getId(), location);
		}
	}

	private Filter createDeviceFilter(String id) throws InvalidSyntaxException {
		return m_context.createFilter("(" + GenericDevice.DEVICE_SERIAL_NUMBER + "=" + id + ")");
	}

	private Filter createEnvironmentFilter(String id) throws InvalidSyntaxException {
		return m_context.createFilter("(&(" + Constants.OBJECTCLASS + "=" + SimulatedEnvironment.class.getName() + ")("
		      + SimulatedEnvironment.ENVIRONMENT_ID + "=" + id + "))");
	}

	private Filter createFactoryFilter(String id) throws InvalidSyntaxException {
		return m_context.createFilter("(&(" + Constants.OBJECTCLASS + "=" + Factory.class.getName() + ")(factory.name="
		      + id + "))");
	}
}
