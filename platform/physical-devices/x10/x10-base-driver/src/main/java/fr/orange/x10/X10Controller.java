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
package fr.orange.x10;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;

@Component(name = "X10.controller")
public class X10Controller {
	
	private BundleContext _context;
	
	@Property(mandatory=true)
	private String port;
	
	@Property(mandatory=true)
	private String module;

	private ControllerWrap _controllerWrap;
	
	public X10Controller(BundleContext context) {
		_context = context;
	}

	@Validate
	public void start() {
		String moduleStr = module.toLowerCase();

		try {
			_controllerWrap = new ControllerWrap("org.orange.x10." + port.toLowerCase(), port, moduleStr, _context);
			_controllerWrap.register();
		} catch (ConfigurationException e) {
			_controllerWrap = null;
			e.printStackTrace();
		}
	}
	
	@Invalidate
	public void stop() {
		if (_controllerWrap != null) {
			_controllerWrap.stop();
			_controllerWrap = null;
		}
	}
}

class ControllerWrap {

	String pid;
	String port;
	String module;
	Controller controller;

	ServiceRegistration reg;
	private BundleContext _context;

	ControllerWrap(String pid, String port, String module, BundleContext context)
			throws ConfigurationException {
		
		_context = context;
		
		if (port == null || "".equals(port)) {
			throw new ConfigurationException("port", "port must be specified");
		}

		if (module == null || "".equals(module)) {
			throw new ConfigurationException("module",
					"module must be specified");
		}

		this.pid = pid;
		this.port = port;
		this.module = module;

		try {
			if ("cm11a".equals(module)) {
				controller = new CM11ASerialController(port);
			} else if ("firecracker".equals(module) || "cm17a".equals(module)) {
				controller = new CM17ASerialController(port);
			} else {
				throw new ConfigurationException("module",
						"Unsupported module (valid modules are cm11a, cm17a and firecracker");
			}
		} catch (IOException e) {
			throw new ConfigurationException("port", "IO failure on port="
					+ port + ", " + e);
		}
		// Activator.log.info("created " + this);
	}

	void register() {
		if (reg != null) {
			return;
		}
		Hashtable props = new Hashtable();
		props.put("port", port);
		props.put("module", module);

		reg = _context.registerService(
				new String[] { Controller.class.getName(),
						controller.getClass().getName() }, controller, props);

		// Activator.log.info("registered " + this);
	}

	void unregister() {
		if (reg != null) {
			reg.unregister();
			reg = null;
			// Activator.log.info("unregistered " + this);
		}
	}

	void stop() {
		unregister();
		if (controller instanceof CM11ASerialController) {
			CM11ASerialController c = (CM11ASerialController) controller;
			try {
				c.shutdown(10 * 1000);
			} catch (Exception e) {
				// Activator.log.error("Clean shutdown of CM11A failed", e);
				try {
					c.shutdownNow();
				} catch (Exception e2) {
					// Activator.log.error("Forced shutdown of CM11A failed",
					// e2);
				}
			}
		} else if (controller instanceof CM17ASerialController) {
			CM17ASerialController c = (CM17ASerialController) controller;
			try {
				c.shutdown(10 * 1000);
			} catch (Exception e) {
				// Activator.log.error("Clean shutdown of CM17A failed", e);
				try {
					c.shutdownNow();
				} catch (Exception e2) {
					// Activator.log.error("Forced shutdown of CM17A failed",
					// e2);
				}
			}
		}
		controller = null;
		// Activator.log.info("stopped " + this);
	}

	public String toString() {
		return "ControllerWrap[" + "pid=" + pid + ", port=" + port
				+ ", module=" + module + ", controller=" + controller + "]";
	}
}
