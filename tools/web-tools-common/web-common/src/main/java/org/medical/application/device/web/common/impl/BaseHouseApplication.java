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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.TaskQueueHandle;
import nextapp.echo.app.Window;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.StyleSheetLoader;

import org.medical.application.device.web.common.impl.component.ActionPane;
import org.medical.application.device.web.common.impl.component.HousePane;
import org.medical.application.device.web.common.widget.DeviceWidgetFactory;
import org.medical.application.device.web.common.widget.DeviceWidgetFactorySelector;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.DevicePositionListener;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;

public abstract class BaseHouseApplication extends ApplicationInstance implements DevicePositionListener,
      PropertyChangeListener {

	/**
	 * @generated
	 */
	private static final long serialVersionUID = -2887321216032546523L;

	protected static final int MARGIN = 10;

	/**
	 * The simulation manager service
	 */
	private SimulationManager m_manager;

	/**
	 * The widget factory selector (selects the right icon and window)
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

	// TODO: This variable must be changed, is used to "glue" code into the application
	/**
	 * Home type
	 */
	private String homeType;

	/**
	 * (Left) Pane where are the house map and graphical icons for devices
	 */
	protected HousePane m_housePane;

	/**
	 * (Right) pane that contains the device pane and others
	 */
	protected ActionPane m_actionPane;

	/**
	 * (Bottom) pane that contains device widgets (specific windows)
	 */

	protected ContentPane m_statusPane;

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

	/**
	 * List of existing widgets factories
	 */
	private List<DeviceWidgetFactory> widgetFactories;

	/**
	 * Indicates if there is a android client using the application
	 */
	private Boolean isAndroid;

	
	private static Bundle _bundle;

	/**
	 * Default constructor
	 * 
	 * @param context
	 *           the bundle context
	 */
	public BaseHouseApplication(BundleContext context) {
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
			setStyleSheet(StyleSheetLoader.load("/Stylesheet.xml", BaseHouseApplication.class.getClassLoader()));
		} catch (final SerialException e) {
			// m_logger.warning("Cannot load stylesheet", e);
		}

		// Create the list with widgets factories
		widgetFactories = new ArrayList<DeviceWidgetFactory>();
	}


	@Override
	public Window init() {
		return m_window;
	}

	@Override
	public void dispose() {
		// Destroy the task queue.
		removeTaskQueue(m_taskQueueHandle);
		super.dispose();
	}
	
	/**
	 * 
	 * @return the bundle
	 */
	public static Bundle getBundle() {
		return _bundle;
	}

	/**
	 * Life cycle method, it must be invoked when the component is validated
	 */
	public void start() {
		initContent();
		m_housePane.addPropertyChangeListener(this);
		m_manager.addDevicePositionListener(this);
	}

	/**
	 * Life cycle method, it must be invoked when the component is invalidated
	 */
	public void stop() {
		m_housePane.removePropertyChangeListener(this);
		m_manager.removeDevicePositionListener(this);

	}
	
	/**
	 * Binds a WidgetFactory instance
	 * @param widgetFactory the WidgetFactory instance
	 */
	protected void bindPortletFactory(final DeviceWidgetFactory widgetFactory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				widgetFactories.add(widgetFactory);
				allDevicePropertiesChanged(widgetFactory);
			}
		});
	}

	/**
	 * Removes a WidgetFactory instance
	 * @param widgetFactory the WidgetFactory instance
	 */
	protected void unbindPortletFactory(final DeviceWidgetFactory widgetFactory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				widgetFactories.remove(widgetFactory);
				allDevicePropertiesChanged(widgetFactory);
			}
		});
	}

	
	/**
	 * Binds a WidgetFactorySelector instance
	 * @param widgetFactorySelector the WidgetFactorySelector instance
	 */
	protected void bindWidgetFactorySelector(DeviceWidgetFactorySelector widgetFactorySelector) {
		m_widgetFactorySelector = widgetFactorySelector;
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_DeviceController.refreshDeviceWidgets();
				// m_actionPane.refreshDeviceWidgets();
			}
		});
	}

	/**
	 * Removes a WidgetFactorySelector instance
	 * @param widgetFactorySelector the WidgetFactorySelector instance
	 */
	protected void unbindWidgetFactorySelector(DeviceWidgetFactorySelector widgetFactorySelector) {
		m_widgetFactorySelector = null;
	}

	/**
	 * 
	 * @return the main application window
	 */
	public Window getWindow() {
		return m_window;
	}

	/**
	 * Adds a new task to the Application using the mechanism impose by echo3 
	 * @param task
	 */
	public void enqueueTask(final Runnable task) {
		enqueueTask(m_taskQueueHandle, task);
	}

	/**
	 * 
	 * @return the house pane
	 */
	public final HousePane getHousePane() {
		return m_housePane;
	}

	/**
	 * 
	 * @return the status pane
	 */
	public final ContentPane getStatusPane() {
		return m_statusPane;
	}

	/**
	 * 
	 * @return the bundle context
	 */
	public BundleContext getContext() {
		return m_context;
	}

	@Override
	public void devicePositionChanged(final String deviceSerialNumber, final Position position) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_DeviceController.moveDevice(deviceSerialNumber, position);
			}
		});
	}

	/**
	 * Sets the house image
	 * @param houseImage
	 */
	public void setHouseImage(String houseImage) {
		this.houseImage = houseImage;
	}

	/**
	 * 
	 * @return the house image
	 */
	public String getHouseImage() {
		return houseImage;
	}

	/**
	 * Sets the user image
	 * @param userImage
	 */
	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	/**
	 * @return the userImage
	 */
	public String getUserImage() {
		return userImage;
	}

	/**
	 * Sets the home type
	 * @param homeType
	 */
	public void setHomeType(String homeType) {
		this.homeType = homeType;
	}

	/**
	 * @return the homeType
	 */
	public String getHomeType() {
		return homeType;
	}

	/**
	 * Determines if application is being executed in a Android client
	 * @return
	 */
	public boolean isAndroid() {
		return isAndroid;
	}

	/**
	 * Sets the isAndroid parameter
	 * @param isAndroid
	 */
	public void setIsAndroid(Boolean isAndroid) {
		this.isAndroid = isAndroid;
	}

	/**
	 * Binds a SimulationManager instance
	 * @param simulationManager the SimulationManager instance
	 */
	protected void bindSimulationManager(SimulationManager simulationManager) {
		m_manager = simulationManager;
	}

	/**
	 * Removes a SimulationManager instance
	 * @param simulationManager the SimulationManager instance
	 */
	protected void unbindSimulationManager(SimulationManager simulationManager) {
		m_manager = null;
	}

	
	/**
	 * 
	 * @return the simulation manager
	 */
	public SimulationManager getSimulationManager() {
		return m_manager;
	}
	
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

	
	// ---- Some business methods ---- //
	
	
	/**
	 * Gets the details window for a GenericDevice (using the WidgetFactory)
	 * @param genericDevice
	 * @return
	 */
	protected WindowPane getDeviceWidget(GenericDevice genericDevice) {
		List<String> portletFactoriesStr = getWidgetFactoryIDsList(genericDevice);
		String selectedPortletFactoryID = m_widgetFactorySelector.selectPortletFactory(genericDevice, portletFactoriesStr);
		DeviceWidgetFactory portletFactory = getWidgetFactoryByID(selectedPortletFactoryID);
		if (portletFactory != null)
			return portletFactory.createWidget(this, genericDevice.getSerialNumber());
		return null;
	}

	/**
	 * Gets the icon for a GenericDevice (using the WidgetFactory)
	 * @param genericDevice
	 * @return
	 */
	protected ResourceImageReference getImageForDevice(GenericDevice genericDevice) {
		List<String> portletFactoriesStr = getWidgetFactoryIDsList(genericDevice);
		if (m_widgetFactorySelector != null) {
			String selectedWidgetFactoryID = m_widgetFactorySelector.selectPortletFactory(genericDevice, portletFactoriesStr);
			DeviceWidgetFactory widgetFactory = getWidgetFactoryByID(selectedWidgetFactoryID);
			if (widgetFactory != null)
				return widgetFactory.getDeviceIcon(genericDevice);
		}
		return null;
	}
	
	/**
	 * Gets the list of WidgetFactory that match a Generic Device
	 * @param device the Generic Device
	 * @return A list with ids of WidgetFactory
	 */
	private List<String> getWidgetFactoryIDsList(GenericDevice device) {
		List<String> portletFactoriesStr = new ArrayList<String>();
		for (DeviceWidgetFactory portletFactory : widgetFactories) {
			if (portletFactory.typeIsSupported(device)) {
				portletFactoriesStr.add(portletFactory.getID());
			}
		}
		return portletFactoriesStr;
	}


	/**
	 * Gets a WidgetFactory using its ID
	 * @param ID WidgetFactory ID
	 * @return Matching WidgetFactory
	 */
	private DeviceWidgetFactory getWidgetFactoryByID(String ID) {
		for (DeviceWidgetFactory portletFactory : widgetFactories) {
			if (portletFactory.getID().equals(ID))
				return portletFactory;
		}
		return null;
	}
	
	/**
	 * It must invoked when device properties have been changed	
	 * @param deviceSerialNumber the device serial number
	 * @param properties the new properties
	 */
	protected void devicePropertiesChanged(final String deviceSerialNumber, final Map<String, Object> properties) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_DeviceController.changeDevice(deviceSerialNumber, properties);
			}
		});
	}
	
	// ---- Abstract Methods ---- //
	

	/**
	 * Returns a window showing details of the device
	 * @param deviceSerialNumber
	 * @return
	 */
	public abstract WindowPane getDeviceWidget(String deviceSerialNumber);
	
	/**
	 * Returns an icon associated with the device
	 * @param deviceSerialNumber
	 * @return
	 */
	public abstract ResourceImageReference getImageForDevice(String deviceSerialNumber);
	
	/**
	 * Returns an instance of a GenericDevice representation of the device
	 * @param deviceSerialNumber
	 * @return
	 */
	public abstract GenericDevice getGenericDeviceBySerialNumber(String deviceSerialNumber);
	


	protected abstract void allDevicePropertiesChanged(DeviceWidgetFactory portletFactory);
}
