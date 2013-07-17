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
package fr.liglab.adele.icasa.application.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.repository;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

import fr.liglab.adele.icasa.application.ApplicationManager;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ApplicationManagerTest {

	@Inject
	BundleContext context;

	
	private ApplicationManager manager;
	
	private DeploymentAdmin dpAdmin;
	
	private ApplicationNumberConditionEvaluation appNumberConditionEvaluation = new ApplicationNumberConditionEvaluation();
	
	private BundleNumberConditionEvaluation gasAlarmBundleNumberConditionEvaluation = new BundleNumberConditionEvaluation("gas-alarm-test-app");

	private BundleNumberConditionEvaluation controlBundleNumberConditionEvaluation = new BundleNumberConditionEvaluation("control-test-app");
	
	@Configuration
	public Option[] config() { 
		return options(junitBundles(), projectConfiguration());
	}



	public CompositeOption projectConfiguration() {
		CompositeOption projectConfig = new DefaultCompositeOption(  
				mavenBundle().groupId("de.akquinet.gomobile").artifactId("deployment-admin-impl").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.eventadmin").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo").versionAsInProject(),
				mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("application.api").versionAsInProject(), 
				mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("application.impl").versionAsInProject(),
				mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("common").versionAsInProject());
		return projectConfig;
	}


	@Before
	public void setUp() {
		manager = (ApplicationManager) getService(ApplicationManager.class);
		dpAdmin = (DeploymentAdmin) getService(DeploymentAdmin.class);
	}

	@After
	public void tearDown() {

	}


	private URL getDeploymentPackageArtifactURL(String artifactID) throws MalformedURLException  {
		MavenArtifactProvisionOption option = mavenBundle().groupId("fr.liglab.adele.icasa")
		      .artifactId(artifactID).type("dp").versionAsInProject(); 
		return new URL(option.getURL());
	}

	private DeploymentPackage installDeploymentPackage(String dpName) throws DeploymentException, IOException {
		URL url =getDeploymentPackageArtifactURL(dpName);
		return dpAdmin.installDeploymentPackage(url.openStream());
	}
	
	@Test
	public void checkInstallOneApplication() throws DeploymentException, IOException {
		Assert.assertNotNull(dpAdmin);

		installDeploymentPackage("follow-me-test-app-dp-1");
		
		Assert.assertNotNull(manager);
		
		int currentApplicationsNumber = checkTimeoutNumberCondition(1, appNumberConditionEvaluation);

		// Only one app installed
		assertEquals(1, currentApplicationsNumber);

		// The instaled application according with bundles
		assertNotNull(manager.getApplicationOfBundle("test-bundle-1"));
		
		// Gets the application according with the bundle
		assertEquals("follow-me-test-app", manager.getApplicationOfBundle("test-bundle-1").getId());

		// No application containing a test-bundle-2 bundle
		assertNull(manager.getApplicationOfBundle("test-bundle-2"));

	}

	@Test
	public void checkInstallApplicationWithTwoDPs() throws DeploymentException, IOException {
		Assert.assertNotNull(dpAdmin);

		installDeploymentPackage("gas-alarm-test-app-dp-1");
		
		Assert.assertNotNull(manager);
		
		int currentApplicationsNumber = checkTimeoutNumberCondition(1, appNumberConditionEvaluation);

		// Only one app installed
		assertEquals(1, currentApplicationsNumber);

		
		//printBundles();
		
		Assert.assertNotNull(manager.getApplicationOfBundle("test-bundle-2"));
		Assert.assertEquals("gas-alarm-test-app", manager.getApplicationOfBundle("test-bundle-2").getName());

		Assert.assertNull(manager.getApplicationOfBundle("test-bundle-3"));

		installDeploymentPackage("gas-alarm-test-app-dp-2");

		Assert.assertNotNull(manager.getApplication("gas-alarm-test-app"));
		
		Set<Bundle> bundles = manager.getApplication("gas-alarm-test-app").getBundles();		
		Set<String> expectedBundles = new HashSet<String>();
		expectedBundles.add("test-bundle-2");
		expectedBundles.add("test-bundle-3");
		
		int currentBundlesNumber = checkTimeoutNumberCondition(expectedBundles.size(), gasAlarmBundleNumberConditionEvaluation);
		
		
		// Al expected bundles in the list
		for (Bundle bundle : bundles) {
			Assert.assertTrue(expectedBundles.contains(bundle.getSymbolicName()));
      }
		
		// No more of expected bundles in the list
		Assert.assertEquals(expectedBundles.size(), currentBundlesNumber);
		
		// Only one application remains
		Assert.assertEquals(1, manager.getApplications().size());
		
		
		Assert.assertNotNull(manager.getApplicationOfBundle("test-bundle-3"));
		Assert.assertEquals("gas-alarm-test-app", manager.getApplicationOfBundle("test-bundle-3").getName());

		Assert.assertEquals(manager.getApplicationOfBundle("test-bundle-2"),
		      manager.getApplicationOfBundle("test-bundle-3"));

	}
	
	
	
	@Test
	public void checkInstallAndUninstallOneApplication() throws DeploymentException, IOException {
		Assert.assertNotNull(dpAdmin);

		DeploymentPackage followMeDP1 = installDeploymentPackage("follow-me-test-app-dp-1");
		
		Assert.assertNotNull(manager);
		
		int currentApplicationsNumber = checkTimeoutNumberCondition(1, appNumberConditionEvaluation); 
				
		// Only one app installed
		assertEquals(1, currentApplicationsNumber);

		// The installed application is not null
		assertNotNull(manager.getApplication("follow-me-test-app"));
		
		// Gets the application according with the bundle
		assertEquals("follow-me-test-app", manager.getApplicationOfBundle("test-bundle-1").getId());
		
		
		followMeDP1.uninstall();
		
		currentApplicationsNumber = checkTimeoutNumberCondition(0, appNumberConditionEvaluation);
		
		// No application installed
		assertEquals(0, currentApplicationsNumber);
		
		// The follow-me-test-app is not more in the platform
		assertNull(manager.getApplication("follow-me-test-app"));
	}
	
	
	@Test
	public void checkInstallAndUninstallOneApplicationWithTwoDPs() throws DeploymentException, IOException {
		Assert.assertNotNull(dpAdmin);

		DeploymentPackage gasAlarmDP1 = installDeploymentPackage("gas-alarm-test-app-dp-1");
		
		Assert.assertNotNull(manager);
		
		int currentApplicationsNumber = checkTimeoutNumberCondition(1, appNumberConditionEvaluation);

		// Only one app installed
		assertEquals(1, currentApplicationsNumber);

		Assert.assertNotNull(manager.getApplicationOfBundle("test-bundle-2"));
		Assert.assertEquals("gas-alarm-test-app", manager.getApplicationOfBundle("test-bundle-2").getName());

		Assert.assertNull(manager.getApplicationOfBundle("test-bundle-3"));

		DeploymentPackage gasAlarmDP2 = installDeploymentPackage("gas-alarm-test-app-dp-2");

		Assert.assertNotNull(manager.getApplication("gas-alarm-test-app"));
				
		int currentBundlesNumber = checkTimeoutNumberCondition(2, gasAlarmBundleNumberConditionEvaluation);
				
		// No more of expected bundles in the list
		Assert.assertEquals(2, currentBundlesNumber);
		
		gasAlarmDP1.uninstall();
		
		currentBundlesNumber = checkTimeoutNumberCondition(2, gasAlarmBundleNumberConditionEvaluation);
				
		// DP2 contains 2 bundles
		Assert.assertEquals(2, currentBundlesNumber);
		
		// Only one app installed
		Assert.assertEquals(1, manager.getApplications().size());
		
		gasAlarmDP2.uninstall();
		
		
		currentApplicationsNumber = checkTimeoutNumberCondition(0, appNumberConditionEvaluation);

		// No applications installed
		assertEquals(0, currentApplicationsNumber);

	}

	@Test
	public void checkInstallAndUninstallTwoApplications() throws DeploymentException, IOException {
		Assert.assertNotNull(dpAdmin);

		DeploymentPackage gasAlarmDP1 = installDeploymentPackage("gas-alarm-test-app-dp-1");
		
		Assert.assertNotNull(manager);
		
		int currentApplicationsNumber = checkTimeoutNumberCondition(1, appNumberConditionEvaluation);

		// Only one app installed
		assertEquals(1, currentApplicationsNumber);

		Assert.assertNotNull(manager.getApplicationOfBundle("test-bundle-2"));
		Assert.assertEquals("gas-alarm-test-app", manager.getApplicationOfBundle("test-bundle-2").getName());

		Assert.assertNull(manager.getApplicationOfBundle("test-bundle-3"));

		DeploymentPackage gasAlarmDP2 = installDeploymentPackage("gas-alarm-test-app-dp-2");

		Assert.assertNotNull(manager.getApplication("gas-alarm-test-app"));
				
		int currentBundlesNumber = checkTimeoutNumberCondition(2, gasAlarmBundleNumberConditionEvaluation);
								
				
		// No more of expected bundles in the list
		Assert.assertEquals(2, currentBundlesNumber);
		
		DeploymentPackage controlDP1 = installDeploymentPackage("control-test-app-dp-1");
		
		
		currentApplicationsNumber = checkTimeoutNumberCondition(2, appNumberConditionEvaluation);

		assertEquals(2, currentApplicationsNumber);
		
		DeploymentPackage controlDP2 = installDeploymentPackage("control-test-app-dp-2");
		
		currentBundlesNumber = checkTimeoutNumberCondition(2, gasAlarmBundleNumberConditionEvaluation);
				
				
		// No more of expected bundles in the list
		Assert.assertEquals(2, currentBundlesNumber);
				
		gasAlarmDP1.uninstall();
		
		currentBundlesNumber = checkTimeoutNumberCondition(2, gasAlarmBundleNumberConditionEvaluation);
				
		// DP2 contains 2 bundles
		Assert.assertEquals(2, currentBundlesNumber);
		
		// Only one app installed
		Assert.assertEquals(2, manager.getApplications().size());
		
		gasAlarmDP2.uninstall();
		
		
		currentApplicationsNumber = checkTimeoutNumberCondition(1, appNumberConditionEvaluation);

		// Only one application remain
		assertEquals(1, currentApplicationsNumber);
		
		
		controlDP1.uninstall();
		
		currentBundlesNumber = checkTimeoutNumberCondition(2, controlBundleNumberConditionEvaluation);
		
		assertEquals(2, currentBundlesNumber);
		
		// Only one app installed
		Assert.assertEquals(1, manager.getApplications().size());
		
		controlDP2.uninstall();
		
		// All applications removed
		currentApplicationsNumber = checkTimeoutNumberCondition(0, appNumberConditionEvaluation);
		
		assertEquals(0, currentApplicationsNumber);

	}
	

	
	private int checkTimeoutNumberCondition(int expectedNumber, NumberConditionEvaluation conditionEvaluation) {
		int attempts = 0;
		int currentNumber = conditionEvaluation.getSize();
		while((currentNumber!=expectedNumber) && (attempts < 50)) {
			try {
	         Thread.sleep(20);
	         currentNumber = conditionEvaluation.getSize();
	         attempts++;
         } catch (InterruptedException e) {
         }
		}
		return currentNumber;
	}
	

	private Object getService(Class clazz) {
		ServiceReference reference = context.getServiceReference(clazz.getName());
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
	
	

	
	private CompositeOption mockitoBundles() {
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

	/**
	 * Used in checkTimeoutNumberCondition method to determine a size (of applications, bundles, etc) using a timeout.
	 * This interface to provide a option to evaluate the size expresion.
	 * 
	 * @author Gabriel
	 * 
	 */
	interface NumberConditionEvaluation {
		int getSize();
	}

	
	/**
	 * Size is the application number
	 * @author Gabriel
	 *
	 */
	class ApplicationNumberConditionEvaluation implements NumberConditionEvaluation {
		public int getSize() {
	      return manager.getApplications().size();
      }	
	}
	
	/**
	 * Size is bundle number in an application
	 * @author Gabriel
	 *
	 */
	class BundleNumberConditionEvaluation implements NumberConditionEvaluation {
		private String appName;
		
		public BundleNumberConditionEvaluation(String appName) {
			this.appName = appName;
		}
		
		public int getSize() {
	      return manager.getApplication(appName).getBundles().size();
      }
	}
}
