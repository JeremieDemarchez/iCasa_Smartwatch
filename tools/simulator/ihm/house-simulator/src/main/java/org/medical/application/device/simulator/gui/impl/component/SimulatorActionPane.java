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

import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.extras.app.event.TabSelectionEvent;
import nextapp.echo.extras.app.event.TabSelectionListener;
import nextapp.echo.extras.app.layout.AccordionPaneLayoutData;

import org.apache.felix.ipojo.Factory;
import org.medical.application.device.simulator.gui.impl.HouseSimulatorGUIImpl;
import org.medical.application.device.web.common.impl.component.ActionPane;
import org.medical.application.device.web.common.impl.component.DevicePane;
import org.medical.clock.api.Clock;

import fr.liglab.adele.icasa.environment.SimulationManager.Position;

public class SimulatorActionPane extends ActionPane {

	/**
    * 
    */
	private static final long serialVersionUID = 3069372724489038894L;

	private HouseSimulatorGUIImpl appInstance;

	private UserPane m_userPane;

	private ClockPane m_clockPane;

	public SimulatorActionPane(HouseSimulatorGUIImpl appInstance) {
		super(appInstance);
		this.appInstance = appInstance;
		initContent();
	}

	protected void initContent() {
		m_devicePane = new SimulatorDevicePane(this);
		final AccordionPaneLayoutData devicePaneLayout = new AccordionPaneLayoutData();
		devicePaneLayout.setIcon(new ResourceImageReference(DevicePane.DEVICE_IMAGE.getResource(), ICON_SIZE, ICON_SIZE));
		// devicePaneLayout.setTitle(getDeviceTabName(SelectAppPane.UNDEFINED_SERV_NAME));
		devicePaneLayout.setTitle("Device List");
		m_devicePane.setLayoutData(devicePaneLayout);
		add(m_devicePane);

		// Create the user controller pane.
		m_userPane = new UserPane(this);
		final AccordionPaneLayoutData userPaneLayout = new AccordionPaneLayoutData();
		userPaneLayout.setIcon(new ResourceImageReference(UserPane.USER_IMAGE.getResource(), ICON_SIZE, ICON_SIZE));
		userPaneLayout.setTitle("Simulated Users");
		m_userPane.setLayoutData(userPaneLayout);
		add(m_userPane);

		// Create the clock controller pane.
		m_clockPane = new ClockPane(this);
		final AccordionPaneLayoutData clockPaneLayout = new AccordionPaneLayoutData();
		clockPaneLayout.setIcon(new ResourceImageReference(ClockPane.CLOCK_IMAGE.getResource(), ICON_SIZE, ICON_SIZE));
		clockPaneLayout.setTitle("Simulated Time & Date");
		m_clockPane.setLayoutData(clockPaneLayout);
		add(m_clockPane);
		addTabSelectionListener(new TabSelectionListener() {
			private static final long serialVersionUID = 6318394128515527348L;

			@Override
			public void tabSelected(final TabSelectionEvent e) {
				if (e.getTabIndex() == 0) {
					m_clockPane.startUpdaterTask();
				} else {
					m_clockPane.stopUpdaterTask();
				}
			}
		});

		// Create the devices status controller pane
		if (appInstance.isAndroid()) {
			final AccordionPaneLayoutData statusPaneLayout = new AccordionPaneLayoutData();
			statusPaneLayout.setIcon(new ResourceImageReference(DevicePane.DEVICE_IMAGE.getResource(), ICON_SIZE,
			      ICON_SIZE));
			statusPaneLayout.setTitle("Device Details");
			appInstance.getStatusPane().setLayoutData(statusPaneLayout);
			add(appInstance.getStatusPane());
		}

	}

	public void setClock(Clock clock) {
		if (m_clockPane != null)
			m_clockPane.setClock(clock);
	}

	public void moveUser(String userName, Position position) {
		if (m_userPane != null)
			m_userPane.moveUser(userName, position);
	}

	public HouseSimulatorGUIImpl getApplicationInstance() {
		return appInstance;
	}

	public void addDeviceFactory(Factory factory) {
		((SimulatorDevicePane) m_devicePane).addDeviceFactory(factory);
	}

	public void removeDeviceFactory(Factory factory) {
		((SimulatorDevicePane) m_devicePane).removeDeviceFactory(factory);
	}

}
