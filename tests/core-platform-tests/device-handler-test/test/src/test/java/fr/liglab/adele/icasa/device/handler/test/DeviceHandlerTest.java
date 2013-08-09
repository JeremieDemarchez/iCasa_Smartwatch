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
package fr.liglab.adele.icasa.device.handler.test;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.handlers.dependency.DependencyDescription;
import org.apache.felix.ipojo.handlers.dependency.DependencyHandlerDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.access.DeviceAccessPolicy;
import fr.liglab.adele.icasa.application.ApplicationManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.handler.test.mock.devices.BinaryLightMockImpl;
import fr.liglab.adele.icasa.device.light.BinaryLight;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class DeviceHandlerTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;

	private DeploymentAdmin dpAdmin;

	private ApplicationManager manager;

	@Before
	public void setUp() throws Exception {
		waitForStability(context);
		dpAdmin = (DeploymentAdmin) getService(context, DeploymentAdmin.class);
		manager = (ApplicationManager) getService(context, ApplicationManager.class);
		installDeploymentPackage("test-handler-app-dp-1");
	}

	@After
	public void tearDown() {

	}

	/**
	 * Test the creation of a new zone.
	 * 
	 * @throws IOException
	 * @throws
	 */
	@Test
	public void testSimpleHandler() throws Exception {
		ContextManager ctxManager = (ContextManager) getService(context, ContextManager.class);
		assertNotNull(ctxManager);

		AccessManager accessManager = (AccessManager) getService(context, AccessManager.class);
		assertNotNull(accessManager);

		Factory factory = (Factory) getService(context, Factory.class, "(factory.name=ComponentUsingBindMethods)");
		assertNotNull(factory);

		ComponentInstance instance = factory.createComponentInstance(null);
		assertNotNull(instance);

		accessManager.setDeviceAccess("test-handler-app", "BinaryLight-X001",  DeviceAccessPolicy.TOTAL);
		
		createDeviceService("BinaryLight-X001");

		InstanceDescription description = instance.getInstanceDescription();

		DependencyHandlerDescription dhd = (DependencyHandlerDescription) description
		      .getHandlerDescription("fr.liglab.adele.icasa.dependency.handler.annotations:requiresdevice");

		
		
		if (dhd != null) {
			for (DependencyDescription d : dhd.getDependencies()) {
				List<ServiceReference> refs = d.getServiceReferences();

				if (refs != null) {
					for (ServiceReference serviceReference : refs) {
						System.out.println("---->" + serviceReference.getProperty(GenericDevice.DEVICE_SERIAL_NUMBER));
					}
				}
			}
		}

	}

	public ComponentInstance createDeviceInstance(String deviceFactory, String deviceId) {

		String filter = "(factory.name=" + deviceFactory + ")";
		Factory factory = (Factory) getService(context, Factory.class, filter);

		ComponentInstance instance = null;
		try {
			Dictionary dictionary = new Hashtable();
			dictionary.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);

			instance = factory.createComponentInstance(dictionary);

		} catch (UnacceptableConfiguration e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instance;
	}

	public void createDeviceService(String deviceId) {
		BinaryLightMockImpl lightMockImpl = new BinaryLightMockImpl(deviceId);

		Dictionary properties = new Hashtable();
		properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);

		context.registerService(new String[] { GenericDevice.class.getName(), BinaryLight.class.getName() },
		      lightMockImpl, properties);

	}

	private URL getDeploymentPackageArtifactURL(String artifactID) throws MalformedURLException {
		MavenArtifactProvisionOption option = mavenBundle().groupId("fr.liglab.adele.icasa").artifactId(artifactID)
		      .type("dp").versionAsInProject();
		return new URL(option.getURL());
	}

	private DeploymentPackage installDeploymentPackage(String dpName) throws DeploymentException, IOException {

		URL url = getDeploymentPackageArtifactURL(dpName);

		System.out.println("==========> Installing " + url);
		return dpAdmin.installDeploymentPackage(url.openStream());
	}

	private void startTestComponentBundle() {

		printBundles("Point 1");

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		printBundles("Point 2");

		Bundle bundle = context.getBundle("test-component-handler");
		try {
			bundle.start();
		} catch (BundleException e) {
			e.printStackTrace();
		}
	}

	private void printBundles(String header) {
		Bundle[] bundles = context.getBundles();

		System.out.println("=================== " + header + " =================");
		System.out.println("Total " + bundles.length);
		for (Bundle bundle : bundles) {
			System.out.println(" ---> " + bundle.getSymbolicName());
		}

	}

}
