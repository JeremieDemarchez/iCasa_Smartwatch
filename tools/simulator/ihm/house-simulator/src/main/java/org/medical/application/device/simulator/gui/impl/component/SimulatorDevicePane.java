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
package org.medical.application.device.simulator.gui.impl.component;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import nextapp.echo.app.Button;
import nextapp.echo.app.Component;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Table;
import nextapp.echo.app.TextField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.GridLayoutData;
import nextapp.echo.extras.app.DropDownMenu;
import nextapp.echo.extras.app.menu.DefaultMenuModel;
import nextapp.echo.extras.app.menu.DefaultMenuSelectionModel;
import nextapp.echo.extras.app.menu.DefaultOptionModel;
import nextapp.echo.extras.app.menu.ItemModel;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.medical.application.device.web.common.impl.component.DeviceEntry;
import org.medical.application.device.web.common.impl.component.DevicePane;
import org.medical.application.device.web.common.impl.component.DevicePane.DeviceTableCellRenderer;
import org.medical.application.device.web.common.impl.component.DevicePane.DeviceTableModel;
import org.osgi.framework.Constants;

import fr.liglab.adele.icasa.device.GenericDevice;

public class SimulatorDevicePane extends DevicePane {

	/**
	 * Serial Version UID for Serializable class
	 */
	private static final long serialVersionUID = 8910837590839771921L;

	private TextField m_description;
	private DropDownMenu m_factory;
	private final Map<String, Factory> m_deviceFactories = new HashMap<String, Factory>();
	private final Random m_random = new Random();

	public SimulatorDevicePane(SimulatorActionPane parent) {
		super(parent);
	}

	@Override
	protected void initContent() {
		m_grid = new Grid(3);
		m_grid.setInsets(new Insets(2, 3));
		
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
		
		recreateDeviceTable();		
		add(m_grid);
	}
	
	
	public void addDeviceFactory(Factory factory) {
		if (m_factory != null) {
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
	}

	public void removeDeviceFactory(Factory factory) {
		if (m_factory != null) {
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
	}

	/**
	 * Creates a device instance
	 * 
	 * @param factoryName
	 *           Name of the iPOJO factory to be used
	 * @param description
	 *           Description of the instance
	 * @param x
	 *           X position
	 * @param y
	 *           Y position
	 * @throws UnacceptableConfiguration
	 * @throws MissingHandlerException
	 * @throws ConfigurationException
	 */
	protected synchronized void createDeviceInstance(String factoryName, String description)
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
		// getAppInstance().getSimulationManager().setDevicePosition(serialNumber,
		// new Position(x, y));
	}


	@Override
	protected DeviceTableModel createTableModel() {
		return new SimulatedDeviceTableModel(0);
	};

	@Override
	protected DeviceTableCellRenderer createTableCellRenderer() {
		return new SimulatorDeviceTableRenderer();
	}
	
	@Override
	protected boolean deviceMustBeShown(String deviceSerialNumber) {
	   return true;
	}
	
	public class SimulatedDeviceTableModel extends DeviceTableModel {

		/**
		 * Serial Version UID for Serializable class
		 */
		private static final long serialVersionUID = 4559599690425279667L;

		private final String[] columns = { "Device", "Location", "State", "Fault", "Details", "Remove" };

		static final int DEVICE_DESC_COL_IDX = 0;
		static final int DEVICE_LOCATION_COL_IDX = 1;
		static final int DEVICE_USABLE_STATE_COL_IDX = 2;
		static final int DEVICE_FAULT_STATE_COL_IDX = 3;

		public SimulatedDeviceTableModel(int rows) {
			super(6, rows);
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
			data[columns.length - 2] = entry.serialNumber;
			data[columns.length - 1] = entry.serialNumber;
			deviceSerialNumbers.add(entry.serialNumber);
			tableModel.addRow(data);
		}

		@Override
		public boolean useState() {
			return true;
		}

		@Override
		public boolean useEditableFault() {
			return true;
		}

	}

	public class SimulatorDeviceTableRenderer extends DeviceTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(Table table, Object value, int column, int row) {
			Component component = super.getTableCellRendererComponent(table, value, column, row);
			final DeviceTableModel deviceTableModel = (DeviceTableModel) table.getModel();
			String deviceSerialNumber = deviceTableModel.getDeviceSerialNumber(row);

			if (column == STATE_COLUMN_INDEX)
				component = createStateList(deviceSerialNumber, (String) value);

			if (column == FAULT_COLUMN_INDEX)
				component = createFaultList(deviceSerialNumber, (String) value);

			return component;
		}
	}



}
