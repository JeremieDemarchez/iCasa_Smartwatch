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

import java.util.Map;

import nextapp.echo.app.Extent;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.extras.app.AccordionPane;
import nextapp.echo.extras.app.event.TabSelectionEvent;
import nextapp.echo.extras.app.event.TabSelectionListener;
import nextapp.echo.extras.app.layout.AccordionPaneLayoutData;

import org.apache.felix.ipojo.Factory;
import org.medical.application.Application;
import org.medical.application.device.dashboards.impl.MedicalHouseSimulatorImpl;
import org.medical.clock.api.Clock;
import org.medical.device.manager.ApplicationDevice;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;

/**
 * This Pane contains actions that can be realized by user 
 * 
 * @author bourretp
 */
public class ActionPane extends AccordionPane implements SelectedApplicationTracker {

	/**
    * 
    */
	private static final long serialVersionUID = 4290465922034559972L;

	public static Extent ICON_SIZE = new Extent(25);

	private final MedicalHouseSimulatorImpl m_appInstance;

	private final DevicePane m_devicePane;

	private final ApplicationManagerPane m_appPane;
	
	private UserPane m_userPane;
	
	private ClockPane m_clockPane;
	

	public ActionPane(final MedicalHouseSimulatorImpl appInstance) {
		m_appInstance = appInstance;

		
		if (!appInstance.isSimulator()) {
			m_devicePane = new DevicePane(this);
			final AccordionPaneLayoutData devicePaneLayout = new AccordionPaneLayoutData();
			devicePaneLayout.setIcon(new ResourceImageReference(
					DevicePane.DEVICE_IMAGE.getResource(), ICON_SIZE, ICON_SIZE));
			devicePaneLayout.setTitle(getDeviceTabName(SelectAppPane.UNDEFINED_SERV_NAME));
			m_devicePane.setLayoutData(devicePaneLayout);
			add(m_devicePane);
		} else {
			m_devicePane = new SimulatedDevicePane(this);
			final AccordionPaneLayoutData devicePaneLayout = new AccordionPaneLayoutData();
			devicePaneLayout.setIcon(new ResourceImageReference(
					DevicePane.DEVICE_IMAGE.getResource(), ICON_SIZE, ICON_SIZE));
			devicePaneLayout.setTitle(getDeviceTabName(SelectAppPane.UNDEFINED_SERV_NAME));
			m_devicePane.setLayoutData(devicePaneLayout);
			add(m_devicePane);
			
			// Create the user controller pane.
			m_userPane = new UserPane(this);
			final AccordionPaneLayoutData userPaneLayout = new AccordionPaneLayoutData();
			userPaneLayout.setIcon(new ResourceImageReference(UserPane.USER_IMAGE
					.getResource(), ICON_SIZE, ICON_SIZE));
			userPaneLayout.setTitle("Simulated Users");
			m_userPane.setLayoutData(userPaneLayout);
			add(m_userPane);
			
			
			
			// Create the clock controller pane.
			m_clockPane = new ClockPane(this);
			final AccordionPaneLayoutData clockPaneLayout = new AccordionPaneLayoutData();
			clockPaneLayout.setIcon(new ResourceImageReference(
					ClockPane.CLOCK_IMAGE.getResource(), ICON_SIZE, ICON_SIZE));
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
		}

		// Create the devices status controller pane
		if (appInstance.isAndroid()) {
			final AccordionPaneLayoutData statusPaneLayout = new AccordionPaneLayoutData();
			statusPaneLayout
					.setIcon(new ResourceImageReference(DevicePane.DEVICE_IMAGE
							.getResource(), ICON_SIZE, ICON_SIZE));
			statusPaneLayout.setTitle("Device Details");
			appInstance.getStatusPane().setLayoutData(statusPaneLayout);
			add(appInstance.getStatusPane());
		}
		
		// Create the application manager pane.
		m_appPane = new ApplicationManagerPane(this);
		final AccordionPaneLayoutData appPaneLayout = new AccordionPaneLayoutData();
		appPaneLayout.setIcon(new ResourceImageReference(
				ApplicationManagerPane.APPLICATION_ICON.getResource(),
				ICON_SIZE, ICON_SIZE));
		appPaneLayout.setTitle("Applications");
		m_appPane.setLayoutData(appPaneLayout);
		add(m_appPane);
		

		
		setActiveTabIndex(0);
	}
	
	private void updateDeviceTabTitle() {
		AccordionPaneLayoutData devicePaneLayout = (AccordionPaneLayoutData) m_devicePane.getLayoutData();
		final Application selectedService = getApplicationInstance().getSelectedApplication();
		String titlePostfix;
		if (selectedService == null) {
			titlePostfix = SelectAppPane.UNDEFINED_SERV_NAME;
		} else {
			titlePostfix = selectedService.getName();
		}
		devicePaneLayout.setTitle(getDeviceTabName(titlePostfix));
		m_devicePane.setLayoutData(devicePaneLayout);
	}

	private String getDeviceTabName(String serviceName) {
		return "Devices for " + serviceName;
	}

	public MedicalHouseSimulatorImpl getApplicationInstance() {
		return m_appInstance;
	}

	public void setClock(final Clock clock) {
		if (m_clockPane!=null)
			m_clockPane.setClock(clock);
	}

	public void addDevice(ApplicationDevice device) {
		m_devicePane.addDevice(device);
	}

	public void removeDevice(ApplicationDevice device) {
		m_devicePane.removeDevice(device);
	}

	public void addDeviceFactory(Factory factory) {
		m_devicePane.addDeviceFactory(factory);
	}

	public void removeDeviceFactory(Factory factory) {
		m_devicePane.removeDeviceFactory(factory);
	}

	public void moveDevice(String deviceSerialNumber, Position position) {
		m_devicePane.moveDevice(deviceSerialNumber, position);
	}

	public void moveUser(String userName, Position position) {
		if (m_userPane!=null)
			m_userPane.moveUser(userName, position);
	}

	public void changeDevice(String deviceSerialNumber,
			final ApplicationDevice device) {
		m_devicePane.changeDevice(deviceSerialNumber, device);
	}

	public void updateScriptList() {

	}
	
	public void addApplication(Application service) {
		m_appPane.addApplication(service);
	}
	
	public void removeApplication(Application service) {
		m_appPane.removeApplication(service);
	}

	@Override
	public void notifySelectedAppChanged(Application oldSelectServ,
			Application newSelectedServ) {
		updateDeviceTabTitle();
		m_devicePane.notifySelectedAppChanged(oldSelectServ, newSelectedServ);
	}

	public void refreshDeviceWidgets() {
		m_devicePane.refreshDeviceWidgets();
	}

}
