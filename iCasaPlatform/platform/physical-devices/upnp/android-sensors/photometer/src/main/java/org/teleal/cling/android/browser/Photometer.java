/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package main.java.org.teleal.cling.android.browser;

import java.beans.PropertyChangeSupport;

import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;
import org.teleal.cling.binding.annotations.UpnpStateVariable;

@UpnpService(
		serviceId = @UpnpServiceId("Photometer"),
		serviceType = @UpnpServiceType(value = "Photometer", version = 1)
		)
public class Photometer {

	@UpnpStateVariable(
			defaultValue = "0",
			name = "Illuminance"
			)
	private double illuminance = 0;

	private final PropertyChangeSupport propertyChangeSupport;

	public Photometer() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

//	@UpnpAction
//	public void setIlluminance(@UpnpInputArgument(name = "NewIlluminanceValue") double newIlluminanceValue) {
//
//		double oldIllumianceValue=illuminance;
//
//		illuminance = newIlluminanceValue;
//
//
//		//Notify that the data change and push data 
//		if( oldIllumianceValue != newIlluminanceValue)
//		{
//			//TO DO
//			getPropertyChangeSupport().firePropertyChange("illuminance", oldIllumianceValue, newIlluminanceValue);
//
//			//This will send an upnp event on property change
//			getPropertyChangeSupport().firePropertyChange("Illuminance", oldIllumianceValue, newIlluminanceValue);
//		}
//	}

		@UpnpAction(out = @UpnpOutputArgument(name = "RetIlluminanceValue", stateVariable = "Illuminance"))
		public double getIlluminance() {

			illuminance = DemoActivity.l;
			return illuminance;
		}
	}
