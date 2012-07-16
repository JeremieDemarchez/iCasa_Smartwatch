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
package org.medical.application.device.web.common.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Pane;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.TaskQueueHandle;
import nextapp.echo.app.Window;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.layout.SplitPaneLayoutData;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.StyleSheetLoader;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Pojo;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.medical.application.device.web.common.impl.component.ActionPane;
import org.medical.application.device.web.common.impl.component.HousePane;
import org.medical.application.device.web.common.portlet.DeviceWidgetFactory;
import org.medical.application.device.web.common.portlet.DeviceWidgetFactorySelector;
import org.medical.device.manager.ApplicationDevice;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.DevicePositionListener;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;


public abstract class MedicalHouseSimulatorImpl extends ApplicationInstance implements DevicePositionListener,
      PropertyChangeListener, MedicalWebApplication {

	/**
	 * @generated
	 */
	private static final long serialVersionUID = -2887321216032546523L;

	private static final int MARGIN = 10;
	
	/**
	 * The simulation manager service
	 */
	private SimulationManager m_manager;
	
	/**
	 * The widget factory selector (selects the rigth incon and window)
	 */
	private DeviceWidgetFactorySelector m_widgetFactorySelector;

	/**
	 * The house image path
	 */
	private String houseImage;

	/**
	 * The user image path
	 */
	private String userImage;

	
	//This variable must be changed, is used to "glue" code into the application
	/**
	 * Home type
	 */
	private String homeType;
	

	/**
	 * (Left) Pane where are the house map and graphical icons for devices
	 */
	private HousePane m_housePane;

	/**
	 * (Right) pane that contains the device pane and others
	 */
	private ActionPane m_actionPane;

	/**
	 * (Bottom) pane that contains device widgets (specific windows)
	 */
	private ContentPane m_statusPane;
	
	/**
	 * Task queue used in echo3 apps 
	 */
	private TaskQueueHandle m_taskQueueHandle;

	/**
	 * The main application window
	 */
	protected Window m_window;

	/**
	 * Bundle context
	 */
	private final BundleContext m_context;
	
	/**
	 * Device controller instance
	 */
	protected DeviceController m_DeviceController;

	//TODO: See this map 
	/**
	 * A map containing all bind devices
	 */
	private Map<String, ApplicationDevice> devices;

	/**
	 * List of existing widgets factories
	 */
	private List<DeviceWidgetFactory> widgetFactories;

	/**
	 * Indicates if there is a android client using the application 
	 */
	private Boolean isAndroid;
	
	/**
	 * Indicates if is a simulator or dashboard application instance
	 */
	private Boolean isSimulator;
	
	
	private static Bundle _bundle;
	
	/**
	 * Default constructor
	 * @param context the bundle context
	 */
	public MedicalHouseSimulatorImpl(BundleContext context) {
		m_context = context;
		_bundle = context.getBundle();
	}
	
	/**
	 * Initializes the GUI applications
	 */
	protected void initContent() {
		m_taskQueueHandle = createTaskQueue();
		// Set the stylesheet.
		try {
			setStyleSheet(StyleSheetLoader.load("/Stylesheet.xml", MedicalHouseSimulatorImpl.class.getClassLoader()));
		} catch (final SerialException e) {
			// m_logger.warning("Cannot load stylesheet", e);
			// Ignore style shit!
		}

		// Create the map with devices
		devices = new HashMap<String, ApplicationDevice>();

		// Create the list with porlet factories
		widgetFactories = new ArrayList<DeviceWidgetFactory>();
		
		
		// Create the house pane.
		m_housePane = new HousePane(this);

		// Create the status pane.
		m_statusPane = new ContentPane();

		// Create the action pane.
		m_actionPane = createActionPane();

		//Create the device controller
		m_DeviceController = createDeviceController();
		m_DeviceController.setDevicePane(m_actionPane.getDevicePane());
		

		SplitPaneLayoutData actionPaneData = new SplitPaneLayoutData();
		actionPaneData.setMinimumSize(new Extent(200, Extent.PX));
		actionPaneData.setMaximumSize(new Extent(900 , Extent.PX));
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
	}

	/**
	 * In this method must be created the action pane
	 * @return
	 */
	protected abstract ActionPane createActionPane();
	

	
	@Override
	public Window init() {
		return m_window;
	}
	
	

	@Override
	public void dispose() {
		// Destroy the task queue.
		removeTaskQueue(m_taskQueueHandle);
		super.dispose();
		//m_controller.dispose();
	}
	
	public static Bundle getBundle() {
		return _bundle;
	}

	@Validate
	public void start() {
		

		initContent();
		m_housePane.addPropertyChangeListener(this);
		m_manager.addDevicePositionListener(this);
		
		
		
		/*
		// Get the script file list
		final List<String> scriptList = m_ScriptExecutor.getScriptList();
		
		
		enqueueTask(new Runnable() {

			@Override
			public void run() {
				scriptFileList = new DefaultListModel(scriptList.toArray());
				m_actionPane.updateScriptList();
			}
		});
		
		*/
		
	}

	
	public void stop() {
		m_housePane.removePropertyChangeListener(this);
		m_manager.removeDevicePositionListener(this);

	}
	
	/*
	
	@Bind(id = "devices", aggregate = true, optional = true)
	public void bindDevice(final ApplicationDevice device, final Map<String, Object> properties) {
		//device.addVariableListener(this);
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				devices.put(device.getId(), device);				
				m_actionPane.addDevice(device, properties);
			}
		});
	}

	@Unbind(id = "devices")
	public void unbindDevice(final ApplicationDevice device) {
		//device.removeVariableListener(this);
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				devices.remove(device.getId());
				m_actionPane.removeDevice(device);
			}
		});
	}

	*/


	protected void bindPortletFactory(final DeviceWidgetFactory portletFactory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				widgetFactories.add(portletFactory);
				allDevicePropertiesChanged(portletFactory);
			}
		});
	}


	protected void unbindPortletFactory(final DeviceWidgetFactory portletFactory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				widgetFactories.remove(portletFactory);
				allDevicePropertiesChanged(portletFactory);
			}
		});
	}
	
	@Bind(id = "portletfactorySelector", aggregate = false, optional = true)
	public void bindPortletFactorySelector(DeviceWidgetFactorySelector portletFactorySelector) {
		m_widgetFactorySelector = portletFactorySelector;
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_DeviceController.refreshDeviceWidgets();
				//m_actionPane.refreshDeviceWidgets();
			}
		});
	}
	
	@Unbind(id = "portletfactorySelector")
	public void unbindPortletFactorySelector(DeviceWidgetFactorySelector portletFactorySelector) {
		m_widgetFactorySelector = null;
	}

	/*
	public Application getSelectedApplication() {
		return m_selectAppPane.getSelectedApplication();
	}
	*/

	private void allDevicePropertiesChanged(DeviceWidgetFactory portletFactory) {
		Collection<ApplicationDevice> myDevices = devices.values();
		for (ApplicationDevice appDevice : myDevices) {
			GenericDevice genericDevice = (GenericDevice) appDevice.getDeviceProxy(GenericDevice.class);
			if (portletFactory.typeIsSupported(genericDevice)) {
				devicePropertiesChanged(genericDevice.getSerialNumber(), appDevice, null);
			}
		}
	}
	
	private void devicePropertiesChanged(final String deviceSerialNumber, final ApplicationDevice device,
	      final Map<String, Object> properties) {

		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_DeviceController.changeDevice(deviceSerialNumber, properties);
				//m_actionPane.changeDevice(deviceSerialNumber, device, properties);
				
				//TODO: Implementation
				
			}
		});
	}

	public void disposeDeviceInstance(String serialNumber) {
		ApplicationDevice device = devices.get(serialNumber);
		GenericDevice genDevice = (GenericDevice) device.getAvailableDevice().getDeviceProxy(GenericDevice.class);
		if ((genDevice != null) && (genDevice instanceof Pojo)) {
			Pojo pojo = (Pojo) device;
			pojo.getComponentInstance().dispose();
		}
	}

	public Window getWindow() {
		return m_window;
	}

	public void enqueueTask(final Runnable task) {
		enqueueTask(m_taskQueueHandle, task);
	}

	public void setComponentInstance(final ComponentInstance appInstance) {
		//m_controller = appInstance;
	}

	public final HousePane getHousePane() {
		return m_housePane;
	}
	
	

	public final ContentPane getStatusPane() {
		return m_statusPane;
	}

	public BundleContext getContext() {
		return m_context;
	}


	@Override
	public void devicePositionChanged(final String deviceSerialNumber, final Position position) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_DeviceController.moveDevice(deviceSerialNumber, position);				
				//m_actionPane.moveDevice(deviceSerialNumber, position);
			}
		});
	}

	

   
	

	public ApplicationDevice getDeviceBySerialNumber(String deviceId) {
		return devices.get(deviceId);
	}

	public WindowPane getPortlet(ApplicationDevice applicationDevice) {
		GenericDevice genDevice = (GenericDevice) applicationDevice.getDeviceProxy(GenericDevice.class);
		
		List<String> portletFactoriesStr = getPortletFactoriesIDList(genDevice);
		String selectedPortletFactoryID = m_widgetFactorySelector.selectPortletFactory(genDevice, portletFactoriesStr);
		DeviceWidgetFactory portletFactory = getPortletFactoryByID(selectedPortletFactoryID);
		if (portletFactory != null)
			return portletFactory.createWidget(this, applicationDevice.getId());
		return null;
	}

	public ResourceImageReference getImageForDevice(ApplicationDevice device2) {
		GenericDevice genDevice = (GenericDevice) device2.getDeviceProxy(GenericDevice.class);
		
		List<String> portletFactoriesStr = getPortletFactoriesIDList(genDevice);
		if (m_widgetFactorySelector != null) {
			String selectedPortletFactoryID = m_widgetFactorySelector.selectPortletFactory(genDevice, portletFactoriesStr);
			DeviceWidgetFactory portletFactory = getPortletFactoryByID(selectedPortletFactoryID);
			if (portletFactory != null)
				return portletFactory.getDeviceIcon(genDevice);
		}
		return null;
	}

	private List<String> getPortletFactoriesIDList(GenericDevice device) {
		List<String> portletFactoriesStr = new ArrayList<String>();
		for (DeviceWidgetFactory portletFactory : widgetFactories) {
			if (portletFactory.typeIsSupported(device)) {
				portletFactoriesStr.add(portletFactory.getID());
			}
		}
		return portletFactoriesStr;
	}

	private DeviceWidgetFactory getPortletFactoryByID(String ID) {
		for (DeviceWidgetFactory portletFactory : widgetFactories) {
			if (portletFactory.getID().equals(ID))
				return portletFactory;
		}
		return null;
	}


	public void setHouseImage(String houseImage) {
		this.houseImage = houseImage;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getHouseImage() {
		return houseImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}
	
	/**
	 * @return the userImage
	 */
	public String getUserImage() {
		return userImage;
	}

	public void setHomeType(String homeType) {
		this.homeType = homeType;
	}
	
	/**
	 * @return the homeType
	 */
	public String getHomeType() {
		return homeType;
	}

	public boolean isAndroid() {
		return isAndroid;
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		@SuppressWarnings("unused")
		Object newVal = evt.getNewValue();

	}
	
	/**
	 * 
	 * @return the simulation manager
	 */
	public SimulationManager getSimulationManager() {
		return m_manager;
	}


	public void setIsAndroid(Boolean isAndroid) {
		this.isAndroid = isAndroid;
   }

	protected void bindSimulationManager(SimulationManager simulationManager) {
	   m_manager = simulationManager;	   
   }


	protected void unbindSimulationManager(SimulationManager simulationManager) {
		m_manager = null;	   
   }

	
	@Override
	public ApplicationInstance getApplicationInstance() {
		return this;
	}
	
	/***
	 * 
	 * @return the device Map
	 */
	public Map<String, ApplicationDevice> getDevicesMap() {
		return devices;
	}
	
	/**
	 * In this method must be created an instance of DeviceController class
	 * @return
	 */
	public abstract DeviceController createDeviceController();
	
	/**
	 * 
	 * @return the device controller
	 */
	public DeviceController getDeviceController() {
		return m_DeviceController;
	}

	/**
    * @return the actionPane
    */
   public ActionPane getActionPane() {
   	return m_actionPane;
   }
	

}
