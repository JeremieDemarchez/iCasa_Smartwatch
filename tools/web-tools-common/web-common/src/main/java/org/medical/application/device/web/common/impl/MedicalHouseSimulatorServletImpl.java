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
package org.medical.application.device.web.common.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nextapp.echo.webcontainer.WebContainerServlet;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.InstanceStateListener;
import org.apache.felix.ipojo.annotations.Invalidate;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
public abstract class MedicalHouseSimulatorServletImpl extends WebContainerServlet implements InstanceStateListener {

	/**
    * 
    */
	private static final long serialVersionUID = 956410092075324420L;

	private String houseImage;

	private String userImage;

	private String homeType;

	private Factory m_appFactory;

	private final Map<String, ComponentInstance> m_appInstances = new HashMap<String, ComponentInstance>();


	/**
	 * Create a new session component instance
	 */

	@Override
	public MedicalHouseSimulatorImpl newApplicationInstance() {
		
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
		final MedicalHouseSimulatorImpl pojo = (MedicalHouseSimulatorImpl) ((InstanceManager) appInstance)
		      .getPojoObject();
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
		Thread.currentThread().setContextClassLoader(getBundleClassLoader());
		try {
			super.service(req, res);
		} finally {
			// Restore classloader
			Thread.currentThread().setContextClassLoader(tccl);
		}
	}

	public abstract ClassLoader getBundleClassLoader();


	public void setHouseImage(String houseImage) {
		this.houseImage = houseImage;
	}

	public void setHomeType(String homeType) {
		this.homeType = homeType;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public void setApplicationInstanceFactory(Factory factory) {
		m_appFactory = factory;
	}
	
}
