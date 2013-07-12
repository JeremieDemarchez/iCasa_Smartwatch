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
package test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.repository;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import fr.liglab.adele.icasa.application.Application;
import fr.liglab.adele.icasa.application.ApplicationManager;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SimpleTest {

	@Inject
	BundleContext context;

	private Map<String, Object> eventProperties;

	private DeploymentAdmin deploymentPackageAdmin;

	private Object monitor = new Object();

	@Configuration
	public Option[] config() { // Reduce log level.
		return options(junitBundles(), mockitoBundles(), projectConfiguration());
	}

	public CompositeOption mockitoBundles() {
		CompositeOption mockitoConfig = new DefaultCompositeOption(
		// Repository required to load harmcrest (OSGi-fied version).
		      repository("http://repository.springsource.com/maven/bundles/external").id(
		            "com.springsource.repository.bundles.external"),

		      // Repository required to load harmcrest (OSGi-fied version).
		      repository("http://repo1.maven.org/maven2/").id("central"),
		      // Mockito without Hamcrest and Objenesis
		      mavenBundle("org.mockito", "mockito-core", "1.9.5"),
		      // Hamcrest with a version matching the range expected by Mockito
		      mavenBundle("org.hamcrest", "com.springsource.org.hamcrest.core", "1.1.0"),

		      // Objenesis with a version matching the range expected by Mockito
		      wrappedBundle(mavenBundle("org.objenesis", "objenesis", "1.2")).exports("*;version=1.2"),

		      // The default JUnit bundle also exports Hamcrest, but with an (incorrect) version of
		      // 4.9 which does not match the Mockito import. When deployed after the hamcrest bundles, it gets
		      // resolved correctly.
		      /*
				 * Felix has implicit boot delegation enabled by default. It conflicts with Mockito: java.lang.LinkageError:
				 * loader constraint violation in interface itable initialization: when resolving method
				 * "org.osgi.service.useradmin.User$$EnhancerByMockitoWithCGLIB$$dd2f81dc
				 * .newInstance(Lorg/mockito/cglib/proxy/Callback;)Ljava/lang/Object;" the class loader (instance of
				 * org/mockito/internal/creation/jmock/SearchingClassLoader) of the current class,
				 * org/osgi/service/useradmin/User$$EnhancerByMockitoWithCGLIB$$dd2f81dc, and the class loader (instance of
				 * org/apache/felix/framework/BundleWiringImpl$BundleClassLoaderJava5) for interface
				 * org/mockito/cglib/proxy/Factory have different Class objects for the type org/mockito/cglib/
				 * proxy/Callback used in the signature
				 * 
				 * So we disable the bootdelegation. this property has no effect on the other OSGi implementation.
				 */
		      frameworkProperty("felix.bootdelegation.implicit").value("false"));
		return mockitoConfig;
	}

	public CompositeOption projectConfiguration() {
		CompositeOption projectConfig = new DefaultCompositeOption(mavenBundle("de.akquinet.gomobile",
		      "deployment-admin-impl", "1.0.2-SNAPSHOT"), mavenBundle("org.apache.felix", "org.apache.felix.eventadmin",
		      "1.3.0"), mavenBundle("org.apache.felix", "org.apache.felix.ipojo", "1.10.1"), mavenBundle(
		      "fr.liglab.adele.icasa", "common", "1.1.1-SNAPSHOT"), mavenBundle("fr.liglab.adele.icasa",
		      "application.api", "1.1.1-SNAPSHOT"), mavenBundle("fr.liglab.adele.icasa", "application.impl",
		      "1.1.1-SNAPSHOT"));
		return projectConfig;
	}

	// org.apache.felix.eventadmin

	@Before
	public void setUp() {

		String[] topics = new String[] { "org/osgi/service/deployment/COMPLETE" };

		Dictionary props = new Hashtable();
		props.put(EventConstants.EVENT_TOPIC, topics);
		context.registerService(EventHandler.class.getName(), new TestEventHandler(), props);

		
		
		
		deploymentPackageAdmin = mock(DeploymentAdmin.class);
		// context.registerService(DeploymentAdmin.class, deploymentPackageAdmin, new Hashtable());

		BundleInfo bundleInfo1 = mock(BundleInfo.class);
		when(bundleInfo1.getSymbolicName()).thenReturn("bundle-1");
		when(bundleInfo1.getVersion()).thenReturn(new Version(1, 0, 0));
		BundleInfo bundleInfo2 = mock(BundleInfo.class);
		when(bundleInfo2.getSymbolicName()).thenReturn("bundle-2");
		when(bundleInfo2.getVersion()).thenReturn(new Version(1, 0, 0));
		BundleInfo bundleInfo3 = mock(BundleInfo.class);
		when(bundleInfo3.getSymbolicName()).thenReturn("bundle-3");
		when(bundleInfo3.getVersion()).thenReturn(new Version(1, 0, 0));

		DeploymentPackage package1 = mock(DeploymentPackage.class);
		when(package1.getName()).thenReturn("deployment-package-1");
		when(package1.getHeader(Application.APP_ID_BUNDLE_HEADER)).thenReturn("follow-me-app");
		when(package1.getHeader(Application.APP_NAME_BUNDLE_HEADER)).thenReturn("follow-me-app");
		when(package1.getHeader(Application.APP_VERSION_BUNDLE_HEADER)).thenReturn("1.0.0");
		when(package1.getBundleInfos()).thenReturn(new BundleInfo[] { bundleInfo1, bundleInfo2 });

		DeploymentPackage package2 = mock(DeploymentPackage.class);
		when(package2.getName()).thenReturn("deployment-package-2");
		when(package2.getHeader(Application.APP_ID_BUNDLE_HEADER)).thenReturn("gas-alarm-app");
		when(package2.getHeader(Application.APP_NAME_BUNDLE_HEADER)).thenReturn("gas-alarm-app");
		when(package2.getHeader(Application.APP_VERSION_BUNDLE_HEADER)).thenReturn("1.0.0");
		when(package2.getBundleInfos()).thenReturn(new BundleInfo[] { bundleInfo1 });

		DeploymentPackage package3 = mock(DeploymentPackage.class);
		when(package3.getName()).thenReturn("deployment-package-3");
		when(package3.getHeader(Application.APP_ID_BUNDLE_HEADER)).thenReturn("gas-alarm-app");
		when(package3.getHeader(Application.APP_NAME_BUNDLE_HEADER)).thenReturn("gas-alarm-app");
		when(package3.getHeader(Application.APP_VERSION_BUNDLE_HEADER)).thenReturn("1.0.0");
		when(package3.getBundleInfos()).thenReturn(new BundleInfo[] { bundleInfo2, bundleInfo3 });

		when(deploymentPackageAdmin.getDeploymentPackage("deployment-package-1")).thenReturn(package1);
		when(deploymentPackageAdmin.getDeploymentPackage("deployment-package-2")).thenReturn(package2);
		when(deploymentPackageAdmin.getDeploymentPackage("deployment-package-3")).thenReturn(package3);

	}

	@After
	public void tearDown() {

	}

	@Ignore
	@Test
	public void ucheckDeploymentService() {
		ServiceReference reference = context.getServiceReference(DeploymentAdmin.class);
		Assert.assertNotNull(reference);
		DeploymentAdmin admin2 = (DeploymentAdmin) context.getService(reference);
		Assert.assertEquals(admin2, deploymentPackageAdmin);

		String dpName = admin2.getDeploymentPackage("deployment-package-1").getName();

		Assert.assertEquals("deployment-package-1", dpName);
	}

	@Ignore
	@Test
	public void checkInstallOneApplicationOld() {
		EventHandler handler = (EventHandler) getService(EventHandler.class);
		Assert.assertNotNull(handler);

		handler.handleEvent(getEvent("INSTALL", "deployment-package-1"));
		handler.handleEvent(getEvent("COMPLETE", "deployment-package-1"));

		ApplicationManager manager = (ApplicationManager) getService(ApplicationManager.class);

		Assert.assertNotNull(manager.getApplicationOfBundle("bundle-1"));
		Assert.assertEquals("follow-me-app", manager.getApplicationOfBundle("bundle-1").getName());

		Assert.assertEquals(1, manager.getApplications().size());

	}

	private Event getEvent(String type, String dpName) {
		Event event = new Event("org/osgi/service/deployment/" + type, getEventProperties(dpName));
		return event;
	}

	private Map<String, Object> getEventProperties(String dpName) {
		if (eventProperties == null) {
			eventProperties = new HashMap<String, Object>();
			eventProperties.put("successful", true);
		}
		eventProperties.put(DeploymentPackage.EVENT_DEPLOYMENTPACKAGE_NAME, dpName);
		return eventProperties;
	}

	@Ignore
	@Test
	public void checkInstallApplicationWithTwoDPsOld() {

		EventHandler handler = (EventHandler) getService(EventHandler.class);
		Assert.assertNotNull(handler);

		ApplicationManager manager = (ApplicationManager) getService(ApplicationManager.class);
		Assert.assertNotNull(manager);

		handler.handleEvent(getEvent("INSTALL", "deployment-package-2"));
		handler.handleEvent(getEvent("COMPLETE", "deployment-package-2"));

		Assert.assertNotNull(manager.getApplicationOfBundle("bundle-1"));
		Assert.assertEquals("gas-alarm-app", manager.getApplicationOfBundle("bundle-1").getName());

		Assert.assertNull(manager.getApplicationOfBundle("bundle-2"));
		Assert.assertNull(manager.getApplicationOfBundle("bundle-3"));

		handler.handleEvent(getEvent("INSTALL", "deployment-package-3"));
		handler.handleEvent(getEvent("COMPLETE", "deployment-package-3"));

		Assert.assertNotNull(manager.getApplicationOfBundle("bundle-2"));
		Assert.assertEquals("gas-alarm-app", manager.getApplicationOfBundle("bundle-2").getName());

		Assert.assertNotNull(manager.getApplicationOfBundle("bundle-3"));

		Assert.assertEquals(manager.getApplicationOfBundle("bundle-1"), manager.getApplicationOfBundle("bundle-2"));
		Assert.assertEquals(manager.getApplicationOfBundle("bundle-1"), manager.getApplicationOfBundle("bundle-3"));
		Assert.assertEquals(manager.getApplicationOfBundle("bundle-2"), manager.getApplicationOfBundle("bundle-3"));

		Assert.assertNull(manager.getApplicationOfBundle("bundle-0"));

		Assert.assertNotNull(manager.getApplication("gas-alarm-app"));

		Assert.assertEquals(1, manager.getApplications().size());

	}

	@Test
	public void checkInstallOneApplication() throws DeploymentException, IOException {
		DeploymentAdmin dpAdmin = (DeploymentAdmin) getService(DeploymentAdmin.class);
		Assert.assertNotNull(dpAdmin);

		URL url = new URL("mvn:fr.liglab.adele.icasa/follow-me-test-app-dp-1/1.1.1-SNAPSHOT/dp");
		dpAdmin.installDeploymentPackage(url.openStream());
		
		//delay(100);		

		ApplicationManager manager = (ApplicationManager) getService(ApplicationManager.class);
		Assert.assertNotNull(manager);

		int attemps = 0;
		int appsNumber = manager.getApplications().size();
		while((appsNumber<1) || (attemps>10)) {
			try {
	         Thread.sleep(10);
	         appsNumber = manager.getApplications().size();
	         attemps++;
	         System.out.println("---Attemps: " + attemps);
         } catch (InterruptedException e) {
	         e.printStackTrace();
         }
		}

		// Only one app installed
		assertEquals(1, appsNumber);

		// The instaled application according with bundles
		assertNotNull(manager.getApplicationOfBundle("test-bundle-1"));
		
		// Gets the application according with the bundle
		assertEquals("follow-me-test-app", manager.getApplicationOfBundle("test-bundle-1").getId());

		// No application containing a test-bundle-2 bundle
		assertNull(manager.getApplicationOfBundle("test-bundle-2"));

	}

	@Test
	public void checkInstallApplicationWithTwoDPs() throws DeploymentException, IOException {
		DeploymentAdmin dpAdmin = (DeploymentAdmin) getService(DeploymentAdmin.class);
		Assert.assertNotNull(dpAdmin);

		URL url = new URL("mvn:fr.liglab.adele.icasa/gas-alarm-test-app-dp-1/1.1.1-SNAPSHOT/dp");
		dpAdmin.installDeploymentPackage(url.openStream());
		
		delay(100);	

		ApplicationManager manager = (ApplicationManager) getService(ApplicationManager.class);
		Assert.assertNotNull(manager);

		// Only one app installed
		assertEquals(1, manager.getApplications().size());

		Assert.assertNotNull(manager.getApplicationOfBundle("test-bundle-2"));
		Assert.assertEquals("gas-alarm-test-app", manager.getApplicationOfBundle("test-bundle-2").getName());

		Assert.assertNull(manager.getApplicationOfBundle("test-bundle-3"));

		url = new URL("mvn:fr.liglab.adele.icasa/gas-alarm-test-app-dp-2/1.1.1-SNAPSHOT/dp");
		dpAdmin.installDeploymentPackage(url.openStream());

		delay(1000);
		

		// Only one application reamins
		Assert.assertEquals(1, manager.getApplications().size());

		
		Assert.assertNotNull(manager.getApplication("gas-alarm-test-app"));
		
		Set<Bundle> bundles = manager.getApplication("gas-alarm-test-app").getBundles();		
		Set<String> expectedBundles = new HashSet<String>();
		expectedBundles.add("test-bundle-2");
		expectedBundles.add("test-bundle-3");
		
		// Al expected bundles in the list
		for (Bundle bundle : bundles) {
			Assert.assertTrue(expectedBundles.contains(bundle.getSymbolicName()));
      }
		
		// No more of expected bundles in the list
		Assert.assertEquals(expectedBundles.size(), bundles.size());
		

		
		Assert.assertNotNull(manager.getApplicationOfBundle("test-bundle-3"));
		Assert.assertEquals("gas-alarm-test-app", manager.getApplicationOfBundle("test-bundle-3").getName());

		Assert.assertEquals(manager.getApplicationOfBundle("test-bundle-2"),
		      manager.getApplicationOfBundle("test-bundle-3"));

	}

	private void delay(int millis) {
		try {
			synchronized (monitor) {
				try {
			      monitor.wait();
		      } catch (InterruptedException e) {
			      e.printStackTrace();
		      }
	      }
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Object getService(Class clazz) {
		ServiceReference reference = context.getServiceReference(clazz);
		if (reference != null)
			return context.getService(reference);
		return null;
	}


	private void printBundles() {
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle != null) {
				System.out.println("=======> " + bundle.getSymbolicName());
			}
		}
	}

	class TestEventHandler implements EventHandler {

		public void handleEvent(Event event) {
			synchronized (monitor) {
				monitor.notify();
         }
		}

	}

}
