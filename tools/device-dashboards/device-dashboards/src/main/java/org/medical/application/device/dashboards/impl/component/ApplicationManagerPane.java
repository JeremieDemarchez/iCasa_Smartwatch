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
import java.util.List;

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

import org.medical.application.Application;
import org.medical.application.ApplicationState;
import org.medical.application.device.dashboards.impl.WebDashboardApplicationImpl;
import org.medical.application.device.web.common.impl.MedicalHouseSimulatorImpl;


/**
 * TODO comments.
 * 
 * @author Thomas Leveque
 */
public class ApplicationManagerPane extends ContentPane  {	
	/**
	 * @Generated
	 */
	private static final long serialVersionUID = -4434232249787264347L;

	public static ResourceImageReference APPLICATION_ICON = new ResourceImageReference("/Application.png");

	private final DashboardActionPane m_parent;
	
	private ApplicationTableModel tableModel;

	public ApplicationManagerPane(final DashboardActionPane parent) {
		m_parent = parent;
		
		// Create the grid and add all components.		
		final Grid grid = new Grid(3);
		grid.setInsets(new Insets(2, 3));

		final GridLayoutData gridLayout = new GridLayoutData();
		gridLayout.setColumnSpan(3);
		Table deviceTable = createTable();
		deviceTable.setLayoutData(gridLayout);
		grid.add(deviceTable);

		add(grid);
	}

	private Table createTable() {

		tableModel = new ApplicationTableModel(0);
		
		Table aTable = new Table(tableModel);
		aTable.setBorder(new Border(1, Color.LIGHTGRAY, Border.STYLE_SOLID));
		aTable.setInsets(new Insets(3, 1));
		aTable.setDefaultRenderer(Object.class, new ApplicationTableCellRenderer());
		aTable.setDefaultHeaderRenderer(new ServiceHeaderTableCellRenderer());

		return aTable;
	}
	
	public void addApplication(final Application app) {
		
		// manage addition
		if (tableModel.containsApplication(app.getId())) {
			showErrorWindow("The service \"" + app.getName() + "\"already exists.");
			return;
		}
		
		// Add device to the table
		tableModel.addApplicationRow(app);
	}
	
	public class ApplicationTableModel extends DefaultTableModel {
		
		private final String[] columns = {
			"Name",
			"Version",
			"Vendor",
			"State",
			"Category"};
		
		private static final int SERV_NAME_COL_IDX = 0;
		private static final int SERV_VERSION_COL_IDX = 1;
		private static final int SERV_VENDOR_COL_IDX = 2;
		private static final int SERV_STATE_COL_IDX = 3;
		private static final int SERV_CATEGORY_COL_IDX = 4;
		
		private List<String> serviceIds = new ArrayList<String>();

		public ApplicationTableModel(int rows) {
			super(5, rows); // change first parameter if you add or remove columns
			for (int i = 0; i < columns.length; i++) {
				setColumnName(i, columns[i]);
			}
		}
		
		public boolean containsApplication(String id) {
			return serviceIds.contains(id);
		}

		public String getApplicationId(int row) {
			return serviceIds.get(row);
		}

		public void updateDeviceRow(Application service) {
			int rowIdx = serviceIds.indexOf(service.getId());
			if (rowIdx <0)
				return;
			
			setValueAt(service.getName(), SERV_NAME_COL_IDX, rowIdx);
			setValueAt(service.getVersion(), SERV_VERSION_COL_IDX, rowIdx);
			setValueAt(service.getState(), SERV_STATE_COL_IDX, rowIdx);
			setValueAt(service.getCategory().getName(), SERV_CATEGORY_COL_IDX, rowIdx);
			setValueAt(service.getVendor(), SERV_VENDOR_COL_IDX, rowIdx);
		}

		@Override
		public Object getValueAt(int column, int row) {
			return super.getValueAt(column, row);
		}
		
		public synchronized void addApplicationRow(Application service) {
			Object data[] = new Object[columns.length];
			data[SERV_NAME_COL_IDX] = service.getName();
			data[SERV_VERSION_COL_IDX] = service.getVersion();
			data[SERV_STATE_COL_IDX] = service.getState();
			data[SERV_VENDOR_COL_IDX] = service.getVendor();
			data[SERV_CATEGORY_COL_IDX] = service.getCategory().getName();
			serviceIds.add(service.getId());
			tableModel.addRow(data);
		}
		
		public synchronized void removeApplicationRow(String serialNumber) {
			int rowIdx = serviceIds.indexOf(serialNumber);
			if (rowIdx >=0) {
				serviceIds.remove(rowIdx);
				tableModel.deleteRow(rowIdx);
			}
		}
	}
	
	class ApplicationTableCellRenderer implements TableCellRenderer {

		/**
	    * 
	    */
		private static final long serialVersionUID = -2146113024393976876L;

		@Override
		public Component getTableCellRendererComponent(Table table, final Object value, int column, int row) {
			if (value == null) {
				return null;
			}
			final ApplicationTableModel serviceTableModel = (ApplicationTableModel) table.getModel();
			String serviceId = serviceTableModel.getApplicationId(row);
			if (column == ApplicationTableModel.SERV_STATE_COL_IDX) {
				return createStateList(serviceId, (ApplicationState) value);
			}
			
			return new Label(value.toString());
		}

	}
	
	private SelectField createStateList(String serviceId,
			ApplicationState servState) {
		final SelectField stateField = new SelectField();
		
		DefaultListModel model = new DefaultListModel();
		String[] states =  { ApplicationState.STARTED.toString(), ApplicationState.STOPED.toString(), ApplicationState.PAUSED.toString() } ;
		int servStateIdx = -1;
		for (int idx = 0; idx < states.length; idx++) {
			String state = states[idx];
			model.add(state);
			if ((servState != null) && (servState.toString().equals(state)))
				servStateIdx  = idx;
		}
		stateField.setModel(model);
		if (servStateIdx == -1)
			servStateIdx = 1; // by default stoped
		
		stateField.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		if (servState != null)
			stateField.getSelectionModel().setSelectedIndex(servStateIdx, true);

		StringListActionListener locationMenuActionListener = new StringListActionListener(
				serviceId, stateField) {
			protected void performSet(String newState) {
				//TODO change it when real implementation will be available
				Application service = (Application) getAppInstance().getApplication(serviceId);
				if (service != null) {
					//service.setState(ApplicationState.fromString(newState));
					//TODO
				}
			}
		};

		stateField.addActionListener(locationMenuActionListener);

		return stateField;
	}
	
	class ServiceHeaderTableCellRenderer implements TableCellRenderer {

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

		protected String serviceId;

		private SelectField field;

		public StringListActionListener(String serviceId,
				SelectField field) {
			super();
			this.serviceId = serviceId;
			this.field = field;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedIdx = field.getSelectionModel().getMinSelectedIndex();
			String state = (String) field.getModel().get(selectedIdx);
			performSet(state);

		}

		protected abstract void performSet(String newValueId);

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
	 * Gets the current application instance
	 * 
	 * @return
	 */
	private WebDashboardApplicationImpl getAppInstance() {
		return (WebDashboardApplicationImpl) m_parent.getApplicationInstance();
	}
	
	public void removeApplication(Application service) {
		
		String serviceId = service.getId();
		// manage addition
		if (!tableModel.containsApplication(serviceId)) {
			showErrorWindow("The service \"" + service.getName() + "\"does not exists.");
			return;
		}
				
		// Add device to the table
		tableModel.removeApplicationRow(serviceId);
	}
}
