/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research Group
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
package fr.liglab.adele.m2mappbuilder.device.manager.test.app1;

import java.util.Hashtable;

import fr.liglab.adele.m2mappbuilder.application.ApplicationProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class App1Activator implements BundleActivator {
	
	private App1 _app;
	
	private BundleContext _context;

	private ServiceRegistration _registeredService;

	public static App1Activator INSTANCE;

	public void start(BundleContext context) throws Exception {
		INSTANCE = this;
		_context = context;
		_app = new App1(context);
		_registeredService = context.registerService(ApplicationProvider.class.getName(), _app, new Hashtable());
	}

	public void stop(BundleContext context) throws Exception {
		_registeredService.unregister();
		INSTANCE = null;
		_app = null;
		_context = null;
	}

}
