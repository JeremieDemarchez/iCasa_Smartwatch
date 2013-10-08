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

import org.teleal.cling.binding.annotations.*;

import java.beans.PropertyChangeSupport;

@UpnpService(
		serviceId = @UpnpServiceId("Gyroscope"),
		serviceType = @UpnpServiceType(value = "Gyroscope", version = 1)
		)
public class Gyroscope {

	private final PropertyChangeSupport propertyChangeSupport;

	public Gyroscope() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@UpnpStateVariable(defaultValue = "0")
	private float xGyroValue = 0;
	
	@UpnpStateVariable(defaultValue = "0")
	private float yGyroValue = 0;

	@UpnpStateVariable(defaultValue = "0")
	private float zGyroValue = 0;
	
	@UpnpAction(out = @UpnpOutputArgument(name = "RetXGyroValue"))
	public float getXGyroValue() {
		xGyroValue = DemoActivity.xGyroscope;
		return xGyroValue;
	}
	
	@UpnpAction(out = @UpnpOutputArgument(name = "RetYGyroValue"))
	public float getYGyroValue() {
		yGyroValue = DemoActivity.yGyroscope;
		return yGyroValue;
	}
	
	@UpnpAction(out = @UpnpOutputArgument(name = "RetZGyroValue"))
	public float getZGyroValue() {
		zGyroValue = DemoActivity.zGyroscope;
		return zGyroValue;
	}
}
