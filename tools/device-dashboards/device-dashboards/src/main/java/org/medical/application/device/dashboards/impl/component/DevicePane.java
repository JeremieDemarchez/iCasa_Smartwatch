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
package org.medical.application.device.dashboards.impl.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

import org.apache.felix.ipojo.Factory;
import org.medical.application.Application;
import org.medical.application.device.dashboards.impl.MedicalHouseSimulatorImpl;
import org.medical.application.device.dashboards.impl.component.event.DropEvent;
import org.medical.application.device.dashboards.impl.component.event.DropListener;
import org.medical.application.device.dashboards.portlet.impl.GenericDeviceStatusWindow;
import org.medical.device.manager.ApplicationDevice;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.power.PowerSwitchmeter;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
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

	private final ActionPane m_parent;

	private final Map<String, DeviceEntry> m_devices = new HashMap<String, DeviceEntry>();

	protected DeviceTableModel tableModel;

	protected List<String> m_deviceSerialNumbers = new ArrayList<String>();

	private Map<Application, Set<String /* device id */>> m_devicesPerApplication = new HashMap<Application, Set<String /*
																																								 * device
																																								 * id
																																								 */>>();

	private static boolean[] BORDER_POSITIONS = new boolean[20];

	private Table m_deviceTable;

	protected Grid m_grid;

	private TextField m_description;
	private DropDownMenu m_factory;
	private final Map<String, Factory> m_deviceFactories = new HashMap<String, Factory>();
	private final Random m_random = new Random();

	public DevicePane(final ActionPane parent) {
		m_parent = parent;

		m_grid = new Grid(3);
		m_grid.setInsets(new Insets(2, 3));

		/*
		
		if (getAppInstance().isSimulator()) {

			final Label image = new Label(new ResourceImageReference(BIG_DEVICE_IMAGE.getResource(), new Extent(50),
			      new Extent(50)));
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

			m_grid.add(image);
			m_grid.add(m_factory);
			m_grid.add(m_description);
			m_grid.add(addDeviceButton);

		}
		
		*/

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

		tableModel = createTableModel(service);

		Table aTable = new Table(tableModel);
		aTable.setBorder(new Border(1, Color.LIGHTGRAY, Border.STYLE_SOLID));
		aTable.setInsets(new Insets(3, 1));
		aTable.setDefaultRenderer(Object.class, new DeviceTableCellRenderer());
		aTable.setDefaultHeaderRenderer(new DeviceHeaderTableCellRenderer());

		return aTable;
	}

	protected DeviceTableModel createTableModel(Application service) {
		DeviceTableModel model = null;
		if (service == null)
			model = new HomeDeviceTableModel(0);
		else if (service.getId().startsWith("Safe"))
			model = new ServiceWithPropDeviceTableModel(0);
		else
			model = new ServiceWithoutPropDeviceTableModel(0);
		return model;
	}
	
	public void addDeviceFactory(Factory factory) {
	}

	public void removeDeviceFactory(Factory factory) {
	}

	/**
	 * Adds a device "reference" into the GUI simulator application
	 * 
	 * @param device
	 *           device instance to be added
	 * @param properties
	 *           properties of the device service
	 */
	public void addDevice(final ApplicationDevice device, Map<String, Object> properties) {
		DeviceEntry entry = getDeviceEntry(device, properties);

		// manage addition
		if (m_devices.containsKey(entry.serialNumber)) {
			showErrorWindow("The device \"" + entry.serialNumber + "\"already exists.");
			return;
		}

		// Creating and adding the house pane button (icon)
		// addDeviceWidget(device, entry.description, entry.position,
		// entry.serialNumber, entry);
		addDeviceWidget(entry);
		synchronized (m_deviceSerialNumbers) {
			m_devices.put(entry.serialNumber, entry);
			m_deviceSerialNumbers.add(entry.serialNumber);
		}

		// set it as unavailable for all services by default
		for (Application service : getAppInstance().getAppMgr().getApplications()) {
			boolean available = service.getId().startsWith("Digital Home Simulator");
			setDeviceAvailabilityFor(entry.serialNumber, service, available);
		}

		// Add device to the table
		addDeviceInTable(entry);

	}

	/**
	 * Builds a device entry instance (gui information) from a device object
	 * 
	 * @param device
	 *           the device object
	 * @param properties
	 *           the device properties
	 * @return the device entry instance (gui object)
	 */
	private DeviceEntry getDeviceEntry(ApplicationDevice device, Map<String, Object> properties) {
		String description = (String) properties.get(Constants.SERVICE_DESCRIPTION);
		if (description == null) {
			// If service description is not defined, use the device serial number.
			description = device.getId();
		}

		Position position = getAppInstance().getSimulationManager().getDevicePosition(device.getId());

		if (position == null) {
			position = generateBorderPosition();
		}
		final String serialNumber = device.getId();

		String state = (String) properties.get("state");
		if (state == null)
			state = "unknown";

		String fault = (String) properties.get("fault");
		if (fault == null)
			fault = "uknown";

		String logicPosition = getAppInstance().getSimulationManager().getEnvironmentFromPosition(position);
		if (logicPosition == null)
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

	protected void addDeviceWidget(final DeviceEntry entry) {

		ApplicationDevice device2 = getAppInstance().getDeviceBySerialNumber(entry.serialNumber);
		ResourceImageReference imageForDevice = getImageForDevice(device2);

		entry.widget = new FloatingButton(entry.position.x, entry.position.y, imageForDevice, entry.description);
		entry.widget.setActionCommand(entry.serialNumber);
		entry.widget.addActionListener(new DeviceSpotActionListener());

		// add support of drag and drop
		final FloatingButtonDragSource dragSource = new FloatingButtonDragSource(entry.widget);
		dragSource.setBackground(Color.YELLOW);
		dragSource.addDropTarget(HousePane.HOUSE_PANE_RENDER_ID);
		dragSource.addDropListener(new DropListener() {

			@Override
			public void dropPerformed(DropEvent event) {
				getAppInstance().getSimulationManager().setDevicePosition(entry.serialNumber,
				      new Position(event.getTargetX(), event.getTargetY()));
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
	 * 
	 * @param device
	 *           Device instance to be removed
	 */
	public void removeDevice(ApplicationDevice device) {

		String serialNumber = device.getId();
		removeDeviceFromTable(serialNumber);

		DeviceEntry entry = m_devices.get(serialNumber);
		synchronized (m_deviceSerialNumbers) {
			m_devices.remove(serialNumber);
			m_deviceSerialNumbers.remove(entry.serialNumber);
		}

		removeDeviceWidget(entry);
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

		String logicPosition = getAppInstance().getSimulationManager().getEnvironmentFromPosition(position);
		if (logicPosition == null)
			logicPosition = "unassigned";

		if (entry.position.equals(position) && logicPosition.equals(entry.logicPosition))
			return;

		entry.position = position;
		entry.logicPosition = logicPosition;

		// re-render
		removeDeviceWidget(entry);

		if (isAvailableForSelectedApplication(deviceSerialNumber))
			// addDeviceWidget(getAppInstance().getDeviceBySerialNumber(deviceSerialNumber),
			// entry.description, position, deviceSerialNumber, entry);
			addDeviceWidget(entry);

		updateDeviceTable(deviceSerialNumber, getAppInstance().getDeviceBySerialNumber(deviceSerialNumber));
	}

	protected void showErrorWindow(final String error) {
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
	public void changeDevice(String deviceSerialNumber, ApplicationDevice device, Map<String, Object> properties) {
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
			// addDeviceWidget(device, entry.description, entry.position,
			// deviceSerialNumber, entry);
			addDeviceWidget(entry);

		updateDeviceTable(deviceSerialNumber, device);
	}

	private void updateDeviceTable(String deviceSerialNumber, ApplicationDevice applicationDevice) {
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
	 * @param device2
	 * @return
	 */
	private ResourceImageReference getImageForDevice(final ApplicationDevice device) {
		ResourceImageReference image = getAppInstance().getImageForDevice(device);
		if (image != null)
			return image;

		// This code is only use to force BND to import the package
		if (device instanceof DimmerLight) {
			new ResourceImageReference("/DimmerLamp.png");
		} else if (device instanceof BinaryLight) {
			new ResourceImageReference("/Lamp.png");
		} else if (device instanceof Thermometer) {
			new ResourceImageReference("/Thermometer.png");
		} else if (device instanceof Heater) {
			new ResourceImageReference("/Heater.png");
		} else if (device instanceof AudioSource) {
			new ResourceImageReference("/Player.png");
		} else if (device instanceof PresenceSensor) {
			new ResourceImageReference("/Player.png");
		} else if (device instanceof PowerSwitchmeter) {
			new ResourceImageReference("/Player.png");			
		} else {
			new ResourceImageReference("/Device-Icasa.png");
		}

		return new ResourceImageReference("/Device-Icasa.png");

	}

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
				m_parent.setActiveTabIndex(1);
		}

	}

	private SelectField createLocationList(String deviceSerialNumber, String deviceLocation) {
		final SelectField locationField = new SelectField();

		DefaultListModel model = new DefaultListModel();
		final SimulationManager manager = getAppInstance().getSimulationManager();
		Set<String> environments = manager.getEnvironments();
		String[] locationStrs = environments.toArray(new String[environments.size()]);
		int deviceLocationIdx = -1;
		for (int idx = 0; idx < locationStrs.length; idx++) {
			String location = locationStrs[idx];
			model.add(location);
			if ((deviceLocation != null) && (deviceLocation.equals(location)))
				deviceLocationIdx = idx;
		}
		model.add("unassigned");
		locationField.setModel(model);
		if (deviceLocationIdx == -1)
			deviceLocationIdx = environments.size();

		locationField.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		if (deviceLocation != null)
			locationField.getSelectionModel().setSelectedIndex(deviceLocationIdx, true);

		StringListActionListener locationMenuActionListener = new StringListActionListener(deviceSerialNumber,
		      locationField) {
			protected void performSet(String newValueId) {
				getAppInstance().getSimulationManager().setDeviceLocation(deviceSerialNumber, newValueId);
			}
		};

		locationField.addActionListener(locationMenuActionListener);

		return locationField;
	}

	private SelectField createStateList(String deviceSerialNumber, String deviceState) {
		final SelectField stateField = new SelectField();

		DefaultListModel model = new DefaultListModel();
		String[] states = { "activated", "deactivated" };
		int deviceStateIdx = -1;
		for (int idx = 0; idx < states.length; idx++) {
			String state = states[idx];
			model.add(state);
			if ((deviceState != null) && (deviceState.equals(state)))
				deviceStateIdx = idx;
		}
		stateField.setModel(model);
		if (deviceStateIdx == -1)
			deviceStateIdx = 0; // by default activated

		stateField.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		if (deviceState != null)
			stateField.getSelectionModel().setSelectedIndex(deviceStateIdx, true);

		StringListActionListener locationMenuActionListener = new StringListActionListener(deviceSerialNumber, stateField) {
			protected void performSet(String newValueId) {
				ApplicationDevice device = getAppInstance().getDeviceBySerialNumber(deviceSerialNumber);
				GenericDevice genericDevice = (GenericDevice) device.getDeviceProxy(GenericDevice.class);
				genericDevice.setState(newValueId);
			}
		};

		stateField.addActionListener(locationMenuActionListener);

		return stateField;
	}

	private SelectField createUsedList(final String deviceSerialNumber, Boolean value) {
		final SelectField stateField = new SelectField();

		DefaultListModel model = new DefaultListModel();
		final String[] states = { "yes", "no" };
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

	private SelectField createFaultList(String deviceSerialNumber, String deviceState) {
		final SelectField stateField = new SelectField();

		DefaultListModel model = new DefaultListModel();
		String[] states = { "yes", "no", "unknown" };
		int deviceStateIdx = -1;
		for (int idx = 0; idx < states.length; idx++) {
			String state = states[idx];
			model.add(state);
			if ((deviceState != null) && (deviceState.equals(state)))
				deviceStateIdx = idx;
		}
		stateField.setModel(model);
		if (deviceStateIdx == -1)
			deviceStateIdx = 2; // by default unknown

		stateField.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ApplicationDevice device = getAppInstance().getDeviceBySerialNumber(deviceSerialNumber);

		if (deviceState != null)
			stateField.getSelectionModel().setSelectedIndex(deviceStateIdx, true);

		StringListActionListener locationMenuActionListener = new StringListActionListener(deviceSerialNumber, stateField) {
			protected void performSet(String newValueId) {
				ApplicationDevice device = getAppInstance().getDeviceBySerialNumber(deviceSerialNumber);
				GenericDevice genericDevice = (GenericDevice) device.getDeviceProxy(GenericDevice.class);
				genericDevice.setFault(newValueId);
			}
		};

		stateField.addActionListener(locationMenuActionListener);

		return stateField;
	}
	
	private SelectField createNonEditableFaultList(String deviceSerialNumber, String deviceState) {
		SelectField faultStateField = createFaultList(deviceSerialNumber, deviceState);
		faultStateField.setEnabled(false);
		return faultStateField;
	}

	class DeviceTableCellRenderer implements TableCellRenderer {

		/**
	    * 
	    */
		private static final long serialVersionUID = -2146113024393976876L;
		
		int LOCATION_COLUMN_INDEX = 1;
		int STATE_COLUMN_INDEX = 2;
		int FAULT_COLUMN_INDEX = 3;
		int DETAIL_COLUMN_INDEX = 4;
		int DELETE_COLUMN_INDEX = 5;
		
		

		@Override
		public Component getTableCellRendererComponent(Table table, final Object value, int column, int row) {
			if (value == null) {
				return null;
			}
			
			final DeviceTableModel deviceTableModel = (DeviceTableModel) table.getModel();
			String deviceSerialNumber = deviceTableModel.getDeviceSerialNumber(row);

			if (column == LOCATION_COLUMN_INDEX) {
				return createLocationList(deviceSerialNumber, (String) value);
			}
			if (column == STATE_COLUMN_INDEX) {
				if (deviceTableModel.useState())
					return createStateList(deviceSerialNumber, (String) value);
				else
					return createUsedList(deviceSerialNumber, (Boolean) value);
			}

			if (column == FAULT_COLUMN_INDEX) {
				if (deviceTableModel.useEditableFault())
					return createFaultList(deviceSerialNumber, (String) value);
				else
					return createNonEditableFaultList(deviceSerialNumber, (String) value);
			}
			if (column == DETAIL_COLUMN_INDEX) {
				Button detailButton = new Button("...");

				detailButton.setActionCommand(value.toString());
				detailButton.addActionListener(new DeviceSpotActionListener());
				return detailButton;
			}
			if (column == DELETE_COLUMN_INDEX) {
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

			return new Label(value.toString());
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

		public StringListActionListener(String deviceSerialNumber, SelectField field) {
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
	public void notifySelectedAppChanged(Application oldSelectServ, Application newSelectedServ) {
		recreateDeviceTable(newSelectedServ);

		synchronized (m_deviceSerialNumbers) {
			for (String deviceSerialNb : m_deviceSerialNumbers) {
				DeviceEntry entry = m_devices.get(deviceSerialNb);
				tableModel.addDeviceRow(entry);
				updateDeviceWidgetVisibility(entry);
			}
		}
	}

	public void refreshDeviceWidgets() {
		synchronized (m_deviceSerialNumbers) {
			for (String deviceSerialNb : m_deviceSerialNumbers) {
				DeviceEntry entry = m_devices.get(deviceSerialNb);
				updateDeviceWidgetVisibility(entry);
			}
		}
	}

	private void updateDeviceWidgetVisibility(DeviceEntry entry) {
		removeDeviceWidget(entry);

		final String deviceSerialNumber = entry.serialNumber;
		if (isAvailableForSelectedApplication(deviceSerialNumber)) {
			ApplicationDevice device = getAppInstance().getDeviceBySerialNumber(deviceSerialNumber);
			addDeviceWidget(entry);
		}
	}



	static class DeviceEntry {
		protected String serialNumber;
		protected Label label;
		protected FloatingButton widget;
		protected Position position;
		protected String logicPosition;
		protected String state;
		protected String fault;
		protected FloatingButtonDragSource dragSource;
		protected String description;
	}

	/**
	 * Abstract (default) model to show devices in a echo3 table
	 * 
	 * @author Gabriel
	 * 
	 */
	public abstract class DeviceTableModel extends DefaultTableModel {

		protected List<String> deviceSerialNumbers = new ArrayList<String>();

		public DeviceTableModel(int cols, int rows) {
			super(cols, rows);
		}

		public String getDeviceSerialNumber(int row) {
			return deviceSerialNumbers.get(row);
		}
		

		@Override
		public Object getValueAt(int column, int row) {
			return super.getValueAt(column, row);
		}

		public synchronized void removeDeviceRow(String serialNumber) {
			int rowIdx = deviceSerialNumbers.indexOf(serialNumber);
			if (rowIdx >= 0) {
				deviceSerialNumbers.remove(rowIdx);
				deleteRow(rowIdx);
			}
		}
		
		public abstract void updateDeviceRow(DeviceEntry entry);

		public abstract void addDeviceRow(DeviceEntry entry);

		public abstract boolean useState();
		
		public abstract boolean useEditableFault();
		
		
	}

	/**
	 * Table model to show all devices presents in the platform using echo3
	 * 
	 * @author Gabriel
	 * 
	 */
	public class HomeDeviceTableModel extends DeviceTableModel {

		private final String[] columns = { "Device Name", "Location", "Usable *", "Fault", "Details" };

		static final int DEVICE_DESC_COL_IDX = 0;
		static final int DEVICE_LOCATION_COL_IDX = 1;
		static final int DEVICE_USABLE_STATE_COL_IDX = 2;
		static final int DEVICE_FAULT_STATE_COL_IDX = 3;

		public HomeDeviceTableModel(int rows) {
			super(5, rows);
			for (int i = 0; i < columns.length; i++) {
				setColumnName(i, columns[i]);
			}
		}

		public void updateDeviceRow(DeviceEntry entry) {
			int rowIdx = deviceSerialNumbers.indexOf(entry.serialNumber);
			if (rowIdx < 0)
				return;

			setValueAt(entry.label.getText(), DEVICE_DESC_COL_IDX, rowIdx);
			setValueAt(entry.logicPosition, DEVICE_LOCATION_COL_IDX, rowIdx);
			setValueAt(entry.state, DEVICE_USABLE_STATE_COL_IDX, rowIdx);
			setValueAt(entry.fault, DEVICE_FAULT_STATE_COL_IDX, rowIdx);
		}

		public synchronized void addDeviceRow(DeviceEntry entry) {
			String data[] = new String[columns.length];
			data[DEVICE_DESC_COL_IDX] = entry.label.getText();
			data[DEVICE_LOCATION_COL_IDX] = entry.logicPosition;
			data[DEVICE_USABLE_STATE_COL_IDX] = entry.state;
			data[DEVICE_FAULT_STATE_COL_IDX] = entry.fault;
			data[columns.length - 1] = entry.serialNumber;
			deviceSerialNumbers.add(entry.serialNumber);
			addRow(data);
		}

		public boolean useState() {
			return true;
		}

		@Override
      public boolean useEditableFault() {
	      return false;
      }


	}

	/**
	 * Table model to show devices (with properties) by application using echo3
	 * 
	 * @author Gabriel
	 * 
	 */
	public class ServiceWithPropDeviceTableModel extends DeviceTableModel {

		private final String[] columns = { "Device Description", "Location *", "Used *", "Fault", "Details" };

		static final int DEVICE_DESC_COL_IDX = 0;
		static final int DEVICE_LOCATION_COL_IDX = 1;
		static final int DEVICE_STATE_COL_IDX = 2;
		static final int DEVICE_FAULT_STATE_COL_IDX = 3;

		public ServiceWithPropDeviceTableModel(int rows) {
			super(5, rows);
			for (int i = 0; i < columns.length; i++) {
				setColumnName(i, columns[i]);
			}
		}

		public void updateDeviceRow(DeviceEntry entry) {
			int rowIdx = deviceSerialNumbers.indexOf(entry.serialNumber);
			if (rowIdx < 0)
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
			data[columns.length - 1] = entry.serialNumber;
			deviceSerialNumbers.add(entry.serialNumber);
			addRow(data);
		}
		
		public boolean useState() {
			return false;
		}

		@Override
      public boolean useEditableFault() {
	      return false;
      }
	}

	/**
	 * Table model to show devices (with properties) by application using echo3
	 * 
	 * @author Gabriel
	 * 
	 */
	public class ServiceWithoutPropDeviceTableModel extends DeviceTableModel {

		private final String[] columns = { "Device Description", "Location", "Used *", "Fault", "Details" };

		static final int DEVICE_DESC_COL_IDX = 0;
		static final int DEVICE_LOCATION_COL_IDX = 1;
		static final int DEVICE_STATE_COL_IDX = 2;
		static final int DEVICE_FAULT_STATE_COL_IDX = 3;

		public ServiceWithoutPropDeviceTableModel(int rows) {
			super(5, rows);
			for (int i = 0; i < columns.length; i++) {
				setColumnName(i, columns[i]);
			}
		}

		public void updateDeviceRow(DeviceEntry entry) {
			int rowIdx = deviceSerialNumbers.indexOf(entry.serialNumber);
			if (rowIdx < 0)
				return;

			setValueAt(entry.label.getText(), DEVICE_DESC_COL_IDX, rowIdx);
			setValueAt(entry.logicPosition, DEVICE_LOCATION_COL_IDX, rowIdx);
			setValueAt(isAvailableForSelectedApplication(entry.serialNumber), DEVICE_STATE_COL_IDX, rowIdx);
			setValueAt(entry.fault, DEVICE_FAULT_STATE_COL_IDX, rowIdx);
		}

		public synchronized void addDeviceRow(DeviceEntry entry) {
			Object data[] = new Object[columns.length];
			data[DEVICE_DESC_COL_IDX] = entry.label.getText();
			data[DEVICE_LOCATION_COL_IDX] = entry.logicPosition;
			data[DEVICE_STATE_COL_IDX] = isAvailableForSelectedApplication(entry.serialNumber);
			data[DEVICE_FAULT_STATE_COL_IDX] = entry.fault;
			data[columns.length - 1] = entry.serialNumber;
			deviceSerialNumbers.add(entry.serialNumber);
			addRow(data);
		}
		
		public boolean useState() {
			return false;
		}

		@Override
      public boolean useEditableFault() {
	      return false;
      }
	}


}
