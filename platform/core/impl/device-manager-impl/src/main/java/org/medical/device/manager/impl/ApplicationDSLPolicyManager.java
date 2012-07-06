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
package org.medical.device.manager.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.medical.application.Application;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.KnownDevice;
import org.osgi.framework.ServiceReference;

/**
 * Default implementation of Policy manager which interprets policies defined in a domain specific language.  
 * 
 * @author Thomas Leveque
 *
 */
@Component(name="application-dsl-policy-mgr")
@Instantiate(name="application-dsl-policy-mgr-1")
@Provides(specifications=PolicyManager.class)
public class ApplicationDSLPolicyManager implements PolicyManager {

	@Override
	public ApplicationDevice createApplicationDevice(KnownDeviceImpl knownDev,
			Application app) {
		
		ApplicationDeviceImpl appDev = new ApplicationDeviceImpl(knownDev, app);
		
		return appDev;
	}

	public boolean creationIsAllowed(KnownDevice dev, Application app) {
		// TODO implement it
		
		return true;
	}

	@Override
	public boolean canGiveUnprotectedDevTo(Application app) {
		//TODO implement it
		if (app.getId().equals("icasa.simulator"))
			return true;
		
		return false;
	}

	@Override
	public boolean allowAccess(Application app, ServiceReference sr) {
		//TODO implement it
		if (app == null)
			return true;
		
		if (app.getId().equals("icasa.simulator"))
			return true;
		
		return false;
	}

	
}
