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
package org.medical.simulator.portlet.impl;

import nextapp.echo.app.Color;
import nextapp.echo.app.Component;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;

import org.medical.application.housesimulator.impl.MedicalHouseSimulatorImpl;
import org.medical.application.housesimulator.portlet.DeviceStatusWindow;
import org.osgi.framework.ServiceReference;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;

public class PresenceSensorStatusWindow extends DeviceStatusWindow {

	/**
    * 
    */
	private static final long serialVersionUID = 5283844186926837953L;

	public PresenceSensorStatusWindow(MedicalHouseSimulatorImpl parent, String deviceSerialNumber) {
		super(parent, deviceSerialNumber);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void updateInfo(final ServiceReference reference, final GenericDevice device) {

		for (Component c : getComponents()) {
			remove(c);
		}

		String title = (String) reference.getProperty("service.description");
		if (title == null)
			title = "Device: " + m_deviceSerialNumber;

		setTitle(title);
		Grid layoutGrid = new Grid();
		layoutGrid.setInsets(new Insets(3, 1));

		layoutGrid.add(new Label("Serial Number: "));
		layoutGrid.add(new Label(m_deviceSerialNumber));

		if (device instanceof PresenceSensor) {
			PresenceSensor presenceDevice = (PresenceSensor) device;
			layoutGrid.add(new Label("Location"));
			Label locationLabel = new Label(presenceDevice.getLocation());

			locationLabel.setForeground(Color.BLUE);
			layoutGrid.add(locationLabel);

			boolean presenceSensed = presenceDevice.getSensedPresence();
			layoutGrid.add(new Label("Presence Sensed"));
			Label presenceLabel;
			if (presenceSensed) {
				presenceLabel = new Label("Somebody in the room");
				presenceLabel.setForeground(Color.RED);
			} else {
				presenceLabel = new Label("Nobody in the room");
				presenceLabel.setForeground(Color.BLACK);
			}
			layoutGrid.add(presenceLabel);
		}

		layoutGrid.add(new Label("Fault"));

		String fault = (String) reference.getProperty("fault");
		Label faultLabel;
		if (fault!=null) {
			if (fault.equals("yes")) {
				faultLabel = new Label("YES");
				faultLabel.setForeground(Color.RED);
			} else {
				faultLabel = new Label("NO");
				faultLabel.setForeground(Color.GREEN);
			}
		} else {
			faultLabel = new Label("unknown");
			faultLabel.setForeground(Color.DARKGRAY);
		}


		layoutGrid.add(faultLabel);

		layoutGrid.add(new Label("State"));

		String stateStr = (String) reference.getProperty("state");
		
		Label stateLabel;
		if (stateStr!=null) {
			if (stateStr.equals("activated")) {
				stateLabel = new Label(stateStr);
				stateLabel.setForeground(Color.GREEN);
			} else {
				stateLabel = new Label(stateStr);
				stateLabel.setForeground(Color.RED);
			}
		} else {
			stateLabel = new Label("unknown");
			stateLabel.setForeground(Color.DARKGRAY);
		}
		
		layoutGrid.add(stateLabel);
		add(layoutGrid);

	}

}
