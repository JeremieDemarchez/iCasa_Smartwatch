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
package fr.liglab.adele.icasa.application.device.web.common.widget.impl;

import nextapp.echo.app.Color;
import nextapp.echo.app.Component;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;

import fr.liglab.adele.icasa.application.device.web.common.impl.BaseHouseApplication;
import fr.liglab.adele.icasa.application.device.web.common.widget.DeviceStatusWindow;
import org.osgi.framework.ServiceReference;

import fr.liglab.adele.icasa.device.GenericDevice;

public class GenericDeviceStatusWindow extends DeviceStatusWindow {

	/**
    * 
    */
	private static final long serialVersionUID = 2361568252033026276L;

	public GenericDeviceStatusWindow(BaseHouseApplication parent, String deviceSerialNumber) {
		super(parent, deviceSerialNumber);
	}

	@Override
	protected void updateInfo(final ServiceReference reference, final GenericDevice device) {

		for (Component c : getComponents()) {
			remove(c);
		}

		String title = (String) reference.getProperty("service.description");
		if (title == null)
			title = "LocatedDevice: " + m_deviceSerialNumber;

		setTitle(title);
		Grid layoutGrid = new Grid();
		layoutGrid.setInsets(new Insets(3, 1));

		layoutGrid.add(new Label("Serial Number: "));
		layoutGrid.add(new Label(m_deviceSerialNumber));

		add(layoutGrid);
		
		
		
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

	}

}
