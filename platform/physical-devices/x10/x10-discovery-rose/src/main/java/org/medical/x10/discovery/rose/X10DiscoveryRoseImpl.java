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
package org.medical.x10.discovery.rose;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.ow2.chameleon.rose.RoseMachine;

import fr.orange.x10.Command;
import fr.orange.x10.Controller;
import fr.orange.x10.UnitEvent;
import fr.orange.x10.UnitListener;

@Component(name = "x10.rose.discovery", immediate = true)
public class X10DiscoveryRoseImpl {

	@Property(name = "x10.module.type", value="cm11a")
	private String m_module;

	@Property(name = "x10.module.port", value = "COM3")
	private String m_port; // "/dev/ttyUSB0"

	private BundleContext m_context;

	private final ServiceTracker m_controllerTracker;

	private ServiceTracker m_controllerFactoryTracker;

	private Map</* housecode + unitcode */String, /* last event timestamp */Long> m_presenceTimestamps = new HashMap<String, Long>();

	@Requires
	private RoseMachine roseMachine;

	private DetectUnavailableSensorThread m_thread;

	private class DetectUnavailableSensorThread extends Thread {

		private boolean m_stoped = false;

		@Override
		public void run() {
			while (!m_stoped) {

				for (String address : m_presenceTimestamps.keySet()) {
					long timestamp = m_presenceTimestamps.get(address);
					long diffTS = System.currentTimeMillis() - timestamp;
					if (diffTS > 10000) {
						unregisterPresenceSensorInROSE(address);
						m_presenceTimestamps.remove(address);
					}
				}

				synchronized (this) {
					try {
						wait(3000);
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			}
		}

	}

	public X10DiscoveryRoseImpl(BundleContext context) {
		m_context = context;
		Filter servFilter = null;
		try {
			servFilter = m_context.createFilter("(&(" + Constants.OBJECTCLASS
					+ "=" + Factory.class.getName()
					+ ")(factory.name=X10.controller))");
		} catch (InvalidSyntaxException e1) {
			// should never happen
			e1.printStackTrace();
		}
		m_controllerFactoryTracker = new ServiceTracker(m_context, servFilter,
				new ServiceTrackerCustomizer() {

					private Factory _factory;

					public Object addingService(ServiceReference reference) {
						_factory = (Factory) m_context.getService(reference);

						Hashtable properties = new Hashtable();
						properties.put("port", m_port);
						properties.put("module", m_module);

						try {
							_factory.createComponentInstance(properties);
						} catch (UnacceptableConfiguration e) {
							e.printStackTrace();
						} catch (MissingHandlerException e) {
							e.printStackTrace();
						} catch (ConfigurationException e) {
							e.printStackTrace();
						}

						return _factory;
					}

					public void modifiedService(ServiceReference reference,
							Object service) {
						// do nothing
					}

					public void removedService(ServiceReference reference,
							Object service) {
						m_context.ungetService(reference);

						// remove all device proxies

					}

				});

		m_controllerTracker = new ServiceTracker(m_context,
				Controller.class.getName(), new ServiceTrackerCustomizer() {

					private Controller _controller;
					private UnitListener _listener;

					public Object addingService(ServiceReference reference) {
						_controller = (Controller) m_context
								.getService(reference);

						_listener = new UnitListener() {

							public void allUnitsOff(UnitEvent event) {
								// do nothing
							}

							public void allLightsOff(UnitEvent event) {
								// do nothing
							}

							public void allLightsOn(UnitEvent event) {
								// do nothing
							}

							public void unitOn(UnitEvent event) {
								Command command = event.getCommand();

								// event from presence sensor
								// TODO check that one command parameter = 1126
								if (command.getFunction() != Short
										.valueOf("1634"))
									return;
								if (command.getFunctionByte() != Byte
										.valueOf("2"))
									return;
								if (command.getLevel() != 0)
									return;
								
								String serialNumber = getSerialNumber(""
										+ command.getHouseCode() + command.getUnitCode());
								boolean registered = m_presenceTimestamps.containsKey(serialNumber);
								
								m_presenceTimestamps.put(serialNumber,
										System.currentTimeMillis());
								if (!registered)
									registerPresenceSensorInROSE(command);
							}

							public void unitOff(UnitEvent event) {
								// do nothing
							}

							public void unitDim(UnitEvent event) {
								// do nothing
							}

							public void unitBright(UnitEvent event) {
								// do nothing
							}

						};
						_controller.addUnitListener(_listener);

						return _controller;
					}

					public void modifiedService(ServiceReference reference,
							Object service) {
						// do nothing
					}

					public void removedService(ServiceReference reference,
							Object service) {

						_controller.removeUnitListener(_listener);

						m_context.ungetService(reference);
					}
				});
	}

	@Validate
	public void start() {
		m_thread = new DetectUnavailableSensorThread();
		m_thread.start();

		m_controllerTracker.open();
		m_controllerFactoryTracker.open();
	}

	@Invalidate
	public void stop() {
		if (m_thread != null) {
			m_thread.m_stoped = true;
			m_thread = null;
		}

		m_controllerTracker.close();
		m_controllerFactoryTracker.close();
	}

	private void unregisterPresenceSensorInROSE(String serialNumber) {

		roseMachine.removeRemote(serialNumber);
	}

	private void registerPresenceSensorInROSE(Command /* intercepted */command) {

		Map props = new Properties();

		char houseCode = command.getHouseCode();
		int unitCode = command.getUnitCode();
		String serialNumber = getSerialNumber(houseCode, unitCode);

		props.put(RemoteConstants.ENDPOINT_ID, serialNumber);
		props.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "x10");
		props.put(
				"objectClass",
				new String[] { "fr.liglab.adele.icasa.device.presence.PresenceSensor" });

		props.put("id", serialNumber);
		props.put("x10.device.type", "presence.sensor");
		props.put("x10.house.code", "" + houseCode);
		props.put("x10.unit.code", "" + unitCode);
		props.put("x10.function", "" + command.getFunction());
		props.put("x10.function.byte", "" + command.getFunctionByte());
		props.put("x10.level", "" + command.getLevel());
		props.put("x10.controller.port", m_port);
		props.put("x10.controller.port", m_module);

		EndpointDescription epd = new EndpointDescription(props);
		roseMachine.putRemote(serialNumber, epd);
	}

	private String getSerialNumber(char houseCode, int unitCode) {
		return getSerialNumber("" + houseCode + unitCode);
	}

	private String getSerialNumber(String address) {
		return "X10." + address;
	}

}
