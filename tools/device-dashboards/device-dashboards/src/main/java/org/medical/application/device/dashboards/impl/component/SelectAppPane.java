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

import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.SelectField;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.GridLayoutData;
import nextapp.echo.app.list.DefaultListModel;
import nextapp.echo.app.list.ListSelectionModel;

import org.medical.application.Application;
import org.medical.application.device.web.common.impl.MedicalHouseSimulatorImpl;
import org.medical.application.device.web.common.impl.component.SelectedApplicationTracker;

/**
 * Panels which allows user to select a digital service.
 * 
 * @author Thomas Leveque
 *
 */
public class SelectAppPane extends ContentPane implements ActionListener {

	public static final String UNDEFINED_SERV_NAME = "Home (All)";

	private final MedicalHouseSimulatorImpl m_appInstance;
	
	private Application _selectedServ;
	
	private List<Application> _apps = new ArrayList<Application>();

	private List<SelectedApplicationTracker> _trackers = new ArrayList<SelectedApplicationTracker>();

	private DefaultListModel _selectFieldModel;

	private ActionListener _servListActionListener;

	private SelectField _servListField;
	
	public SelectAppPane(final MedicalHouseSimulatorImpl appInstance) {
		m_appInstance = appInstance;
		
		// create widgets
		Label selecServLabel = new Label("Devices for: ");
		final GridLayoutData labelLayout = new GridLayoutData();
		labelLayout.setColumnSpan(1);
		selecServLabel.setLayoutData(labelLayout);
		
		createServListField();
		final GridLayoutData servListLayout = new GridLayoutData();
		servListLayout.setColumnSpan(GridLayoutData.SPAN_FILL);
		_servListField.setLayoutData(servListLayout);
		
		// Create the grid and add all components.		
		final Grid grid = new Grid(2);
		grid.setInsets(new Insets(2, 3));
		grid.add(selecServLabel);
		grid.add(_servListField);
		
		add(grid);
	}
	
	private void createServListField() {
		_servListField = new SelectField();
		
		updateServList();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedIdx = _servListField.getSelectionModel().getMinSelectedIndex();
		Application selectedServ = null;
		if (selectedIdx > 0)
			selectedServ = _apps.get(selectedIdx - 1);
		setSelectedApplication(selectedServ);
	}
	
	private synchronized void updateServList() {
		
		_servListField.removeActionListener(this);
		
		_selectFieldModel = new DefaultListModel();
		_selectFieldModel.add(UNDEFINED_SERV_NAME);
		int selectedServIdx = 0; // by default, no selected service
		for (int idx = 0; idx <  _apps.size(); idx++) {
			Application service = _apps.get(idx);
			_selectFieldModel.add(service.getName());
			if ((_selectedServ != null) && (_selectedServ.getId().equals(service.getId())))
				selectedServIdx  = idx;
		}
		_servListField.setModel(_selectFieldModel);
		
		_servListField.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_servListField.getSelectionModel().setSelectedIndex(selectedServIdx, true);

		_servListField.addActionListener(this);
	}
	
	public void addSelectedApplicationTracker(SelectedApplicationTracker tracker) {
		synchronized (_trackers) {
			if (_trackers.contains(tracker))
				return;
			
			_trackers.add(tracker);
			
			try {
				tracker.notifySelectedAppChanged(null, _selectedServ);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeSelectedApplicationTracker(SelectedApplicationTracker tracker) {
		synchronized (_trackers) {
			_trackers.remove(tracker);
		}
	}
	
	public synchronized void setSelectedApplication(Application selectedServ) {
		Application oldSelectServ = _selectedServ;
		_selectedServ = selectedServ;
		if (!sameApplication(oldSelectServ, _selectedServ)) {
			synchronized (_trackers) {
				for (SelectedApplicationTracker tracker : _trackers)
					try {
						tracker.notifySelectedAppChanged(oldSelectServ, _selectedServ);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
	}

	private boolean sameApplication(Application serv1,
			Application serv2) {
		if (serv1 == null)
			return (serv2 == null);
		
		if (serv2 == null)
			return false;
		
		return (serv1.getId().equals(serv2.getId()));
	}

	public void addApplication(Application service) {
		synchronized(_apps) {
			Application foundServ = getKnownApplication(service);
			
			if (foundServ != null)
				return;
			
			_apps.add(service);
			updateServList();
		}
	}

	/**
	 * Return known service with same id than specified service.
	 * 
	 * @param service
	 * @return known service with same id than specified service.
	 */
	private Application getKnownApplication(Application service) {
		for (Application curServ : _apps) {
			if (sameApplication(curServ, service)) 
				return curServ;
		}
		
		return null;
	}

	public void removeApplication(Application service) {
		synchronized(_apps) {
			Application foundServ = getKnownApplication(service);
			
			if (foundServ == null)
				return;
			
			if (sameApplication(service, _selectedServ))
				setSelectedApplication(null);
			_apps.remove(foundServ);
			updateServList();
		}
	}
	
	public MedicalHouseSimulatorImpl getApplicationInstance() {
		return m_appInstance;
	}

	public final Application getSelectedApplication() {
		return _selectedServ;
	}
}
