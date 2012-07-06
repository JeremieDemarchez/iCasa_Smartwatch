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
package org.medical.application.device.dashboards.portlet;

import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.WindowPane;

import org.medical.application.device.dashboards.impl.MedicalHouseSimulatorImpl;

import fr.liglab.adele.icasa.device.GenericDevice;

public interface DeviceWidgetFactory {

	public WindowPane createWidget(MedicalHouseSimulatorImpl parent, String deviceId);
	
	public boolean typeIsSupported(GenericDevice device);
	
	public ResourceImageReference getDeviceIcon(GenericDevice device);
	
	public String getID();
	
}
