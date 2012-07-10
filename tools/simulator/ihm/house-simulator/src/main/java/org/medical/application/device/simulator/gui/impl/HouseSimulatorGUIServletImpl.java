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
package org.medical.application.device.simulator.gui.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nextapp.echo.app.serial.property.BooleanPeer;
import nextapp.echo.extras.app.serial.property.AccordionPaneLayoutDataPeer;
import nextapp.echo.extras.webcontainer.sync.component.GroupPeer;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.sync.command.BrowserOpenWindowCommandPeer;
import nextapp.echo.webcontainer.sync.component.RadioButtonPeer;
import nextapp.echo.webcontainer.sync.property.ServedImageReferencePeer;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.InstanceStateListener;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
@Component(name = "SimulatorGUIServlet")
// @Provides(specifications = Servlet.class, properties = {
// @StaticServiceProperty(name = "alias", type = "java.lang.String", value =
// "/dashboards") })
@Provides(specifications = Servlet.class)
public class HouseSimulatorGUIServletImpl extends WebContainerServlet implements InstanceStateListener {

	@ServiceProperty(name = "alias")
	private String alias;

	@Property(name = "houseImage", mandatory = true)
	private String houseImage;

	@Property(name = "userImage", mandatory = true)
	private String userImage;

	@Property(name = "homeType", mandatory = true)
	private String homeType;

	Class[] peerClasses = { RadioButtonPeer.class, GroupPeer.class, BooleanPeer.class, ServedImageReferencePeer.class,
	      BrowserOpenWindowCommandPeer.class, AccordionPaneLayoutDataPeer.class };

	/**
	 * The classloader of this bundle.
	 */
	private static final ClassLoader CLASSLOADER = HouseSimulatorGUIServletImpl.class.getClassLoader();

	@Requires(filter = "(factory.name=WebHouseSimulatorNew)")
	private Factory m_appFactory;

	private final Map<String, ComponentInstance> m_appInstances = new HashMap<String, ComponentInstance>();

	/**
	 * Create a new session component instance
	 */
	@Override
	public HouseSimulatorGUIImpl newApplicationInstance() {
		System.out.println(peerClasses);

		// Create the application instance.
		final ComponentInstance appInstance;
		Dictionary<String, Object> dict = new Hashtable<String, Object>();
		dict.put("houseImage", houseImage);
		dict.put("userImage", userImage);
		dict.put("homeType", homeType);

		String[] isAndroidParams = (String[]) getActiveConnection().getUserInstance().getInitialRequestParameterMap()
		      .get("isAndroid");
		boolean isAndroid = false;
		if ((isAndroidParams != null) && (isAndroidParams.length > 0)) {
			String isAndroidStr = isAndroidParams[0];
			isAndroid = ((isAndroidStr != null) && (Boolean.valueOf(isAndroidStr)));
		}
		dict.put("isAndroid", Boolean.toString(isAndroid));

		// TODO: Provisional solution to share the same code in both Servlets
		// (dashboard and simulator)

		//if (alias.equals("/simulator"))
			dict.put("isSimulator", new Boolean(true));
		//else
		//	dict.put("isSimulator", new Boolean(false));

		try {
			appInstance = m_appFactory.createComponentInstance(dict);
		} catch (final Exception e) {
			throw new RuntimeException("Cannot create application instance", e);
		}
		final String name = appInstance.getInstanceName();
		synchronized (m_appInstances) {
			m_appInstances.put(name, appInstance);
			appInstance.addInstanceStateListener(this);
		}
		final HouseSimulatorGUIImpl pojo = (HouseSimulatorGUIImpl) ((InstanceManager) appInstance).getPojoObject();
		pojo.setComponentInstance(appInstance);
		// m_logger.info("Application instance created : " + name);
		return pojo;
	}

	@Override
	public void stateChanged(final ComponentInstance appInstance, final int newState) {
		if (newState != ComponentInstance.DISPOSED) {
			return;
		}
		final String name = appInstance.getInstanceName();
		synchronized (m_appInstances) {
			m_appInstances.remove(name);
		}
		// m_logger.info("Application instance disposed : " + name);
	}

	@Invalidate
	public void cleanUp() {
		// m_logger.info("Destroying all applications instances");
		final Collection<ComponentInstance> appInstances;
		synchronized (m_appInstances) {
			// Copy to avoid ConcurrentModificationExceptions.
			appInstances = new ArrayList<ComponentInstance>(m_appInstances.values());
		}
		for (ComponentInstance appInstance : appInstances) {
			appInstance.dispose();
		}
	}

	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Echo3 uses the thread current class loader, so we need to feed it,
		// with the appropriated class loader, before any request.
		final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(CLASSLOADER);
		try {
			super.service(req, res);
		} finally {
			// Restore classloader
			Thread.currentThread().setContextClassLoader(tccl);
		}
	}

}
