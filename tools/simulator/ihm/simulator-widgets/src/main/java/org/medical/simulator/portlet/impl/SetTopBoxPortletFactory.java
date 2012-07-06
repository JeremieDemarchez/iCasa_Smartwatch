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
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.WindowPane;

import org.medical.application.housesimulator.impl.MedicalHouseSimulatorImpl;
import org.medical.application.housesimulator.portlet.PortletFactory;

import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.settopbox.SetTopBox;


public class SetTopBoxPortletFactory implements PortletFactory {

	@Override
	public WindowPane createPortlet(MedicalHouseSimulatorImpl parent, String deviceId) {
		return new SetTopBoxStatusWindow(parent, deviceId);
	}

	@Override
	public boolean typeIsSupported(GenericDevice device) {
		if (device instanceof SetTopBox) 
			return true;
		
	   return false;
	}

	@Override
   public ResourceImageReference getImageForDevice(GenericDevice device) {
		if (device instanceof GenericDevice) {
			GenericDevice medicalGenericDevice = (GenericDevice) device;
			if (medicalGenericDevice.getState().equals("deactivated")) {
				return new ResourceImageReference("/SetTopBox-Deactivated.png");
			} else if (medicalGenericDevice.getState().equals("activated")) {
				if (medicalGenericDevice.getFault().equals("yes")) {
					return new ResourceImageReference("/SetTopBox-Fault.png");
				} else if (((SetTopBox) medicalGenericDevice).isStarted()) {
					return new ResourceImageReference("/SetTopBox-Activated-Event.png");
				} else {
					return new ResourceImageReference("/SetTopBox-Activated.png");
				}
			}
		}
	   return null;
   }

}
