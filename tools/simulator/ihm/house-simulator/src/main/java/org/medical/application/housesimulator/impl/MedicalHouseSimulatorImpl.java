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
package org.medical.application.housesimulator.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import nextapp.echo.app.list.DefaultListModel;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.StyleSheetLoader;

import org.apache.felix.ipojo.ComponentInstance;
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
import org.medical.application.housesimulator.impl.component.ActionPane;
import org.medical.application.housesimulator.impl.component.HousePane;
import org.medical.application.housesimulator.impl.component.SelectAppPane;
import org.medical.application.housesimulator.portlet.PortletFactory;
import org.medical.clock.api.Clock;
import org.medical.application.Application;
import org.medical.application.ApplicationTracker;
import org.medical.application.ApplicationManager;
import org.medical.script.executor.ScriptExecutor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.DevicePositionListener;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;
import fr.liglab.adele.icasa.environment.SimulationManager.UserPositionListener;
import fr.liglab.adele.icasa.environment.SimulationManager.Zone;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
@Component(name = "WebHouseSimulator")
@Provides
public class MedicalHouseSimulatorImpl extends ApplicationInstance implements DevicePositionListener,
      UserPositionListener, PropertyChangeListener, ApplicationTracker {

	/**
	 * @generated
	 */
	private static final long serialVersionUID = -2887321216032546523L;

	private static final int MARGIN = 10;

	@Requires
	private SimulationManager m_manager;

	@Requires
	private ScriptExecutor m_ScriptExecutor;
	
	@Requires
	private ApplicationManager m_appMgr;
	
	@Property(name="houseImage")
	private String houseImage;
	
	@Property(name="userImage")
	private String userImage;
	
	@Property(name="homeType")
	private String homeType;

	private HousePane m_housePane;

	private ActionPane m_actionPane;
	
	private SelectAppPane m_selectAppPane;
	
	private ContentPane m_statusPane;

	private TaskQueueHandle m_taskQueueHandle;

	private Window m_window;

	private ComponentInstance m_controller;

	private final BundleContext m_context;

	private DeviceServiceTracker tracker;

	private DefaultListModel scriptFileList;

	private Map<String, GenericDevice> devices;

	private List<PortletFactory> portletFactories;
	
	@Property(name="isAndroid", mandatory = true)
	private String isAndroidStr;
	
	private Boolean isAndroid;

	public MedicalHouseSimulatorImpl(BundleContext context) {
		m_context = context;
		
		initContent();
	}

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
		devices = new HashMap<String, GenericDevice>();

		// Create the list with porlet factories
		portletFactories = new ArrayList<PortletFactory>();

		// Create the script list
		scriptFileList = new DefaultListModel();

		// Create the house pane.
		m_housePane = new HousePane(this);
		
		// Create the status pane.
		m_statusPane = new ContentPane();
		
		// Create the action pane.
		m_actionPane = new ActionPane(this);
		
		// Create a panel where user can select a digital service
		m_selectAppPane = new SelectAppPane(this);
		
		// add listeners
		m_selectAppPane.addSelectedApplicationTracker(m_actionPane);
		
		// Create a panel which contains select service panel and action panel
		final SplitPane dashboardPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM, false); 
		dashboardPane.setResizable(false);
		
		SplitPaneLayoutData selectServPaneData = new SplitPaneLayoutData();
		selectServPaneData.setMinimumSize(new Extent(30 , Extent.PX));
		selectServPaneData.setMaximumSize(new Extent(30 , Extent.PX));
		selectServPaneData.setOverflow(SplitPaneLayoutData.OVERFLOW_HIDDEN);
		m_selectAppPane.setLayoutData(selectServPaneData);
		
		dashboardPane.add(m_selectAppPane);
		
		SplitPaneLayoutData actionPaneData = new SplitPaneLayoutData();
		actionPaneData.setMinimumSize(new Extent(200 , Extent.PX));
//		actionPaneData.setMaximumSize(new Extent(900 , Extent.PX));
		actionPaneData.setOverflow(SplitPaneLayoutData.OVERFLOW_AUTO);
		m_actionPane.setLayoutData(actionPaneData);
		
		dashboardPane.add(m_actionPane);

		// Create the top split pane, that contains the house and action panes.
		final SplitPane topPane = new SplitPane(SplitPane.ORIENTATION_HORIZONTAL_RIGHT_LEFT, true); 
		topPane.setResizable(true);
		
		SplitPaneLayoutData data = new SplitPaneLayoutData();
		data.setMinimumSize(new Extent(500 , Extent.PX));
		data.setMaximumSize(new Extent(900 , Extent.PX));
		data.setOverflow(SplitPaneLayoutData.OVERFLOW_AUTO);
		dashboardPane.setLayoutData(data);
		
		topPane.add(dashboardPane);
		
		data = new SplitPaneLayoutData();
		data.setMinimumSize(new Extent(200 , Extent.PX));
		data.setMaximumSize(new Extent(900 , Extent.PX));
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
			SplitPane pane = new SplitPane(SplitPane.ORIENTATION_VERTICAL_TOP_BOTTOM, new Extent(HousePane.HEIGHT
			      + MARGIN));
			pane.add(topPane);
			pane.add(m_statusPane);
			
			globalPane = pane;
		}
		
		// Create the top level window, that contains top split pane and the
		// status pane.
		m_window = new Window();
		m_window.getContent().add((nextapp.echo.app.Component) globalPane);
		m_window.setTitle("iCASA House Simulator");
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
		m_controller.dispose();
	}

	@Validate
	public void start() {
		m_housePane.addPropertyChangeListener(this);
		
		m_manager.addDevicePositionListener(this);
		m_manager.addUserPositionListener(this);
		
		m_appMgr.addApplicationListener(this);

		// MedicalGenericDevice service tracker
		tracker = new DeviceServiceTracker(m_context);
		tracker.open();
		
		
		//Get the script file list
		final List<String> scriptList = m_ScriptExecutor.getScriptList();
		enqueueTask(new Runnable() {

			@Override
			public void run() {
				scriptFileList = new DefaultListModel(scriptList.toArray());
				m_actionPane.updateScriptList();
			}
		});

	}

	@Invalidate
	public void stop() {
		
		
		m_manager.removeDevicePositionListener(this);
		m_manager.removeUserPositionListener(this);
		m_appMgr.removeApplicationListener(this);

		// GenericDevice service tracker
		tracker.close();
	}

	@Bind(id = "clock", optional = true)
	public void bindClock(final Clock clock) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_actionPane.setClock(clock);
				// m_logger.info("Clock service bound");
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

	@Bind(id = "devices", aggregate = true, optional = true)
	public void bindDevice(final GenericDevice device, final Map<String, Object> properties) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				devices.put(device.getSerialNumber(), device);
				m_actionPane.addDevice(device, properties);
			}
		});
	}

	@Unbind(id = "devices")
	public void unbindDevice(final GenericDevice device) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				devices.remove(device.getSerialNumber());
				m_actionPane.removeDevice(device);
			}
		});
	}

	@Bind(id = "portletfactory", aggregate = true, optional = true)
	public void bindPortletFactory(final PortletFactory portletFactory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {
				portletFactories.add(portletFactory);
				allDevicePropertiesChanged(portletFactory);
			}
		});
	}
	
	@Unbind(id = "portletfactory")
	public void unbindPortletFactory(final PortletFactory portletFactory) {
		enqueueTask(new Runnable() {
			@Override
			public void run() {				
				portletFactories.remove(portletFactory);
				allDevicePropertiesChanged(portletFactory);
			}
		});
	}
	
	public Application getSelectedApplication() {
		return m_selectAppPane.getSelectedApplication();
	}

	private void allDevicePropertiesChanged(PortletFactory portletFactory) {
		Collection<GenericDevice> myDevices = devices.values();
		for (GenericDevice genericDevice : myDevices) {
         if (portletFactory.typeIsSupported(genericDevice)) {
         	devicePropertiesChanged(genericDevice.getSerialNumber(), genericDevice, null);
         }	            	
      }
	}
	
	public void disposeDeviceInstance(String serialNumber) {
		GenericDevice device = devices.get(serialNumber);
		if (device != null) {
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
		m_controller = appInstance;
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

	public SimulationManager getSimulationManager() {
		return m_manager;
	}

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

	public void devicePropertiesChanged(final String deviceSerialNumber, final GenericDevice device, final Map<String, Object> properties) {

		enqueueTask(new Runnable() {
			@Override
			public void run() {
				m_actionPane.changeDevice(deviceSerialNumber, device, properties);
			}
		});
	}

	/**
	 * @return the scriptFileList
	 */
	public DefaultListModel getScriptFileList() {
		return scriptFileList;
	}

	public void executeScript(String scriptName) {
		m_ScriptExecutor.executeScript(scriptName);
	}

	public void stopScript() {
		m_ScriptExecutor.stopExecution();
	}

	public GenericDevice getDeviceBySerialNumber(String deviceId) {
		return devices.get(deviceId);
	}

	public WindowPane getPortlet(GenericDevice device) {
		for (PortletFactory portletFactory : portletFactories) {
			if (portletFactory.typeIsSupported(device))
				return portletFactory.createPortlet(this, device.getSerialNumber());
		}
		return null;
	}
	
	public ResourceImageReference getImageForDevice(GenericDevice device) {
		for (PortletFactory portletFactory : portletFactories) {
			if (portletFactory.typeIsSupported(device))
				return portletFactory.getImageForDevice(device);
		}
		return null;
	}
	
	

	/**
	 * Tracks the properties changes of devices to update GUI information
	 * 
	 * @author Gabriel
	 *
	 */
	class DeviceServiceTracker extends ServiceTracker {

		private BundleContext m_context;

		public DeviceServiceTracker(BundleContext context) {
			super(context, GenericDevice.class.getName(), null);
			m_context = context;

		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			String serialNumber = (String) reference.getProperty("device.serialNumber");
			if (serialNumber!=null) {
				Map<String, Object> properties = new HashMap<String, Object>();
				for (String property : reference.getPropertyKeys()) {
					Object propValue = reference.getProperty(property);
					if (propValue != null)
						properties.put(property, propValue);
				}
				devicePropertiesChanged(serialNumber, ((GenericDevice) service), properties);
			}
		}

		@Override
		public void remove(ServiceReference reference) {
			super.remove(reference);
			m_context.ungetService(reference);
		}

	}



	/**
    * @return the houseImage
    */
   public String getHouseImage() {
   	return houseImage;
   }

	/**
    * @return the userImage
    */
   public String getUserImage() {
   	return userImage;
   }

	/**
    * @return the homeType
    */
   public String getHomeType() {
   	return homeType;
   }

   
	public boolean isAndroid() {
		if (isAndroid == null) {
			isAndroid = ((isAndroidStr != null) && (Boolean.valueOf(isAndroidStr)));
		}
		
		return isAndroid;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		 @SuppressWarnings("unused")
		Object newVal = evt.getNewValue();
				
	}

	public Application getApplication(String appId) {
		return m_appMgr.getApplication(appId);
	}

	@Override
	public void addApplication(Application app) {
		m_actionPane.addApplication(app);
		m_selectAppPane.addApplication(app);
	}

	@Override
	public void removeApplication(Application app) {
		m_actionPane.removeApplication(app);
		m_selectAppPane.removeApplication(app);
	}
	
	public void saveSimulationEnvironment() {
		Set<String> envs = m_manager.getEnvironments();
		
		Set<String> devices = m_manager.getDevices();
		
		System.out.println("==============================================");
		
		
		FileWriter outFile;
		PrintWriter out;
      try {
	      outFile = new FileWriter("saveInput.icasa");
	      out = new PrintWriter(outFile);
	      
			for (String environment : envs) {
		      out.println("environment " + "\"" + environment + "\" {");
		      
		      Zone zone = m_manager.getEnvironmentZone(environment);
		      out.println("\t position = " + zone.leftX + " " + zone.topY + " " + zone.rightX + " " +zone.bottomY);
		      
		      for (String device : devices) {
		         Position position = m_manager.getDevicePosition(device);
		         String deviceEnv = m_manager.getEnvironmentFromPosition(position);
		         if (deviceEnv.equals(environment)) {
		         	String deviceLine = "\t device " + "\"" + device + "\" : \"";
		         	
		         	try {
		               ServiceReference[] references = m_context.getServiceReferences((String) null, "(device.serialNumber="+ device +")");
		               if (references.length>0) {
		               	ServiceReference reference = references[0];
		               	deviceLine += reference.getProperty("factory.name") + "\" {";
		               	out.println(deviceLine);		               	
		               	out.println("\t\t position = " + position.x + " " + position.y);
		               	String description = (String) reference.getProperty("service.description");
		               	if (description!=null)
		               		out.println("\t\t \"service.description\" = \"" + description + "\"");
		               	String state = (String) reference.getProperty("state");
		               	if (state!=null)
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

	public final ApplicationManager getAppMgr() {
		return m_appMgr;
	}

}
