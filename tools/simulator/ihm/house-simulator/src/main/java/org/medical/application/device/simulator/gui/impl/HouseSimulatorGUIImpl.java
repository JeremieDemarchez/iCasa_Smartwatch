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
package org.medical.application.device.simulator.gui.impl;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.medical.application.device.simulator.gui.impl.component.SimulatorActionPane;
import org.medical.application.device.web.common.impl.MedicalHouseSimulatorImpl;
import org.medical.application.device.web.common.impl.component.ActionPane;
import org.medical.application.device.web.common.impl.component.HousePane;
import org.medical.application.device.web.common.portlet.DeviceWidgetFactory;
import org.medical.application.device.web.common.portlet.DeviceWidgetFactorySelector;
import org.medical.clock.api.Clock;
import org.medical.common.StateVariable;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.Device;
import org.medical.device.manager.Service;
import org.medical.script.executor.ScriptExecutor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import fr.liglab.adele.icasa.environment.SimulatedDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;
import fr.liglab.adele.icasa.environment.SimulationManager.UserPositionListener;
import fr.liglab.adele.icasa.environment.SimulationManager.Zone;
import fr.liglab.adele.icasa.script.ScenarioInstaller;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
@Component(name = "WebHouseSimulatorNew")
@Provides
public class HouseSimulatorGUIImpl extends MedicalHouseSimulatorImpl implements UserPositionListener {

	/**
	 * @generated
	 */
	private static final long serialVersionUID = -2887321216032546523L;

	private static final int MARGIN = 10;


	@Requires
	private SimulationManager m_manager;

	@Requires
	private ScriptExecutor m_ScriptExecutor;

	private ScenarioInstaller m_ScenarioInstaller;

	@Property(name = "isAndroid", mandatory = true)
	private String isAndroidStr;

	@Property(name = "isSimulator", mandatory = true)
	private Boolean isSimulator;

	private SimulatorActionPane m_actionPane;

	private final BundleContext m_context;

	private Boolean isAndroid;

	public HouseSimulatorGUIImpl(BundleContext context) {
		super(context);
		m_context = context;
	}



	@Validate
	public void start() {
		super.start();
		m_manager.addUserPositionListener(this);

	}

	@Invalidate
	public void stop() {
		super.stop();
		m_manager.removeUserPositionListener(this);
	}

	@Bind(id = "clock", optional = true)
	public void bindClock(final Clock clock) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_actionPane.setClock(clock);
			}
		});
	}

	@Unbind(id = "clock")
	public void unbindClock(final Clock clock) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_actionPane.setClock(null);
			}
		});
	}

	@Bind(id = "scenarioInstaller")
	public void bindScenarioInstaller(ScenarioInstaller scenarioInstaller) {
		m_ScenarioInstaller = scenarioInstaller;
	}

	@Unbind(id = "scenarioInstaller")
	public void unbindScenarioInstaller(ScenarioInstaller scenarioInstaller) {
		m_ScenarioInstaller = null;
	}

	@Bind(id = "deviceFactories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.environment.SimulatedDevice)")
	public void bindDeviceFactory(final Factory factory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_actionPane.addDeviceFactory(factory);
			}
		});
	}

	@Unbind(id = "deviceFactories")
	public void unbindDeviceFactory(final Factory factory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_actionPane.removeDeviceFactory(factory);
			}
		});
	}

	@Override
	@Bind(id = "devices", aggregate = true, optional = true)
	public void bindDevice(final ApplicationDevice device, final Map<String, Object> properties) {
		super.bindDevice(device, properties);
	}

	@Override
	@Unbind(id = "devices")
	public void unbindDevice(final ApplicationDevice device) {
		super.unbindDevice(device);
	}

	@Override
	@Bind(id = "portletfactory", aggregate = true, optional = true)
	public void bindPortletFactory(final DeviceWidgetFactory portletFactory) {
		super.bindPortletFactory(portletFactory);
	}

	@Unbind(id = "portletfactory")
	public void unbindPortletFactory(final DeviceWidgetFactory portletFactory) {
		super.unbindPortletFactory(portletFactory);
	}

	@Bind(id = "portletfactorySelector", aggregate = false, optional = true)
	public void bindPortletFactorySelector(DeviceWidgetFactorySelector portletFactorySelector) {
		super.bindPortletFactorySelector(portletFactorySelector);
	}

	@Unbind(id = "portletfactorySelector")
	public void unbindPortletFactorySelector(DeviceWidgetFactorySelector portletFactorySelector) {
		super.unbindPortletFactorySelector(portletFactorySelector);
	}

	/*
	 * private void allDevicePropertiesChanged(DeviceWidgetFactory
	 * portletFactory) {
	 * 
	 * Collection<ApplicationDevice> myDevices = devices.values(); for
	 * (ApplicationDevice appDevice : myDevices) { GenericDevice genericDevice =
	 * (GenericDevice) appDevice.getDeviceProxy(GenericDevice.class); if
	 * (portletFactory.typeIsSupported(genericDevice)) {
	 * devicePropertiesChanged(genericDevice.getSerialNumber(), appDevice, null);
	 * } } }
	 */

	/*
	 * public void disposeDeviceInstance(String serialNumber) { ApplicationDevice
	 * device = devices.get(serialNumber); GenericDevice genDevice =
	 * (GenericDevice)
	 * device.getAvailableDevice().getDeviceProxy(GenericDevice.class); if
	 * ((genDevice != null) && (genDevice instanceof Pojo)) { Pojo pojo = (Pojo)
	 * device; pojo.getComponentInstance().dispose(); } }
	 */
	/*
	 * public Window getWindow() { return m_window; }
	 */

	/*
	 * public void enqueueTask(final Runnable task) {
	 * enqueueTask(m_taskQueueHandle, task); }
	 */

	/*
	 * public void setComponentInstance(final ComponentInstance appInstance) {
	 * m_controller = appInstance; }
	 */

	/*
	 * public BundleContext getContext() { return m_context; }
	 */

	/*
	 * public SimulationManager getSimulationManager() { return m_manager; }
	 */

	@Override
	public void userPositionChanged(final String userName, final Position position) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_actionPane.moveUser(userName, position);
			}
		});
	}

	@Override
	public void devicePositionChanged(final String deviceSerialNumber, final Position position) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_actionPane.moveDevice(deviceSerialNumber, position);
			}
		});
	}

	/*
	 * public void devicePropertiesChanged(final String deviceSerialNumber, final
	 * ApplicationDevice device, final Map<String, Object> properties) {
	 * 
	 * enqueueTask(new Runnable() {
	 * 
	 * @Override public void run() {
	 * m_actionPane.changeDevice(deviceSerialNumber, device, properties); } }); }
	 */

	/**
	 * @return the scriptFileList
	 */
	/*
	 * public DefaultListModel getScriptFileList() { return scriptFileList; }
	 */

	public List<String> getScenarioList() {
		return m_ScenarioInstaller.getScenarioList();
	}

	public void installScenario(String scenarioName) {
		try {
			m_ScenarioInstaller.installScenario(scenarioName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getScriptList() {
		return m_ScriptExecutor.getScriptList();
	}

	public void executeScript(String scriptName) {
		m_ScriptExecutor.executeScript(scriptName);
	}

	public void stopScript() {
		m_ScriptExecutor.stopExecution();
	}

	/*
	 * public ApplicationDevice getDeviceBySerialNumber(String deviceId) { return
	 * devices.get(deviceId); }
	 */

	/*
	 * public WindowPane getPortlet(ApplicationDevice applicationDevice) {
	 * GenericDevice genDevice = (GenericDevice)
	 * applicationDevice.getDeviceProxy(GenericDevice.class);
	 * 
	 * List<String> portletFactoriesStr = getPortletFactoriesIDList(genDevice);
	 * String selectedPortletFactoryID =
	 * m_portletFactorySelector.selectPortletFactory(genDevice,
	 * portletFactoriesStr); DeviceWidgetFactory portletFactory =
	 * getPortletFactoryByID(selectedPortletFactoryID); if (portletFactory !=
	 * null) return portletFactory.createWidget(this, applicationDevice.getId());
	 * return null; }
	 */

	/*
	 * public ResourceImageReference getImageForDevice(ApplicationDevice device2)
	 * { GenericDevice genDevice = (GenericDevice)
	 * device2.getDeviceProxy(GenericDevice.class);
	 * 
	 * List<String> portletFactoriesStr = getPortletFactoriesIDList(genDevice);
	 * if (m_portletFactorySelector != null) { String selectedPortletFactoryID =
	 * m_portletFactorySelector.selectPortletFactory(genDevice,
	 * portletFactoriesStr); DeviceWidgetFactory portletFactory =
	 * getPortletFactoryByID(selectedPortletFactoryID); if (portletFactory !=
	 * null) return portletFactory.getDeviceIcon(genDevice); } return null; }
	 */

	/*
	 * private List<String> getPortletFactoriesIDList(GenericDevice device) {
	 * List<String> portletFactoriesStr = new ArrayList<String>(); for
	 * (DeviceWidgetFactory portletFactory : portletFactories) { if
	 * (portletFactory.typeIsSupported(device)) {
	 * portletFactoriesStr.add(portletFactory.getID()); } } return
	 * portletFactoriesStr; }
	 */

	/*
	 * private DeviceWidgetFactory getPortletFactoryByID(String ID) { for
	 * (DeviceWidgetFactory portletFactory : portletFactories) { if
	 * (portletFactory.getID().equals(ID)) return portletFactory; } return null;
	 * }
	 */


	/*
	@Property(name="houseImage")
	@Override
	public String getHouseImage() {
	   return super.getHouseImage();
	}
	*/
	
	@Property(name="houseImage")
	@Override
	public void setHouseImage(String houseImage) {
	   super.setHouseImage(houseImage);
	}
	
	/*
	@Property(name="userImage")
	@Override
	public String getUserImage() {
	   return super.getUserImage();
	}
	*/
	
	@Property(name="userImage")
	@Override
	public void setUserImage(String userImage) {
	   super.setUserImage(userImage);
	}
	
	@Property(name="homeType")
	@Override
	public void setHomeType(String homeType) {
	   super.setHomeType(homeType);
	}


	public boolean isAndroid() {
		if (isAndroid == null) {
			isAndroid = ((isAndroidStr != null) && (Boolean.valueOf(isAndroidStr)));
		}

		return isAndroid;
	}

	public boolean isSimulator() {
		return isSimulator;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		@SuppressWarnings("unused")
		Object newVal = evt.getNewValue();

	}

	/*
	 * public Application getApplication(String appId) { return
	 * m_appMgr.getApplication(appId); }
	 */

	/*
	 * @Override public void addApplication(Application app) {
	 * m_actionPane.addApplication(app); m_selectAppPane.addApplication(app); }
	 * 
	 * @Override public void removeApplication(Application app) {
	 * m_actionPane.removeApplication(app);
	 * m_selectAppPane.removeApplication(app); }
	 */

	/**
	 * Saves the current simulation scenario
	 */
	public void saveSimulationEnvironment() {
		Set<String> envs = m_manager.getEnvironments();

		Set<String> devices = m_manager.getDevices();

		FileWriter outFile;
		PrintWriter out;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy-HHmmss");

			File directory = new java.io.File("scenarios");
			if (!directory.exists())
				directory.mkdir();
			String fileName = "scenarios" + File.separator + "simulation-" + formatter.format(new Date()) + ".icasa";
			outFile = new FileWriter(new File(fileName));
			out = new PrintWriter(outFile);

			for (String environment : envs) {
				out.println("environment " + "\"" + environment + "\" {");

				Zone zone = m_manager.getEnvironmentZone(environment);
				out.println("\t position = " + zone.leftX + " " + zone.topY + " " + zone.rightX + " " + zone.bottomY);

				for (String device : devices) {
					System.out.println("Device -----> " + device);
					Position position = m_manager.getDevicePosition(device);
					String deviceEnv = m_manager.getEnvironmentFromPosition(position);
					if (deviceEnv.equals(environment)) {
						String deviceLine = "\t device " + "\"" + device + "\" : \"";

						try {
							ServiceReference[] references = m_context.getServiceReferences(
							      SimulatedDevice.class.getCanonicalName(), "(device.serialNumber=" + device + ")");
							if (references != null && references.length > 0) {
								ServiceReference reference = references[0];
								deviceLine += reference.getProperty("factory.name") + "\" {";
								out.println(deviceLine);
								out.println("\t\t position = " + position.x + " " + position.y);
								String description = (String) reference.getProperty("service.description");
								if (description != null)
									out.println("\t\t \"service.description\" = \"" + description + "\"");
								String state = (String) reference.getProperty("state");
								if (state != null)
									out.println("\t\t \"state\" = \"" + state + "\"");
							}
						} catch (InvalidSyntaxException e) {
							e.printStackTrace();
						}
						out.println("\t}");
					}
				}
				out.println("}");
				out.println();
			}

			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void addVariable(StateVariable variable, Object sourceObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeVariable(StateVariable variable, Object sourceObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifValueChange(StateVariable variable, Object oldValue, Object sourceObject) {
		String devId = null;
		Object owner = variable.getOwner();
		if (owner instanceof Device) {
			devId = ((Device) owner).getId();
		} else if (owner instanceof Service) {
			devId = ((Service) owner).getDevice().getId();
		}

		if (devId != null) {
			final ApplicationDevice dev = getDeviceBySerialNumber(devId);
			enqueueTask(new Runnable() {
				@Override
				public void run() {
					m_actionPane.changeDevice(dev.getId(), dev, Collections.EMPTY_MAP);
				}
			});
		}
	}

	@Override
   protected ActionPane getActionPane() {
	   return new SimulatorActionPane(this);
   }

}
