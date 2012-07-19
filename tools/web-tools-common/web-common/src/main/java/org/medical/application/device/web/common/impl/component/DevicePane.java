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
package org.medical.application.device.web.common.impl.component;

import java.util.ArrayList;
import java.util.List;
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
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.GridLayoutData;
import nextapp.echo.app.list.DefaultListModel;
import nextapp.echo.app.list.ListSelectionModel;
import nextapp.echo.app.table.DefaultTableModel;
import nextapp.echo.app.table.TableCellRenderer;

import org.medical.application.device.web.common.impl.BaseHouseApplication;
import org.medical.application.device.web.common.impl.component.event.DropEvent;
import org.medical.application.device.web.common.impl.component.event.DropListener;
import org.medical.application.device.web.common.portlet.impl.GenericDeviceStatusWindow;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;

/**
 * TODO comments.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public abstract class DevicePane extends ContentPane {

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 3778184066500074285L;

	public static ResourceImageReference DEVICE_IMAGE = new ResourceImageReference("/Device.png");
	public static ResourceImageReference BIG_DEVICE_IMAGE = new ResourceImageReference("/BigDevice.png");

	private final ActionPane m_parent;

	protected DeviceTableModel tableModel;

	private Table m_deviceTable;

	protected Grid m_grid;

	public DevicePane(final ActionPane parent) {
		m_parent = parent;
		initContent();
	}
	


	protected void recreateDeviceTable() {
		if (m_deviceTable != null)
			m_grid.remove(m_deviceTable);

		final GridLayoutData gridLayout = new GridLayoutData();
		gridLayout.setColumnSpan(3);
		m_deviceTable = createTable();
		m_deviceTable.setLayoutData(gridLayout);
		m_grid.add(m_deviceTable);
	}

	private Table createTable() {

		tableModel = createTableModel();

		Table aTable = new Table(tableModel);
		aTable.setBorder(new Border(1, Color.LIGHTGRAY, Border.STYLE_SOLID));
		aTable.setInsets(new Insets(3, 1));
		aTable.setDefaultRenderer(Object.class, createTableCellRenderer());
		aTable.setDefaultHeaderRenderer(new DeviceHeaderTableCellRenderer());

		return aTable;
	}

	/**
	 * Adds an device representation into the DevicePane
	 * 
	 * @param entry
	 */
	public void addDevice(DeviceEntry entry) {
		// Add icon in the house pane
		addDeviceWidget(entry);
		// Add device to the table
		addDeviceInTable(entry);
	}

	protected void addDeviceWidget(final DeviceEntry entry) {

		// ApplicationDevice device2 =
		// getAppInstance().getDeviceBySerialNumber(entry.serialNumber);
		ResourceImageReference imageForDevice = getImageForDevice(entry.serialNumber);

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

	/**
	 * Adds an device representation into the table
	 * 
	 * @param entry
	 */
	private void addDeviceInTable(final DeviceEntry entry) {
		tableModel.addDeviceRow(entry);
	}

	/**
	 * Removes a device representation into the Device pane
	 * 
	 * @param entry
	 */
	public void removeDevice(DeviceEntry entry) {
		removeDeviceFromTable(entry.serialNumber);
		removeDeviceWidget(entry);
	}

	/**
	 * Removes the device icon from the House Pane
	 * 
	 * @param entry
	 */
	private void removeDeviceWidget(DeviceEntry entry) {
		getAppInstance().getHousePane().getChildContainer().remove(entry.dragSource);
	}

	/**
	 * 
	 * @param serialNumber
	 */
	private void removeDeviceFromTable(String serialNumber) {
		tableModel.removeDeviceRow(serialNumber);
	}

	public void moveDevice(DeviceEntry entry, Position position) {

		/*
		
		if (((Extent) entry.widget.get(FloatingButton.PROPERTY_POSITION_X)).getValue() == 0 && position != null) { 			
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
		
		*/

		// re-render
		removeDeviceWidget(entry);

		
		if (deviceMustBeShown(entry.serialNumber))
			addDeviceWidget(entry);

		updateDeviceTable(entry);
	}

	/**
	 * Utility method to show a error message in the GUI
	 * 
	 * @param error
	 */
	public void showErrorWindow(final String error) {
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
	public void changeDevice(DeviceEntry entry) {
		removeDeviceWidget(entry);
		if (deviceMustBeShown(entry.serialNumber))
			addDeviceWidget(entry);
		updateDeviceTable(entry);
	}

	private void updateDeviceTable(DeviceEntry entry) {
		if (entry != null)
			tableModel.updateDeviceRow(entry);
	}

	/**
	 * Gets the current application instance
	 * 
	 * @return
	 */
	protected BaseHouseApplication getAppInstance() {
		return m_parent.getApplicationInstance();
	}

	private ResourceImageReference getImageForDevice(String deviceSerialNumber) {
		ResourceImageReference image = getAppInstance().getImageForDevice(deviceSerialNumber);
		if (image != null)
			return image;
		return new ResourceImageReference("/Device-Icasa.png");
	}


	/*
	private static Position generateBorderPosition() { // Find the next slot 
	  int i = nextSlotAvailable(); BORDER_POSITIONS[i] = true; return
	  new Position(20, (30 * i) + 20); }

	private static int nextSlotAvailable() {
		for (int i = 0; i < BORDER_POSITIONS.length; i++) {
			if (!BORDER_POSITIONS[i]) {
				return i;
			}
		}
		return BORDER_POSITIONS.length - 1;
	}
	*/

	private class DeviceSpotActionListener implements ActionListener {

		/**
         * 
         */
		private static final long serialVersionUID = -8163178014335773459L;

		@Override
		public void actionPerformed(ActionEvent e) {
			// Display the device in the status pane.

			String deviceId = e.getActionCommand();
			BaseHouseApplication app = getAppInstance();

			if (app.getStatusPane().getComponent(deviceId) != null) {
				return;
			}

			WindowPane widget = app.getDeviceWidget(deviceId);
			if (widget == null)
				widget = new GenericDeviceStatusWindow(app, deviceId);

			app.getStatusPane().add(widget);

			if (m_parent.getApplicationInstance().isAndroid())
				m_parent.setActiveTabIndex(1);
		}

	}

	/**
	 * 
	 * @param deviceSerialNumber
	 * @param deviceLocation
	 * @return
	 */
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

	/**
	 * 
	 * @param deviceSerialNumber
	 * @param deviceState
	 * @return
	 */
	protected SelectField createStateList(String deviceSerialNumber, String deviceState) {
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
				GenericDevice genericDevice = getAppInstance().getGenericDeviceBySerialNumber(deviceSerialNumber);
				if (genericDevice != null)
					genericDevice.setState(newValueId);
			}
		};

		stateField.addActionListener(locationMenuActionListener);

		return stateField;
	}

	/**
	 * 
	 * @param deviceSerialNumber
	 * @param deviceState
	 * @return
	 */
	protected SelectField createFaultList(String deviceSerialNumber, String deviceState) {
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


		if (deviceState != null)
			stateField.getSelectionModel().setSelectedIndex(deviceStateIdx, true);

		StringListActionListener locationMenuActionListener = new StringListActionListener(deviceSerialNumber, stateField) {
			protected void performSet(String newValueId) {
				GenericDevice genericDevice = getAppInstance().getGenericDeviceBySerialNumber(deviceSerialNumber);
				if (genericDevice != null)
					genericDevice.setFault(newValueId);
			}
		};

		stateField.addActionListener(locationMenuActionListener);

		return stateField;
	}

	protected SelectField createNonEditableFaultList(String deviceSerialNumber, String deviceState) {
		SelectField faultStateField = createFaultList(deviceSerialNumber, deviceState);
		faultStateField.setEnabled(false);
		return faultStateField;
	}

	// ----- Abstract methods delegated to concrete Device Panes ---- //

	/**
	 * Creates the instance of table model to be used in the device table
	 * 
	 * @return Devices table model
	 */
	protected abstract DeviceTableModel createTableModel();

	/**
	 * Creates an instance of the device table cell renderer
	 * @return
	 */
	protected abstract DeviceTableCellRenderer createTableCellRenderer();
	
	/**
	 * Initializes the GUI components
	 */
	protected abstract void initContent();
	
	
	/**
	 * Determines if the device must be shown in the house pane (house plan)
	 * @param deviceSerialNumber the device serial number
	 * @return
	 */
	protected abstract boolean deviceMustBeShown(String deviceSerialNumber);

	// ------ Utility classes used into the DevicePane ----- //

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
	
	/**
	 * Renders the cells of the table
	 * @author Gabriel
	 */
	public class DeviceTableCellRenderer implements TableCellRenderer {

		private static final long serialVersionUID = -2146113024393976876L;

		protected int LOCATION_COLUMN_INDEX = 1;
		protected int STATE_COLUMN_INDEX = 2;
		protected int FAULT_COLUMN_INDEX = 3;
		protected int DETAIL_COLUMN_INDEX = 4;
		protected int DELETE_COLUMN_INDEX = 5;

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

	/**
	 * Render the headers of the table
	 */
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

}
