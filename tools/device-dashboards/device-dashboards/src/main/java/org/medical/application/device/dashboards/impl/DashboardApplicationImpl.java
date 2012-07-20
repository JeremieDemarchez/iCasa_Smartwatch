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
package org.medical.application.device.dashboards.impl;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Pane;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.Window;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.layout.SplitPaneLayoutData;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.medical.application.Application;
import org.medical.application.ApplicationManager;
import org.medical.application.ApplicationTracker;
import org.medical.application.device.dashboards.impl.component.DashboardActionPane;
import org.medical.application.device.dashboards.impl.component.SelectAppPane;
import org.medical.application.device.web.common.impl.BaseHouseApplication;
import org.medical.application.device.web.common.impl.component.HousePane;
import org.medical.application.device.web.common.widget.DeviceWidgetFactory;
import org.medical.application.device.web.common.widget.DeviceWidgetFactorySelector;
import org.medical.common.StateVariable;
import org.medical.common.StateVariableListener;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.DependRegistration;
import org.medical.device.manager.Device;
import org.medical.device.manager.DeviceDependencies;
import org.medical.device.manager.DeviceManager;
import org.medical.device.manager.Service;
import org.osgi.framework.BundleContext;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
@Component(name = "DashboardApplication", immediate = true)
@Provides
public class DashboardApplicationImpl extends BaseHouseApplication implements ApplicationTracker, StateVariableListener {

	/**
	 * @generated
	 */
	private static final long serialVersionUID = -2887321216032546523L;

	@Requires
	private ApplicationManager m_appMgr;
	
	@Requires
	private DeviceManager m_deviceMgr;
	
	private DependRegistration _devDepReg;
	
	private SelectAppPane m_selectAppPane;
	
	/**
	 * A map containing all bind devices
	 */
	private Map<String, ApplicationDevice> devices;
	
	
	public DashboardApplicationImpl(BundleContext context) {
		super(context);
	}


	@Bind(id = "devices", aggregate = true, optional = true)
	public void bindDevice(final ApplicationDevice device, final Map<String, Object> properties) {
		device.addVariableListener(this);
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				devices.put(device.getId(), device);
				DashboardDeviceController controller = (DashboardDeviceController) getDeviceController();
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
				devices.remove(device.getId());
				DashboardDeviceController controller = (DashboardDeviceController) getDeviceController();
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
		DeviceDependencies devDep = new DeviceDependencies().requiresAll().optional().exportsTo(GenericDevice.class);
		
		devDep.includes().all();
		_devDepReg = m_deviceMgr.addDependencies(devDep);
		
		m_appMgr.addApplicationListener(this);
	}

	@Invalidate
	public void stop() {
		super.stop();
		if (_devDepReg != null) {
			_devDepReg.unregister();
			_devDepReg = null;
		}
		m_appMgr.removeApplicationListener(this);
	}
	
	// ---- Component inherited methods ---- //
	
	
	@Override
	protected void initContent() {
	   super.initContent();
	   
		// Create the map with devices
		devices = new HashMap<String, ApplicationDevice>();
		
		// Create the house pane.
		m_housePane = new HousePane(this);

		// Create the status pane.
		m_statusPane = new ContentPane();

		// Create the action pane.
		m_actionPane = new DashboardActionPane(this);
		
		//Create the device controller
		m_DeviceController = new DashboardDeviceController(getSimulationManager());
		m_DeviceController.setDevicePane(m_actionPane.getDevicePane());

		// Create a panel where user can select a digital service
		m_selectAppPane = new SelectAppPane(this);

		// add listeners
		m_selectAppPane.addSelectedApplicationTracker((DashboardActionPane)m_actionPane);

		// Create a panel which contains select service panel and action panel
		final SplitPane dashboardPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM, false);
		dashboardPane.setResizable(false);

		SplitPaneLayoutData selectServPaneData = new SplitPaneLayoutData();
		selectServPaneData.setMinimumSize(new Extent(30, Extent.PX));
		selectServPaneData.setMaximumSize(new Extent(30, Extent.PX));
		selectServPaneData.setOverflow(SplitPaneLayoutData.OVERFLOW_HIDDEN);
		m_selectAppPane.setLayoutData(selectServPaneData);

		dashboardPane.add(m_selectAppPane);

		SplitPaneLayoutData actionPaneData = new SplitPaneLayoutData();
		actionPaneData.setMinimumSize(new Extent(200, Extent.PX));
		// actionPaneData.setMaximumSize(new Extent(900 , Extent.PX));
		actionPaneData.setOverflow(SplitPaneLayoutData.OVERFLOW_AUTO);
		m_actionPane.setLayoutData(actionPaneData);

		dashboardPane.add(m_actionPane);

		// Create the top split pane, that contains the house and action panes.
		final SplitPane topPane = new SplitPane(SplitPane.ORIENTATION_HORIZONTAL_RIGHT_LEFT, true);
		topPane.setResizable(true);

		SplitPaneLayoutData data = new SplitPaneLayoutData();
		data.setMinimumSize(new Extent(500, Extent.PX));
		data.setMaximumSize(new Extent(900, Extent.PX));
		data.setOverflow(SplitPaneLayoutData.OVERFLOW_AUTO);
		dashboardPane.setLayoutData(data);

		topPane.add(dashboardPane);

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

	   m_window.setTitle("iCasa Dashboard");
	}
	
	
	@Override
	public WindowPane getDeviceWidget(String deviceSerialNumber) {
		ApplicationDevice applicationDevice = devices.get(deviceSerialNumber);
		if (applicationDevice!=null) {
			GenericDevice genDevice = (GenericDevice) applicationDevice.getDeviceProxy(GenericDevice.class);
			return getDeviceWidget(genDevice);
		}
		return null;
	}
	
	@Override
	public ResourceImageReference getImageForDevice(String deviceSerialNumber) {
	   ApplicationDevice applicationDevice = devices.get(deviceSerialNumber);
	   if (applicationDevice!=null) {
			GenericDevice genDevice = (GenericDevice) applicationDevice.getDeviceProxy(GenericDevice.class);
			return getImageForDevice(genDevice);
		}
	   return null;
	}
	
	@Override
	public void disposeDeviceInstance(String deviceSerialNumber) {
		//Nothing to do in the dash board application
	}
	
	@Override
	public GenericDevice getGenericDeviceBySerialNumber(String deviceSerialNumber) {
		ApplicationDevice device = devices.get(deviceSerialNumber);
		GenericDevice genericDevice = (GenericDevice) device.getDeviceProxy(GenericDevice.class);
	   return genericDevice;
	}
	
	// ---- Component Business Methods ---- //
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		@SuppressWarnings("unused")
		Object newVal = evt.getNewValue();
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
			final ApplicationDevice dev = devices.get(devId);
			enqueueTask(new Runnable() {
				@Override
				public void run() {
					DashboardDeviceController controller = (DashboardDeviceController) getDeviceController();
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
	
	public Application getApplication(String appId) {
		return m_appMgr.getApplication(appId);
	}


	public Application getSelectedApplication() {
		return m_selectAppPane.getSelectedApplication();
   }


	@Override
   protected void allDevicePropertiesChanged(DeviceWidgetFactory portletFactory) {
		Collection<ApplicationDevice> myDevices = devices.values();
		for (ApplicationDevice appDevice : myDevices) {
			GenericDevice genericDevice = (GenericDevice) appDevice.getDeviceProxy(GenericDevice.class);
			if (portletFactory.typeIsSupported(genericDevice)) {
				devicePropertiesChanged(genericDevice.getSerialNumber(), null);
			}
		}		   
   }
	
	public ApplicationDevice getDeviceBySerialNumber(String deviceSerialNumber) {
		return devices.get(deviceSerialNumber);
	}

	
	@Override
   public void addApplication(Application app) {
		((DashboardActionPane)m_actionPane).addApplication(app);
		m_selectAppPane.addApplication(app);	   
   }


	@Override
   public void removeApplication(Application app) {
		((DashboardActionPane)m_actionPane).removeApplication(app);
		m_selectAppPane.removeApplication(app);	   
   }
}
