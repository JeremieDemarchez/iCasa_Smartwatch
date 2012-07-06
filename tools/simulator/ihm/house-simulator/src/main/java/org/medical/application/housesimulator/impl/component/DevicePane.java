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
package org.medical.application.housesimulator.impl.component;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nextapp.echo.app.Border;
import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
import nextapp.echo.app.Component;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Font;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.SelectField;
import nextapp.echo.app.Table;
import nextapp.echo.app.TextField;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.GridLayoutData;
import nextapp.echo.app.list.DefaultListModel;
import nextapp.echo.app.list.ListSelectionModel;
import nextapp.echo.app.table.DefaultTableModel;
import nextapp.echo.app.table.TableCellRenderer;
import nextapp.echo.extras.app.DropDownMenu;
import nextapp.echo.extras.app.menu.DefaultMenuModel;
import nextapp.echo.extras.app.menu.DefaultMenuSelectionModel;
import nextapp.echo.extras.app.menu.DefaultOptionModel;
import nextapp.echo.extras.app.menu.ItemModel;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.medical.application.Application;
import org.medical.application.housesimulator.impl.MedicalHouseSimulatorImpl;
import org.medical.application.housesimulator.impl.component.event.DropEvent;
import org.medical.application.housesimulator.impl.component.event.DropListener;
import org.medical.application.housesimulator.portlet.impl.GenericDeviceStatusWindow;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.sound.AudioSource;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;

/**
 * TODO comments.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public class DevicePane extends ContentPane implements SelectedApplicationTracker {

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 3778184066500074285L;

	public static ResourceImageReference DEVICE_IMAGE = new ResourceImageReference("/Device.png");
	public static ResourceImageReference BIG_DEVICE_IMAGE = new ResourceImageReference("/BigDevice.png");
	
	private final Random m_random = new Random();

	private final ActionPane m_parent;

	private final TextField m_description;

	private final DropDownMenu m_factory;

	private final Map<String, Factory> m_deviceFactories = new HashMap<String, Factory>();

	private final Map<String, DeviceEntry> m_devices = new HashMap<String, DeviceEntry>();
	
	private final Map<String, DeviceEntry> m_savedDevices = new HashMap<String, DeviceEntry>();

	private DeviceTableModel tableModel;
	
	protected List<String> m_deviceSerialNumbers = new ArrayList<String>();
	
	private Map<Application, Set<String /* device id */>> m_devicesPerApplication = new HashMap<Application, Set<String /* device id */>>();

	public DevicePane(final ActionPane parent) {
		m_parent = parent;
		// Create the image label.
		final Label image = new Label(new ResourceImageReference(BIG_DEVICE_IMAGE.getResource(), new Extent(50), new Extent(
		      50)));
		final GridLayoutData imageLayout = new GridLayoutData();
		imageLayout.setRowSpan(2);
		image.setLayoutData(imageLayout);
		// Create the device creation text fields and button.
		m_description = new TextField();
		m_factory = new DropDownMenu(new DefaultMenuModel(), new DefaultMenuSelectionModel());
		final GridLayoutData factoryLayoutData = new GridLayoutData();
		factoryLayoutData.setColumnSpan(2);
		m_factory.setLayoutData(factoryLayoutData);

		final Button addDeviceButton = new Button("Create device");
		addDeviceButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = -3523127008825007357L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String description = m_description.getText();
				String factoryName = m_factory.getSelectionModel().getSelectedId();

				// Create the device.
				try {
					createDeviceInstance(factoryName, description);
				} catch (Exception e1) {
					showErrorWindow(e.toString());
					throw new RuntimeException(e1);
				}
				// The device will be plotted when its service will be provided.
				m_description.setText("");
			}
		});
		final GridLayoutData deviceButtonLayout = new GridLayoutData();
		deviceButtonLayout.setColumnSpan(2);
		addDeviceButton.setLayoutData(deviceButtonLayout);
		
		
		m_grid = new Grid(3);
		m_grid.setInsets(new Insets(2, 3));
		m_grid.add(image);
		m_grid.add(m_factory);
		m_grid.add(m_description);
		m_grid.add(addDeviceButton);
		// grid.add(m_grid);

		recreateDeviceTable(null);

		add(m_grid);
	}

	private void recreateDeviceTable(Application service) {
		if (m_deviceTable != null)
			m_grid.remove(m_deviceTable);
		
		final GridLayoutData gridLayout = new GridLayoutData();
		gridLayout.setColumnSpan(3);
		m_deviceTable = createTable(service);
		m_deviceTable.setLayoutData(gridLayout);
		m_grid.add(m_deviceTable);
	}

	private Table createTable(Application service) {

		if (service == null)
			tableModel = new HomeDeviceTableModel(0);
		else if (service.getId().startsWith("Safe"))
			tableModel = new ServiceWithPropDeviceTableModel(0);
		else
			tableModel = new ServiceWithoutPropDeviceTableModel(0);
		
		Table aTable = new Table(tableModel);
		aTable.setBorder(new Border(1, Color.LIGHTGRAY, Border.STYLE_SOLID));
		aTable.setInsets(new Insets(3, 1));
		aTable.setDefaultRenderer(Object.class, new DeviceTableCellRenderer());
		aTable.setDefaultHeaderRenderer(new DeviceHeaderTableCellRenderer());

		return aTable;
	}

	
	/**
	 * Creates a device instance
	 * @param factoryName Name of the iPOJO factory to be used
	 * @param description Description of the instance
	 * @param x X position
	 * @param y Y position
	 * @throws UnacceptableConfiguration
	 * @throws MissingHandlerException
	 * @throws ConfigurationException
	 */
	private synchronized void createDeviceInstance(String factoryName, String description)
	      throws UnacceptableConfiguration, MissingHandlerException, ConfigurationException {
	
		final Factory factory = m_deviceFactories.get(factoryName);
		if (factory == null) {
			throw new NoSuchElementException("Factory not found : " + factoryName);
		}
		// Generate a serial number
		String serialNumber = Long.toString(m_random.nextLong(), 16);
		// Create the device
		Dictionary<String, String> properties = new Hashtable<String, String>();
		properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, serialNumber);
		properties.put(Constants.SERVICE_DESCRIPTION, description);
		properties.put("instance.name", factoryName + "-" + serialNumber);
		factory.createComponentInstance(properties);
		// Set its position.
		//getAppInstance().getSimulationManager().setDevicePosition(serialNumber, new Position(x, y));
	}

	
	private static class DeviceEntry {
		private String serialNumber;
		private Label label;
		private FloatingButton widget;
		private Position position;
		private String logicPosition;
		private String state;
		private String fault;
		public FloatingButtonDragSource dragSource;
		public String description;
	}

	
	/**
	 * Adds a device "reference" into the GUI simulator application
	 * @param device device instance to be added
	 * @param properties properties of the device service
	 */
	public void addDevice(final GenericDevice device, Map<String, Object> properties) {
		
		DeviceEntry entry = getDeviceEntry(device, properties);
		
		String serialNumber = device.getSerialNumber();
		DeviceEntry savedEntry = null;
		synchronized (m_deviceSerialNumbers) {
			savedEntry = m_savedDevices.get(serialNumber);
			if (savedEntry == null) {
				// workaround for tikitag (not able to get serial number of
				// tikitag)
				Pattern tikitagPattern = Pattern.compile("usb:0x072:090cc:.*");
				if (tikitagPattern.matcher(serialNumber).matches()) {
					for (String savedSerialNb : m_savedDevices.keySet()) {
						Matcher matcher = tikitagPattern.matcher(savedSerialNb);
						if (matcher.matches()) {
							savedEntry = m_savedDevices.get(savedSerialNb);
							break;
						}
					}
				}
			}
		}
		if (savedEntry != null) {
			entry.state = savedEntry.state;
			entry.position= savedEntry.position;
			entry.logicPosition = savedEntry.logicPosition;
		}

		// manage addition
		if (m_devices.containsKey(entry.serialNumber)) {
			showErrorWindow("The device \"" + entry.serialNumber + "\"already exists.");
			return;
		}
		
		// Creating and adding the house pane button (icon)
		addDeviceWidget(device, entry.description, entry.position, entry.serialNumber, entry);
		synchronized (m_deviceSerialNumbers) {
			m_devices.put(entry.serialNumber, entry);
			m_savedDevices.put(entry.serialNumber, entry);
			m_deviceSerialNumbers.add(entry.serialNumber);
		}
		
		// set it as unavailable for all services by default
		for (Application service : getAppInstance().getAppMgr().getApplications()) {
			boolean available = service.getId().startsWith("Digital Home Simulator");
			setDeviceAvailabilityFor(entry.serialNumber, service, available);
		}
		
		// Add device to the table
		addDeviceInTable(entry);
		
		// restore state at the end of adding
		if (savedEntry != null) {
			device.setState(entry.state);
			getAppInstance().getSimulationManager().setDevicePosition(
				serialNumber, new Position(entry.position.x, entry.position.y));
		}
		
	}

	private DeviceEntry getDeviceEntry(GenericDevice device,
			Map<String, Object> properties) {
		String description = (String) properties.get(Constants.SERVICE_DESCRIPTION);
		if (description == null) {
			// If service description is not defined, use the device serial number.
			description = device.getSerialNumber();
		}
		
		Position position = getAppInstance().getSimulationManager().getDevicePosition(device.getSerialNumber());

		if (position == null) {
			position = generateBorderPosition();
		}
		final String serialNumber = device.getSerialNumber();
		
		String state = (String) properties.get("state");
		if (state==null)
			state = "unknown";
		
		String fault = (String) properties.get("fault");
		if (fault==null)
			fault = "uknown";
		
		String logicPosition =  getAppInstance().getSimulationManager().getEnvironmentFromPosition(position);
		if (logicPosition==null)
			logicPosition = "unassigned";
		
		final DeviceEntry entry = new DeviceEntry();
		entry.serialNumber = serialNumber;
		entry.state = state;
		entry.fault = fault;
		entry.label = new Label(description);
		entry.position = position;
		entry.logicPosition = logicPosition;
		entry.description = description;
		
		return entry;
	}

	protected void addDeviceWidget(final GenericDevice device,
			String description, Position position, final String serialNumber,
			final DeviceEntry entry) {
		ResourceImageReference imageForDevice = getImageForDevice(device);
		entry.widget = new FloatingButton(position.x, position.y, imageForDevice, description);
		entry.widget.setActionCommand(serialNumber);
		entry.widget.addActionListener(new DeviceSpotActionListener());
		
		// add support of drag and drop
		final FloatingButtonDragSource dragSource = new FloatingButtonDragSource(entry.widget);
		dragSource.setBackground(Color.YELLOW);
		dragSource.addDropTarget(HousePane.HOUSE_PANE_RENDER_ID);
		dragSource.addDropListener(new DropListener() {
			
			@Override
			public void dropPerformed(DropEvent event) {
				getAppInstance().getSimulationManager().setDevicePosition(
						serialNumber, new Position(event.getTargetX(), event.getTargetY()));
			}
		});
		entry.dragSource = dragSource;
		
		getAppInstance().getHousePane().getChildContainer().add(entry.dragSource);
	}


	private void addDeviceInTable(final DeviceEntry entry) {
		tableModel.addDeviceRow(entry);
	}

	/**
	 * Removes the device "references" in the GUI simulator application
	 * @param device Device instance to be removed
	 */
	public void removeDevice(GenericDevice device) {
			
		String serialNumber = device.getSerialNumber();
		removeDeviceFromTable(serialNumber);

		
		DeviceEntry entry = m_devices.get(serialNumber);
		synchronized (m_deviceSerialNumbers) {
			m_devices.remove(serialNumber);
			m_deviceSerialNumbers.remove(entry.serialNumber);
		}
		
		removeDeviceWidget(entry);
		
		entry.label = null;
		entry.widget = null;
		entry.dragSource = null;
	}

	protected void removeDeviceWidget(DeviceEntry entry) {
		
		getAppInstance().getHousePane().getChildContainer().remove(entry.dragSource);
	}


	private void removeDeviceFromTable(String serialNumber) {
		tableModel.removeDeviceRow(serialNumber);
	}

	public void moveDevice(final String deviceSerialNumber, Position position) {
		DeviceEntry entry = m_devices.get(deviceSerialNumber);
		if (entry == null) {
			return;
		}
		if (((Extent) entry.widget.get(FloatingButton.PROPERTY_POSITION_X)).getValue() == 0 && position != null) {
			// Free the border slot.
			int y = ((Extent) entry.widget.get(FloatingButton.PROPERTY_POSITION_Y)).getValue();
			BORDER_POSITIONS[y / 20] = false;
		}
		if (position == null) {
			position = generateBorderPosition();
		} 
		
		String logicPosition =  getAppInstance().getSimulationManager().getEnvironmentFromPosition(position);
		if (logicPosition==null)
			logicPosition = "unassigned";
		
		if (entry.position.equals(position) && logicPosition.equals(entry.logicPosition))
			return;
		
		entry.position = position;
		entry.logicPosition = logicPosition;
		
		// re-render
		removeDeviceWidget(entry);
		
		if (isAvailableForSelectedApplication(deviceSerialNumber))
			addDeviceWidget(getAppInstance().getDeviceBySerialNumber(deviceSerialNumber), entry.description, position, deviceSerialNumber, entry);
		
		updateDeviceTable(deviceSerialNumber, getAppInstance().getDeviceBySerialNumber(deviceSerialNumber));
	}

	public void addDeviceFactory(Factory factory) {
		String factoryName = factory.getName();
		m_deviceFactories.put(factoryName, factory);
		DefaultMenuModel model = ((DefaultMenuModel) m_factory.getModel());
		model.addItem(new DefaultOptionModel(factoryName, factoryName, null));
		m_factory.setModel(new DefaultMenuModel());
		m_factory.setModel(model);
		if (model.getItemCount() == 1) {
			m_factory.getSelectionModel().setSelectedId(factoryName);
		}
	}

	public void removeDeviceFactory(Factory factory) {
		String factoryName = factory.getName();
		m_deviceFactories.remove(factoryName);
		DefaultMenuModel model = ((DefaultMenuModel) m_factory.getModel());
		for (int i = 0; i < model.getItemCount(); i++) {
			ItemModel item = model.getItem(i);
			if (item.getId().equals(factoryName)) {
				model.removeItem(item);
				break;
			}
		}
		m_factory.setModel(new DefaultMenuModel());
		m_factory.setModel(model);
	}

	private void showErrorWindow(final String error) {
		final WindowPane window = new WindowPane();
		// Create the icon.
		final Label icon = new Label(new ResourceImageReference("/Error.png"));
		// Create the message label.
		final Label message = new Label(error);
		// Create the confirmation button.
		final Button okButton = new Button("OK");
		okButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = -6209035414023630010L;

			@Override
			public void actionPerformed(ActionEvent e) {
				window.userClose();
			}
		});
		final GridLayoutData okButtonLayout = new GridLayoutData();
		okButtonLayout.setColumnSpan(2);
		okButton.setLayoutData(okButtonLayout);
		// Create the grid.
		final Grid grid = new Grid(2);
		grid.add(icon);
		grid.add(message);
		grid.add(okButton);
		// Populate the window.
		window.setTitle("ERROR");
		window.setModal(true);
		window.setMaximizeEnabled(false);
		window.setClosable(false);
		window.add(grid);
		getAppInstance().getWindow().getContent().add(window);
	}
	
	/**
	 * Changes device image when the properties of services have been changed
	 * 
	 * @param deviceSerialNumber
	 * @param device
	 */
	public void changeDevice(String deviceSerialNumber, GenericDevice device, Map<String, Object> properties) {
		DeviceEntry entry = m_devices.get(deviceSerialNumber);
		if (entry == null) {
			return;
		}
		removeDeviceWidget(entry);
		
		if (properties != null) {
			DeviceEntry newEntry = getDeviceEntry(device, properties);
			entry.description = newEntry.description;
			entry.logicPosition = newEntry.logicPosition;
			entry.state = newEntry.state;
			entry.fault = newEntry.fault;
		}

		if (isAvailableForSelectedApplication(deviceSerialNumber))
			addDeviceWidget(device, entry.description, entry.position, deviceSerialNumber, entry);
		
		updateDeviceTable(deviceSerialNumber, device);
	}

	private void updateDeviceTable(String deviceSerialNumber,
			GenericDevice device) {
		DeviceEntry entry = m_devices.get(deviceSerialNumber);
		if (entry == null) {
			return;
		}
		tableModel.updateDeviceRow(entry);
	}

	/**
	 * Gets the current application instance
	 * 
	 * @return
	 */
	private MedicalHouseSimulatorImpl getAppInstance() {
		return m_parent.getApplicationInstance();
	}

	
	/**
	 * Gets the corresponding image for a device
	 * 
	 * @param device
	 * @return
	 */
	private ResourceImageReference getImageForDevice(final GenericDevice device) {
		ResourceImageReference image = getAppInstance().getImageForDevice(device);
		if (image!=null)
			return image;
		
		if (device instanceof DimmerLight) {
			return new ResourceImageReference("/DimmerLamp.png");
		} else if (device instanceof BinaryLight) {
			return new ResourceImageReference("/Lamp.png");
		} else if (device instanceof Thermometer) {
			return new ResourceImageReference("/Thermometer.png");
		} else if (device instanceof Heater) {
			return new ResourceImageReference("/Heater.png");
		} else if (device instanceof AudioSource) {
			return new ResourceImageReference("/Player.png");
		} else {
			return new ResourceImageReference("/Device-Icasa.png");
		}
	}
			

	private static boolean[] BORDER_POSITIONS = new boolean[20];

	private Table m_deviceTable;

	private Grid m_grid;

	private static Position generateBorderPosition() {
		// Find the next slot available.
		int i = nextSlotAvailable();
		BORDER_POSITIONS[i] = true;
		return new Position(20, (30 * i) + 20);
	}

	private static int nextSlotAvailable() {
		for (int i = 0; i < BORDER_POSITIONS.length; i++) {
			if (!BORDER_POSITIONS[i]) {
				return i;
			}
		}
		return BORDER_POSITIONS.length - 1;
	}

	private class DeviceSpotActionListener implements ActionListener {

		/**
         * 
         */
		private static final long serialVersionUID = -8163178014335773459L;

		@Override
		public void actionPerformed(ActionEvent e) {
			// Display the device in the status pane.

			String deviceId = e.getActionCommand();
			MedicalHouseSimulatorImpl app = getAppInstance();
			
			if (app.getStatusPane().getComponent(deviceId) != null) {
				return;
			}
			
			// Portlet Binding
			WindowPane portlet = app.getPortlet((app.getDeviceBySerialNumber(deviceId)));
			if (portlet == null)
				portlet = new GenericDeviceStatusWindow(app, deviceId);
			
			app.getStatusPane().add(portlet);
			
			if (m_parent.getApplicationInstance().isAndroid())
				m_parent.setActiveTabIndex(2);
		}

	}
	
	private SelectField createLocationList(String deviceSerialNumber,
			String deviceLocation) {
		final SelectField locationField = new SelectField();
		
		DefaultListModel model = new DefaultListModel();
		final SimulationManager manager = getAppInstance()
				.getSimulationManager();
		Set<String> environments = manager.getEnvironments();
		String[] locationStrs = environments.toArray(new String[environments.size()]);
		int deviceLocationIdx = -1;
		for (int idx = 0; idx < locationStrs.length; idx++) {
			String location = locationStrs[idx];
			model.add(location);
			if ((deviceLocation != null) && (deviceLocation.equals(location)))
				deviceLocationIdx  = idx;
		}
		model.add("unassigned");
		locationField.setModel(model);
		if (deviceLocationIdx == -1)
			deviceLocationIdx = environments.size();
		
		locationField.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		if (deviceLocation != null)
			locationField.getSelectionModel().setSelectedIndex(deviceLocationIdx, true);

		StringListActionListener locationMenuActionListener = new StringListActionListener(
				deviceSerialNumber, locationField) {
			protected void performSet(String newValueId) {
				getAppInstance().getSimulationManager().setDeviceLocation(
						deviceSerialNumber, newValueId);
			}
		};

		locationField.addActionListener(locationMenuActionListener);

		return locationField;
	}
	
	private SelectField createStateList(String deviceSerialNumber,
			String deviceState) {
		final SelectField stateField = new SelectField();
		
		DefaultListModel model = new DefaultListModel();
		String[] states =  { "activated", "deactivated" } ;
		int deviceStateIdx = -1;
		for (int idx = 0; idx < states.length; idx++) {
			String state = states[idx];
			model.add(state);
			if ((deviceState != null) && (deviceState.equals(state)))
				deviceStateIdx  = idx;
		}
		stateField.setModel(model);
		if (deviceStateIdx == -1)
			deviceStateIdx = 0; // by default activated
		
		stateField.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		if (deviceState != null)
			stateField.getSelectionModel().setSelectedIndex(deviceStateIdx, true);
		
		GenericDevice device = getAppInstance().getDeviceBySerialNumber(deviceSerialNumber);
		if (!(device instanceof GenericDevice)) {
			stateField.setEnabled(false);
		}

		StringListActionListener locationMenuActionListener = new StringListActionListener(
				deviceSerialNumber, stateField) {
			protected void performSet(String newValueId) {
				GenericDevice device = getAppInstance().getDeviceBySerialNumber(deviceSerialNumber);
				device.setState(newValueId);
			}
		};

		stateField.addActionListener(locationMenuActionListener);

		return stateField;
	}
	
	private SelectField createUsedList(final String deviceSerialNumber,
			Boolean value) {
		final SelectField stateField = new SelectField();
		
		DefaultListModel model = new DefaultListModel();
		final String[] states =  { "yes", "no" } ;
		int deviceStateIdx = -1;
		for (int idx = 0; idx < states.length; idx++) {
			String state = states[idx];
			model.add(state);
		}
		stateField.setModel(model);
		if (value == null)
			deviceStateIdx = 1; // by default not used
		else if (value)
			deviceStateIdx = 0;
		else
			deviceStateIdx = 1;
		
		stateField.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		stateField.getSelectionModel().setSelectedIndex(deviceStateIdx, true);
		
		ActionListener stateMenuActionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIdx = stateField.getSelectionModel().getMinSelectedIndex();
				String availableStr = (String) stateField.getModel().get(selectedIdx);
				
				DeviceEntry entry = m_devices.get(deviceSerialNumber);
				if (entry == null)
					return;
				
				int stateIdx = -1;
				for (int idx = 0; idx < states.length; idx++) {
					String state = states[idx];
					if (state.equals(availableStr))
						stateIdx = idx;
				}
				boolean available = (stateIdx == 0);
				
				setDeviceAvailabilityFor(deviceSerialNumber, getAppInstance().getSelectedApplication(), available);
				tableModel.updateDeviceRow(entry);
				updateDeviceWidgetVisibility(entry);
			}
		};

		stateField.addActionListener(stateMenuActionListener);

		return stateField;
	}
	
	private SelectField createFaultList(String deviceSerialNumber,
			String deviceState) {
		final SelectField stateField = new SelectField();
		
		DefaultListModel model = new DefaultListModel();
		String[] states =  { "yes", "no", "unknown" } ;
		int deviceStateIdx = -1;
		for (int idx = 0; idx < states.length; idx++) {
			String state = states[idx];
			model.add(state);
			if ((deviceState != null) && (deviceState.equals(state)))
				deviceStateIdx  = idx;
		}
		stateField.setModel(model);
		if (deviceStateIdx == -1)
			deviceStateIdx = 2; // by default unknown
		
		stateField.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		GenericDevice device = getAppInstance().getDeviceBySerialNumber(deviceSerialNumber);
		if (!(device instanceof GenericDevice)) {
			stateField.setEnabled(false);
			deviceStateIdx = 2; // by default unknown
		}
		
		if (deviceState != null)
			stateField.getSelectionModel().setSelectedIndex(deviceStateIdx, true);

		StringListActionListener locationMenuActionListener = new StringListActionListener(
				deviceSerialNumber, stateField) {
			protected void performSet(String newValueId) {
				GenericDevice device = getAppInstance().getDeviceBySerialNumber(deviceSerialNumber);
				if (device instanceof GenericDevice) {
					((GenericDevice) device).setFault(newValueId);
				}
			}
		};

		stateField.addActionListener(locationMenuActionListener);

		return stateField;
	}
		
	class DeviceTableCellRenderer implements TableCellRenderer {

		/**
	    * 
	    */
		private static final long serialVersionUID = -2146113024393976876L;

		@Override
		public Component getTableCellRendererComponent(Table table, final Object value, int column, int row) {
			if (value == null) {
				return null;
			}
			final DeviceTableModel deviceTableModel = (DeviceTableModel) tableModel;
			String deviceSerialNumber = deviceTableModel.getDeviceSerialNumber(row);
			
			int detailCol = -1, deleteCol = -1, faultCol = -1, stateCol = -1, locationCol = -1, usableCol = -1;
			if (tableModel instanceof HomeDeviceTableModel) {
				usableCol = HomeDeviceTableModel.DEVICE_USABLE_STATE_COL_IDX;
				faultCol = HomeDeviceTableModel.DEVICE_FAULT_STATE_COL_IDX;
				deleteCol = faultCol + 1;
				detailCol = deleteCol + 1;
			} else if (tableModel instanceof ServiceWithPropDeviceTableModel) {
				locationCol = ServiceWithPropDeviceTableModel.DEVICE_LOCATION_COL_IDX;
				stateCol = ServiceWithPropDeviceTableModel.DEVICE_STATE_COL_IDX;
				faultCol = ServiceWithPropDeviceTableModel.DEVICE_FAULT_STATE_COL_IDX;
				deleteCol = faultCol + 1;
				detailCol = deleteCol + 1;
			} else if (tableModel instanceof ServiceWithoutPropDeviceTableModel) {
				stateCol = ServiceWithoutPropDeviceTableModel.DEVICE_STATE_COL_IDX;
				faultCol = ServiceWithoutPropDeviceTableModel.DEVICE_FAULT_STATE_COL_IDX;
				deleteCol = faultCol + 1;
				detailCol = deleteCol + 1;
			}
			
			if (column == usableCol) {
				return createStateList(deviceSerialNumber, (String) value);
			}
			if (column == locationCol) {
				return createLocationList(deviceSerialNumber, (String) value);
			}
			if (column == stateCol) {
				return createUsedList(deviceSerialNumber, (Boolean) value);
			}
			if (column == faultCol) {
				return createFaultList(deviceSerialNumber, (String) value);				
			}			
			if (column == deleteCol) {
				Button deleteButton = new Button(new ResourceImageReference("/Remove.png"));
				deleteButton.addActionListener(new ActionListener() {

					/**
                * 
                */
					private static final long serialVersionUID = 264629139995718720L;

					@Override
					public void actionPerformed(ActionEvent e) {
						getAppInstance().disposeDeviceInstance(value.toString());
					}
				});
				return deleteButton;
			}
			if (column == detailCol) {
				Button detailButton = new Button("...");
				
				detailButton.setActionCommand(value.toString());
				detailButton.addActionListener(new DeviceSpotActionListener());
				return detailButton;
			}
			
			return new Label(value.toString());
		}

	}
	
	public abstract class DeviceTableModel extends DefaultTableModel {
		
		protected List<String> deviceSerialNumbers = new ArrayList<String>();
		
		public DeviceTableModel(int cols, int rows) {
			super(cols, rows);
		}
		
		public String getDeviceSerialNumber(int row) {
			return deviceSerialNumbers.get(row);
		}
		
		public abstract void updateDeviceRow(DeviceEntry entry);
		
		public abstract void addDeviceRow(DeviceEntry entry);
		
		@Override
		public Object getValueAt(int column, int row) {
			return super.getValueAt(column, row);
		}
		
		public synchronized void removeDeviceRow(String serialNumber) {
			int rowIdx = deviceSerialNumbers.indexOf(serialNumber);
			if (rowIdx >=0) {
				deviceSerialNumbers.remove(rowIdx);
				tableModel.deleteRow(rowIdx);
			}
		}
	}
	
	public class HomeDeviceTableModel extends DeviceTableModel {
		
		private final String[] columns = {
			"Device Name",
			"Usable",
			"Fault",
			"Remove",
			"Details"};
		
		static final int DEVICE_DESC_COL_IDX = 0;
		static final int DEVICE_USABLE_STATE_COL_IDX = 1;
		static final int DEVICE_FAULT_STATE_COL_IDX = 2;

		public HomeDeviceTableModel(int rows) {
			super(5, rows);
			for (int i = 0; i < columns.length; i++) {
				setColumnName(i, columns[i]);
			}
		}

		public void updateDeviceRow(DeviceEntry entry) {
			int rowIdx = deviceSerialNumbers.indexOf(entry.serialNumber);
			if (rowIdx <0)
				return;
			
			setValueAt(entry.label.getText(), DEVICE_DESC_COL_IDX, rowIdx);
			setValueAt(entry.state, DEVICE_USABLE_STATE_COL_IDX, rowIdx);
			setValueAt(entry.fault, DEVICE_FAULT_STATE_COL_IDX, rowIdx);
		}
		
		public synchronized void addDeviceRow(DeviceEntry entry) {
			String data[] = new String[columns.length];
			data[DEVICE_DESC_COL_IDX] = entry.label.getText();
			data[DEVICE_USABLE_STATE_COL_IDX] = entry.state;
			data[DEVICE_FAULT_STATE_COL_IDX] = entry.fault;
			data[columns.length - 2] = entry.serialNumber;
			data[columns.length - 1] = entry.serialNumber;
			deviceSerialNumbers.add(entry.serialNumber);
			tableModel.addRow(data);
		}
	}
	
	public class ServiceWithPropDeviceTableModel extends DeviceTableModel {
		
		private final String[] columns = {
			"Device Description",
			"Location *",
			"Used *",
			"Fault",
			"Remove",
			"Details"};
		
		static final int DEVICE_DESC_COL_IDX = 0;
		static final int DEVICE_LOCATION_COL_IDX = 1;
		static final int DEVICE_STATE_COL_IDX = 2;
		static final int DEVICE_FAULT_STATE_COL_IDX = 3;
		
		public ServiceWithPropDeviceTableModel(int rows) {
			super(6, rows);
			for (int i = 0; i < columns.length; i++) {
				setColumnName(i, columns[i]);
			}
		}
		
		public void updateDeviceRow(DeviceEntry entry) {
			int rowIdx = deviceSerialNumbers.indexOf(entry.serialNumber);
			if (rowIdx <0)
				return;
			
			setValueAt(entry.label.getText(), DEVICE_DESC_COL_IDX, rowIdx);
			setValueAt(isAvailableForSelectedApplication(entry.serialNumber), DEVICE_STATE_COL_IDX, rowIdx);
			setValueAt(entry.logicPosition, DEVICE_LOCATION_COL_IDX, rowIdx);
			setValueAt(entry.fault, DEVICE_FAULT_STATE_COL_IDX, rowIdx);
		}
		
		public synchronized void addDeviceRow(DeviceEntry entry) {
			Object data[] = new Object[columns.length];
			data[DEVICE_DESC_COL_IDX] = entry.label.getText();
			data[DEVICE_LOCATION_COL_IDX] = entry.logicPosition;
			data[DEVICE_STATE_COL_IDX] = isAvailableForSelectedApplication(entry.serialNumber);
			data[DEVICE_FAULT_STATE_COL_IDX] = entry.fault;
			data[columns.length - 2] = entry.serialNumber;
			data[columns.length - 1] = entry.serialNumber;
			deviceSerialNumbers.add(entry.serialNumber);
			tableModel.addRow(data);
		}
	}
	
	public class ServiceWithoutPropDeviceTableModel extends DeviceTableModel {
		
		private final String[] columns = {
			"Device Description",
			"Used *",
			"Fault",
			"Remove",
			"Details"};
		
		static final int DEVICE_DESC_COL_IDX = 0;
		static final int DEVICE_STATE_COL_IDX = 1;
		static final int DEVICE_FAULT_STATE_COL_IDX = 2;
		
		public ServiceWithoutPropDeviceTableModel(int rows) {
			super(5, rows);
			for (int i = 0; i < columns.length; i++) {
				setColumnName(i, columns[i]);
			}
		}
		
		public void updateDeviceRow(DeviceEntry entry) {
			int rowIdx = deviceSerialNumbers.indexOf(entry.serialNumber);
			if (rowIdx <0)
				return;
			
			setValueAt(entry.label.getText(), DEVICE_DESC_COL_IDX, rowIdx);
			setValueAt(isAvailableForSelectedApplication(entry.serialNumber), DEVICE_STATE_COL_IDX, rowIdx);
			setValueAt(entry.fault, DEVICE_FAULT_STATE_COL_IDX, rowIdx);
		}

		public synchronized void addDeviceRow(DeviceEntry entry) {
			Object data[] = new Object[columns.length];
			data[DEVICE_DESC_COL_IDX] = entry.label.getText();
			data[DEVICE_STATE_COL_IDX] = isAvailableForSelectedApplication(entry.serialNumber);
			data[DEVICE_FAULT_STATE_COL_IDX] = entry.fault;
			data[columns.length - 2] = entry.serialNumber;
			data[columns.length - 1] = entry.serialNumber;
			deviceSerialNumbers.add(entry.serialNumber);
			tableModel.addRow(data);
		}
	}

	class DeviceHeaderTableCellRenderer implements TableCellRenderer {

		/**
       * 
       */
		private static final long serialVersionUID = -396501832386443994L;


		private final Font headerFont = new Font(Font.HELVETICA, Font.BOLD, new Extent(11, Extent.PT));

		@Override
		public Component getTableCellRendererComponent(Table table, Object value, int column, int row) {
			if (value == null) {
				return null;
			} else {
				Label alLabel = new Label(value.toString());
				alLabel.setFont(headerFont);
				return alLabel;
			}
		}

	}
	
	abstract class StringListActionListener implements ActionListener {

		/**
       * 
       */
		private static final long serialVersionUID = 5723251084216759477L;

		protected String deviceSerialNumber;

		private SelectField field;

		public StringListActionListener(String deviceSerialNumber,
				SelectField field) {
			super();
			this.deviceSerialNumber = deviceSerialNumber;
			this.field = field;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedIdx = field.getSelectionModel().getMinSelectedIndex();
			String locationId = (String) field.getModel().get(selectedIdx);
			performSet(locationId);

		}

		protected abstract void performSet(String newValueId);

	}

	public boolean isAvailableFor(String deviceSerialNumber, Application service) {
		if (service == null)
			return true;
		
		Set<String> deviceIds = m_devicesPerApplication.get(service);
		if (deviceIds == null) {
			return false;
		}
		
		return (deviceIds.contains(deviceSerialNumber));
	}

	public boolean isAvailableForSelectedApplication(String deviceSerialNumber) {
		Application service = getAppInstance().getSelectedApplication();
		if (service == null)
			return true;
		
		return isAvailableFor(deviceSerialNumber, service);
	}

	private void setDeviceAvailabilityFor(String deviceSerialNumber, Application service, boolean available) {
		if (service == null)
			return;
		
		synchronized (m_devicesPerApplication) {
			Set<String> deviceIds = m_devicesPerApplication.get(service);
			if (deviceIds == null) {
				deviceIds = new HashSet<String>();
				m_devicesPerApplication.put(service, deviceIds);
			}

			if (available) {
				if (!m_devices.containsKey(deviceSerialNumber))
					return;

				deviceIds.add(deviceSerialNumber);
			} else {
				deviceIds.remove(deviceSerialNumber);
			}
		}
	}

	@Override
	public void notifySelectedAppChanged(Application oldSelectServ,
			Application newSelectedServ) {
		recreateDeviceTable(newSelectedServ);
		
		synchronized (m_deviceSerialNumbers) {
			for (String deviceSerialNb : m_deviceSerialNumbers) {
				DeviceEntry entry = m_devices.get(deviceSerialNb);
				tableModel.addDeviceRow(entry);
				updateDeviceWidgetVisibility(entry);
			}
		}
	}

	private void updateDeviceWidgetVisibility(DeviceEntry entry) {
		removeDeviceWidget(entry);
		
		final String deviceSerialNumber = entry.serialNumber;
		if (isAvailableForSelectedApplication(deviceSerialNumber)) {
			GenericDevice device = getAppInstance().getDeviceBySerialNumber(deviceSerialNumber);
			addDeviceWidget(device, entry.description, entry.position, deviceSerialNumber, entry);
		}
	}

}
