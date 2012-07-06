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
package org.medical.upnp.presence.detector;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;


@Component(name = "upnp.simulated.presence.detector", immediate=true)
public class PresenceDetectorDevice extends AbstractPresenceDetectorDevice {

	final private String DEVICE_ID = "uuid:Medical-PD-" +Integer.toHexString(503257);
	
	public PresenceDetectorDevice(BundleContext context) {
	   super(context);
   }
	
	protected boolean isSimulated() {
		return true;
	}

	@Override
	protected String getDeviceId() {
		return DEVICE_ID;
	}
	
	protected boolean mustBeNetworkExported() {
		return true;
	}

	@Override
	protected String getModelDescription() {
		return "Presence Detector";
	}

	@Override
	protected String getUPC() {
		return "123456789";
	}

	@Override
	protected String getFriendlyName() {
		return "Medical Fake Presence Detector";
	}

	@Override
	protected String getManufacturer() {
		return "Medical Project Consortium";
	}

	@Override
	protected String getModelName() {
		return "TotoPresence";
	}

	@Override
	protected String getSerialNumber() {
		return getUPC();
	}

	@Override
	protected String getModelURL() {
		return "http://felix.apache.org/site/upnp-examples.html";
	}

	@Override
	protected String getModelNumber() {
		return "1.0";
	}
	
	@Override
	protected String getManufacterURL() {
		return "http://medical.org";
	}
	
	@Validate
	protected void start() {
		super.start();
	}
	
	@Invalidate
	protected void stop() {
		super.stop();
	}
}
