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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Pane;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.Window;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.layout.SplitPaneLayoutData;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.Pojo;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.medical.application.device.simulator.gui.impl.component.SimulatorActionPane;
import org.medical.application.device.web.common.impl.BaseHouseApplication;
import org.medical.application.device.web.common.impl.component.HousePane;
import org.medical.application.device.web.common.widget.DeviceWidgetFactory;
import org.medical.application.device.web.common.widget.DeviceWidgetFactorySelector;
import org.medical.clock.api.Clock;
import org.medical.script.executor.ScriptExecutor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import fr.liglab.adele.icasa.device.GenericDevice;
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
@Component(name = "WebHouseSimulator", immediate = true)
@Provides
public class SimulatorApplicationImpl extends BaseHouseApplication implements UserPositionListener {

	/**
	 * @generated
	 */
	private static final long serialVersionUID = -2887321216032546523L;

	@Requires
	private ScriptExecutor m_ScriptExecutor;

	@Requires
	private ScenarioInstaller m_ScenarioInstaller;
	
	private Map<String, GenericDevice> devices;

	private DeviceServiceTracker serviceTracker;
	
	public SimulatorApplicationImpl(BundleContext context) {
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

	@Bind(id = "devices", aggregate = true, optional = true)
	public void bindDevice(final GenericDevice device, final Map<String, Object> properties) {
		//TODO workaround to ignore proxies created by device manager
		if (isDeviceManagerProxy(device))
			return;
		
		devices.put(device.getSerialNumber(), device);
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				SimulatorDeviceController controller = (SimulatorDeviceController) getDeviceController();
				controller.addDevice(device, properties);
			}
		});
	}

	private boolean isDeviceManagerProxy(GenericDevice device) {
		Set<String> interfaces = getInterfaceNames(device.getClass().getInterfaces());
	 	return interfaces.contains("org.medical.device.manager.ApplicationDeviceProxy");
	}
	

	private Set<String> getInterfaceNames(Class[] interfaces) {
		HashSet<String> names = new HashSet<String>();
		for (Class interfaze : interfaces) {
			names.add(interfaze.getCanonicalName());
		}

		return names;
	}

	@Unbind(id = "devices")
	public void unbindDevice(final GenericDevice device) {
		//TODO workaround to ignore proxies created by device manager
		if (isDeviceManagerProxy(device))
			return;
		
		devices.remove(device);
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				SimulatorDeviceController controller = (SimulatorDeviceController) getDeviceController();
				controller.removeDevice(device);
			}
		});
	}

	/*
	 * 
	 * @Bind(id = "devices", aggregate = true, optional = true) public void
	 * bindDevice(final ApplicationDevice device, final Map<String, Object>
	 * properties) { device.addVariableListener(this); enqueueTask(new Runnable()
	 * {
	 * 
	 * @Override public void run() { getDevicesMap().put(device.getId(), device);
	 * SimulatorDeviceController controller = (SimulatorDeviceController)
	 * getDeviceController(); controller.addDevice(device, properties); } }); }
	 * 
	 * @Unbind(id = "devices") public void unbindDevice(final ApplicationDevice
	 * device) { device.removeVariableListener(this); enqueueTask(new Runnable()
	 * {
	 * 
	 * @Override public void run() { getDevicesMap().remove(device.getId());
	 * SimulatorDeviceController controller = (SimulatorDeviceController)
	 * getDeviceController(); controller.removeDevice(device); } }); }
	 */

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
	public void bindWidgetFactorySelector(DeviceWidgetFactorySelector portletFactorySelector) {
		super.bindWidgetFactorySelector(portletFactorySelector);
	}

	@Unbind(id = "portletfactorySelector")
	public void unbindWidgetFactorySelector(DeviceWidgetFactorySelector portletFactorySelector) {
		super.unbindWidgetFactorySelector(portletFactorySelector);
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
		serviceTracker = new DeviceServiceTracker(getContext());
		serviceTracker.open();
	}

	@Invalidate
	public void stop() {
		super.stop();
		getSimulationManager().removeUserPositionListener(this);
		serviceTracker.close();
	}

	// ---- Component inherited methods ---- //

	@Override
	protected void initContent() {
		super.initContent();
		
		// Create the map with devices
		devices = new HashMap<String, GenericDevice>();
		
		// Create the house pane.
		m_housePane = new HousePane(this);

		// Create the status pane.
		m_statusPane = new ContentPane();

		// Create the action pane.
		m_actionPane = new SimulatorActionPane(this);

		// Create the device controller
		m_DeviceController = new SimulatorDeviceController(getSimulationManager());
		m_DeviceController.setDevicePane(m_actionPane.getDevicePane());

		SplitPaneLayoutData actionPaneData = new SplitPaneLayoutData();
		actionPaneData.setMinimumSize(new Extent(200, Extent.PX));
		actionPaneData.setMaximumSize(new Extent(900, Extent.PX));
		actionPaneData.setOverflow(SplitPaneLayoutData.OVERFLOW_AUTO);
		m_actionPane.setLayoutData(actionPaneData);

		// Create the top split pane, that contains the house and action panes.
		final SplitPane topPane = new SplitPane(SplitPane.ORIENTATION_HORIZONTAL_RIGHT_LEFT, true);
		topPane.setResizable(true);

		SplitPaneLayoutData data = new SplitPaneLayoutData();
		data.setMinimumSize(new Extent(500, Extent.PX));
		data.setMaximumSize(new Extent(900, Extent.PX));
		data.setOverflow(SplitPaneLayoutData.OVERFLOW_AUTO);

		topPane.add(m_actionPane);

		data = new SplitPaneLayoutData();
		data.setMinimumSize(new Extent(200, Extent.PX));
		data.setMaximumSize(new Extent(900, Extent.PX));
		data.setOverflow(SplitPaneLayoutData.OVERFLOW_AUTO);
		m_housePane.setLayoutData(data);

		topPane.add(m_housePane);

		// Create the global split pane, that contains the top split pane and
		// the status pane.
		Pane globalPane;
		if (isAndroid()) {
			ContentPane pane = new ContentPane();
			pane.add(topPane);

			globalPane = pane;
		} else {
			SplitPane pane = new SplitPane(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM,
			      new Extent(HousePane.HEIGHT + MARGIN));
			pane.add(topPane);
			pane.add(m_statusPane);

			globalPane = pane;
		}

		// Create the top level window, that contains top split pane and the
		// status pane.
		m_window = new Window();
		m_window.getContent().add((nextapp.echo.app.Component) globalPane);

		m_window.setTitle("iCasa Simulator Platform ");
	}

	@Override
	public WindowPane getDeviceWidget(String deviceSerialNumber) {
		/*
		ApplicationDevice applicationDevice = getDevicesMap().get(deviceSerialNumber);
		if (applicationDevice != null) {
			GenericDevice genDevice = (GenericDevice) applicationDevice.getDeviceProxy(GenericDevice.class);
			return getDeviceWidget(genDevice);
		}
		*/
		GenericDevice genDevice = devices.get(deviceSerialNumber);
		if (genDevice!=null)
			return getDeviceWidget(genDevice);
		return null;
	}

	@Override
	public ResourceImageReference getImageForDevice(String deviceSerialNumber) {
		/*
		ApplicationDevice applicationDevice = getDevicesMap().get(deviceSerialNumber);
		if (applicationDevice != null) {
			GenericDevice genDevice = (GenericDevice) applicationDevice.getDeviceProxy(GenericDevice.class);
			return getImageForDevice(genDevice);
		}
		return null;
		*/
		GenericDevice genDevice = devices.get(deviceSerialNumber);
		if (genDevice!=null)
			return getImageForDevice(genDevice);
		return null;
	}
	
	
	public void disposeDeviceInstance(String deviceSerialNumber) {
		GenericDevice genDevice = devices.get(deviceSerialNumber);
		if ((genDevice != null) && (genDevice instanceof Pojo)) {
			Pojo pojo = (Pojo) genDevice;
			pojo.getComponentInstance().dispose();
		}	   
	}
	
	
	@Override
	public GenericDevice getGenericDeviceBySerialNumber(String deviceSerialNumber) {
	   return devices.get(deviceSerialNumber);
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

	/*
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
					SimulatorDeviceController controller = (SimulatorDeviceController) getDeviceController();
					controller.changeDevice(dev.getId(), Collections.EMPTY_MAP);
				}
			});
		}
	}
	*/

	/*
	@Override
	public void addVariable(StateVariable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeVariable(StateVariable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
   */
	
	class DeviceServiceTracker extends ServiceTracker {

		public DeviceServiceTracker(BundleContext context) {
	      super(context, GenericDevice.class.getCanonicalName(), null);
      }

		@Override
		public void modifiedService(final ServiceReference reference, Object service) {
			final String deviceSerialNumber = (String)reference.getProperty("device.serialNumber");
			enqueueTask(new Runnable() {
				@Override
				public void run() {
					m_DeviceController.changeDevice(deviceSerialNumber, getProperties(reference));
				}
			});
		}
		
		private Map<String, Object> getProperties(ServiceReference reference) {
			Map<String, Object> map = new Hashtable<String, Object>();
			
			String[] keys = reference.getPropertyKeys();
			for (String key : keys) {
	         map.put(key, reference.getProperty(key));
         }
			return map;
		}
		
	}


	@Override
   protected void allDevicePropertiesChanged(DeviceWidgetFactory portletFactory) {
		Collection<GenericDevice> myDevices = devices.values();
		for (GenericDevice genericDevice : myDevices) {			
			if (portletFactory.typeIsSupported(genericDevice)) {
				devicePropertiesChanged(genericDevice.getSerialNumber(), Collections.EMPTY_MAP);
			}
		}	   
   }
}
