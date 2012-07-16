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

import nextapp.echo.app.ApplicationInstance;

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
import org.medical.application.device.web.common.impl.DeviceController;
import org.medical.application.device.web.common.impl.MedicalHouseSimulatorImpl;
import org.medical.application.device.web.common.impl.MedicalWebApplication;
import org.medical.application.device.web.common.impl.component.ActionPane;
import org.medical.application.device.web.common.portlet.DeviceWidgetFactory;
import org.medical.application.device.web.common.portlet.DeviceWidgetFactorySelector;
import org.medical.clock.api.Clock;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.Device;
import org.medical.device.manager.Service;
import org.medical.script.executor.ScriptExecutor;
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
@Component(name = "WebHouseSimulatorNew", immediate = true)
@Provides
public class HouseSimulatorGUIImpl extends MedicalHouseSimulatorImpl implements UserPositionListener, StateVariableListener {

	/**
	 * @generated
	 */
	private static final long serialVersionUID = -2887321216032546523L;

	@Requires
	private ScriptExecutor m_ScriptExecutor;

	@Requires
	private ScenarioInstaller m_ScenarioInstaller;

	
	public HouseSimulatorGUIImpl(BundleContext context) {
		super(context);
	}


	// ---- Component dependencies methods ---- //

	@Bind(id = "clock", optional = true)
	public void bindClock(final Clock clock) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				SimulatorActionPane actionPane = (SimulatorActionPane) getActionPane();
				actionPane.setClock(clock);
			}
		});
	}

	@Unbind(id = "clock")
	public void unbindClock(final Clock clock) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				SimulatorActionPane actionPane = (SimulatorActionPane) getActionPane();
				actionPane.setClock(null);
			}
		});
	}

	/*
	@Bind(id = "scenarioInstaller")
	public void bindScenarioInstaller(ScenarioInstaller scenarioInstaller) {
		m_ScenarioInstaller = scenarioInstaller;
	}

	@Unbind(id = "scenarioInstaller")
	public void unbindScenarioInstaller(ScenarioInstaller scenarioInstaller) {
		m_ScenarioInstaller = null;
	}
	*/

	@Bind(id = "deviceFactories", aggregate = true, optional = true, filter = "(component.providedServiceSpecifications=fr.liglab.adele.icasa.environment.SimulatedDevice)")
	public void bindDeviceFactory(final Factory factory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				SimulatorActionPane actionPane = (SimulatorActionPane) getActionPane();
				actionPane.addDeviceFactory(factory);
			}
		});
	}

	@Unbind(id = "deviceFactories")
	public void unbindDeviceFactory(final Factory factory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				SimulatorActionPane actionPane = (SimulatorActionPane) getActionPane();
				actionPane.removeDeviceFactory(factory);
			}
		});
	}

	/*
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
	*/

	@Bind(id = "devices", aggregate = true, optional = true)
	public void bindDevice(final ApplicationDevice device, final Map<String, Object> properties) {
		device.addVariableListener(this);
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				//devices.put(device.getId(), device);				
				//m_actionPane.addDevice(device, properties);
				getDevicesMap().put(device.getId(), device);
				SimulatorDeviceController controller = (SimulatorDeviceController) getDeviceController();
				controller.addDevice(device, properties);
			}
		});
	}

	@Unbind(id = "devices")
	public void unbindDevice(final ApplicationDevice device) {
		device.removeVariableListener(this);
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				//devices.remove(device.getId());
				//m_actionPane.removeDevice(device);
				getDevicesMap().remove(device.getId());
				SimulatorDeviceController controller = (SimulatorDeviceController) getDeviceController();
				controller.removeDevice(device);
			}
		});
	}
	
	@Override
	@Bind(id = "simulationManager")
	public void bindSimulationManager(final SimulationManager simulationManager) {
		super.bindSimulationManager(simulationManager);
	}

	@Override
	@Unbind(id = "simulationManager")
	public void unbindSimulationManager(final SimulationManager simulationManager) {
		super.unbindSimulationManager(simulationManager);
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

	@Override
	public void userPositionChanged(final String userName, final Position position) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				SimulatorActionPane actionPane = (SimulatorActionPane) getActionPane();
				actionPane.moveUser(userName, position);
			}
		});
	}

	// ---- Component properties methods ---- //
	
	@Override
	@Property(name = "houseImage", mandatory = true)
	public void setHouseImage(String houseImage) {
		super.setHouseImage(houseImage);
	}

	@Override
	@Property(name = "userImage", mandatory = true)
	public void setUserImage(String userImage) {
		super.setUserImage(userImage);
	}

	@Override
	@Property(name = "homeType", mandatory = true)
	public void setHomeType(String homeType) {
		super.setHomeType(homeType);
	}


	@Override
	@Property(name = "isAndroid", mandatory = true)
	public void setIsAndroid(Boolean isAndroid) {
		super.setIsAndroid(isAndroid);
	}

	
	// ---- Component Life cycle methods ---- //
	
	@Validate
	public void start() {
		super.start();
		getSimulationManager().addUserPositionListener(this);
	}

	@Invalidate
	public void stop() {
		super.stop();
		getSimulationManager().removeUserPositionListener(this);
	}
	
	// ---- Component inherited methods ---- //
	
	@Override
	protected ActionPane createActionPane() {
		return new SimulatorActionPane(this);
	}
	
	@Override
	protected void initContent() {
	   super.initContent();
	   m_window.setTitle("iCasa Simulator Platform ");
	}
	
	// ---- Component Business Methods ---- //
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		@SuppressWarnings("unused")
		Object newVal = evt.getNewValue();
	}

	/**
	 * Saves the current simulation scenario
	 */
	public void saveSimulationEnvironment() {
		SimulationManager simulationManager = getSimulationManager();
		Set<String> envs = simulationManager.getEnvironments();

		Set<String> devices = simulationManager.getDevices();

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

				Zone zone = simulationManager.getEnvironmentZone(environment);
				out.println("\t position = " + zone.leftX + " " + zone.topY + " " + zone.rightX + " " + zone.bottomY);

				for (String device : devices) {
					System.out.println("Device -----> " + device);
					Position position = simulationManager.getDevicePosition(device);
					String deviceEnv = simulationManager.getEnvironmentFromPosition(position);
					if (deviceEnv.equals(environment)) {
						String deviceLine = "\t device " + "\"" + device + "\" : \"";

						try {
							ServiceReference[] references = getContext().getServiceReferences(
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


	@Override
   public DeviceController createDeviceController() {
		return new SimulatorDeviceController(getSimulationManager());
   }





	@Override
	public void notifValueChange(StateVariable variable, Object oldValue,
			Object sourceObject) {
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
					SimulatorDeviceController controller = (SimulatorDeviceController) getDeviceController();
					controller.changeDevice(dev.getId(), Collections.EMPTY_MAP);
				}
			});
		}	   
   }

	@Override
   public void addVariable(StateVariable arg0, Object arg1) {
	   // TODO Auto-generated method stub
	   
   }

	@Override
   public void removeVariable(StateVariable arg0, Object arg1) {
	   // TODO Auto-generated method stub
	   
   }

}
