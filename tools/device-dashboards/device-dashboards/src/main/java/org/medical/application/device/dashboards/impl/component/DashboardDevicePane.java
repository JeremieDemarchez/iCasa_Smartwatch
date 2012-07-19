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
import java.util.Set;

import nextapp.echo.app.Component;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.SelectField;
import nextapp.echo.app.Table;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.list.DefaultListModel;
import nextapp.echo.app.list.ListSelectionModel;

import org.medical.application.Application;
import org.medical.application.device.dashboards.impl.DashboardApplicationImpl;
import org.medical.application.device.web.common.impl.component.DeviceEntry;
import org.medical.application.device.web.common.impl.component.DevicePane;
import org.medical.device.manager.ApplicationDevice;

/**
 * TODO comments.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public class DashboardDevicePane extends DevicePane implements SelectedApplicationTracker {

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 3778184066500074285L;

	public static ResourceImageReference DEVICE_IMAGE = new ResourceImageReference("/Device.png");
	public static ResourceImageReference BIG_DEVICE_IMAGE = new ResourceImageReference("/BigDevice.png");

	// private final DashboardActionPane m_parent;

	private final Map<String, DeviceEntry> m_devices = new HashMap<String, DeviceEntry>();

	protected DeviceTableModel tableModel;

	protected List<String> m_deviceSerialNumbers = new ArrayList<String>();

	private Map<Application, Set<String /* device id */>> m_devicesPerApplication = new HashMap<Application, Set<String /*
																																								 * device
																																								 * id
																																								 */>>();
	/**
	 * Selected service in pane
	 */
	private Application service;

	// private static boolean[] BORDER_POSITIONS = new boolean[20];

	// private Table m_deviceTable;

	// protected Grid m_grid;

	// private TextField m_description;
	// private DropDownMenu m_factory;
	// private final Map<String, Factory> m_deviceFactories = new HashMap<String,
	// Factory>();
	// private final Random m_random = new Random();

	public DashboardDevicePane(DashboardActionPane parent) {
		super(parent);
	}

	@Override
	protected void initContent() {
		m_grid = new Grid(3);
		m_grid.setInsets(new Insets(2, 3));
		recreateDeviceTable();
		add(m_grid);
	}

	@Override
	public void notifySelectedAppChanged(Application oldSelectServ, Application newSelectedServ) {
		service = newSelectedServ;
		recreateDeviceTable();
		synchronized (m_deviceSerialNumbers) {
			for (String deviceSerialNb : m_deviceSerialNumbers) {
				DeviceEntry entry = m_devices.get(deviceSerialNb);
				tableModel.addDeviceRow(entry);
				updateDeviceWidgetVisibility(entry);
			}
		}

		// TODO: Reimplement this code
		/*
		 * synchronized (m_deviceSerialNumbers) { for (String deviceSerialNb :
		 * m_deviceSerialNumbers) { DeviceEntry entry =
		 * m_devices.get(deviceSerialNb); tableModel.addDeviceRow(entry);
		 * updateDeviceWidgetVisibility(entry); } }
		 */

	}

	public void refreshDeviceWidgets() {
		synchronized (m_deviceSerialNumbers) {
			for (String deviceSerialNb : m_deviceSerialNumbers) {
				DeviceEntry entry = m_devices.get(deviceSerialNb);
				updateDeviceWidgetVisibility(entry);
			}
		}
	}

	@Override
	protected DeviceTableModel createTableModel() {
		DeviceTableModel model = null;
		if (service == null)
			model = new HomeDeviceTableModel(0);
		else if (service.getId().startsWith("Safe"))
			model = new ServiceWithPropDeviceTableModel(0);
		else
			model = new ServiceWithoutPropDeviceTableModel(0);
		return model;
	}

	@Override
	protected DeviceTableCellRenderer createTableCellRenderer() {
		return new DashboardDeviceTableRenderer();
	}

	@Override
	public boolean deviceMustBeShown(String deviceSerialNumber) {
		Application service = ((DashboardApplicationImpl) getAppInstance()).getSelectedApplication();
		if (service == null)
			return true;

		return isAvailableFor(deviceSerialNumber, service);
	}

	/**
	 * Determines if a device is available for a device in a determinate
	 * application
	 * 
	 * @param deviceSerialNumber the device serial number
	 * @param service the application
	 * @return
	 */
	private boolean isAvailableFor(String deviceSerialNumber, Application service) {
		if (service == null)
			return true;

		Set<String> deviceIds = m_devicesPerApplication.get(service);
		if (deviceIds == null)
			return false;

		return (deviceIds.contains(deviceSerialNumber));
	}

	/**
	 * Modifies the availability for a device into the application
	 * 
	 * @param deviceSerialNumber the device serial number
	 * @param service the application
	 * @param available a boolean indicating availability
	 */
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

	public void updateDeviceWidgetVisibility(DeviceEntry entry) {
		removeDevice(entry);

		final String deviceSerialNumber = entry.serialNumber;
		if (deviceMustBeShown(deviceSerialNumber)) {
			// ApplicationDevice device =
			// ((DashboardApplicationImpl)getAppInstance()).getDeviceBySerialNumber(deviceSerialNumber);
			addDeviceWidget(entry);
		}
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

				Application selectedApplication = ((DashboardApplicationImpl) getAppInstance()).getSelectedApplication();

				setDeviceAvailabilityFor(deviceSerialNumber, selectedApplication, available);
				tableModel.updateDeviceRow(entry);
				updateDeviceWidgetVisibility(entry);
			}
		};

		stateField.addActionListener(stateMenuActionListener);

		return stateField;
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
			setValueAt(deviceMustBeShown(entry.serialNumber), DEVICE_STATE_COL_IDX, rowIdx);
			setValueAt(entry.logicPosition, DEVICE_LOCATION_COL_IDX, rowIdx);
			setValueAt(entry.fault, DEVICE_FAULT_STATE_COL_IDX, rowIdx);
		}

		public synchronized void addDeviceRow(DeviceEntry entry) {
			Object data[] = new Object[columns.length];
			data[DEVICE_DESC_COL_IDX] = entry.label.getText();
			data[DEVICE_LOCATION_COL_IDX] = entry.logicPosition;
			data[DEVICE_STATE_COL_IDX] = deviceMustBeShown(entry.serialNumber);
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
			setValueAt(deviceMustBeShown(entry.serialNumber), DEVICE_STATE_COL_IDX, rowIdx);
			setValueAt(entry.fault, DEVICE_FAULT_STATE_COL_IDX, rowIdx);
		}

		public synchronized void addDeviceRow(DeviceEntry entry) {
			Object data[] = new Object[columns.length];
			data[DEVICE_DESC_COL_IDX] = entry.label.getText();
			data[DEVICE_LOCATION_COL_IDX] = entry.logicPosition;
			data[DEVICE_STATE_COL_IDX] = deviceMustBeShown(entry.serialNumber);
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

	public class DashboardDeviceTableRenderer extends DeviceTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(Table table, Object value, int column, int row) {
			Component component = super.getTableCellRendererComponent(table, value, column, row);
			final DeviceTableModel deviceTableModel = (DeviceTableModel) table.getModel();
			String deviceSerialNumber = deviceTableModel.getDeviceSerialNumber(row);

			if (column == STATE_COLUMN_INDEX) {
				if (deviceTableModel.useState())
					component = createStateList(deviceSerialNumber, (String) value);
				else
					component = createUsedList(deviceSerialNumber, (Boolean) value);
			}
			if (column == FAULT_COLUMN_INDEX) {
				if (deviceTableModel.useEditableFault())
					component = createFaultList(deviceSerialNumber, (String) value);
				else
					component = createNonEditableFaultList(deviceSerialNumber, (String) value);
			}
			return component;
		}
	}

}
