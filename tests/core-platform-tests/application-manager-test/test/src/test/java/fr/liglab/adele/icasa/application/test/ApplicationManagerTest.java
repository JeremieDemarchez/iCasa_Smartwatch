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

import fr.liglab.adele.commons.test.utils.Condition;
import fr.liglab.adele.commons.test.utils.TestUtils;
import fr.liglab.adele.icasa.application.ApplicationManager;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ApplicationManagerTest {

	@Inject
	BundleContext context;

	
	private ApplicationManager manager;
	
	private DeploymentAdmin dpAdmin;
	
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
				mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("common").versionAsInProject(),
				mavenBundle().groupId("fr.liglab.adele.common").artifactId("base.distribution.test").versionAsInProject());
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
		
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(1));
		
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
		
		// Only one app installed
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(1));

		
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
		
		
		// Al expected bundles in the list
		for (Bundle bundle : bundles) {
			Assert.assertTrue(expectedBundles.contains(bundle.getSymbolicName()));
      }
		
		// No more of expected bundles in the list	
		TestUtils.testConditionWithTimeout(new BundleNumberCondition("gas-alarm-test-app", expectedBundles.size()));
		
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

				
		// Only one app installed
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(1));

		// The installed application is not null
		assertNotNull(manager.getApplication("follow-me-test-app"));
		
		// Gets the application according with the bundle
		assertEquals("follow-me-test-app", manager.getApplicationOfBundle("test-bundle-1").getId());
		
		
		followMeDP1.uninstall();
		
   	// No application installed
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(0));
			
		// The follow-me-test-app is not more in the platform
		assertNull(manager.getApplication("follow-me-test-app"));
	}
	
	
	@Test
	public void checkInstallAndUninstallOneApplicationWithTwoDPs() throws DeploymentException, IOException {
		Assert.assertNotNull(dpAdmin);

		DeploymentPackage gasAlarmDP1 = installDeploymentPackage("gas-alarm-test-app-dp-1");
		
		Assert.assertNotNull(manager);
		
		// Only one app installed
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(1));

		Assert.assertNotNull(manager.getApplicationOfBundle("test-bundle-2"));
		Assert.assertEquals("gas-alarm-test-app", manager.getApplicationOfBundle("test-bundle-2").getName());

		Assert.assertNull(manager.getApplicationOfBundle("test-bundle-3"));

		DeploymentPackage gasAlarmDP2 = installDeploymentPackage("gas-alarm-test-app-dp-2");

		Assert.assertNotNull(manager.getApplication("gas-alarm-test-app"));
				
				
		// No more of expected bundles in the list
		TestUtils.testConditionWithTimeout(new BundleNumberCondition("gas-alarm-test-app", 2));
		
		gasAlarmDP1.uninstall();
		
				
		// DP2 contains 2 bundles
		TestUtils.testConditionWithTimeout(new BundleNumberCondition("gas-alarm-test-app", 2));
		
		// Only one app installed
		Assert.assertEquals(1, manager.getApplications().size());
		
		gasAlarmDP2.uninstall();
		

		// No applications installed
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(0));

	}

	@Test
	public void checkInstallAndUninstallTwoApplications() throws DeploymentException, IOException {
		Assert.assertNotNull(dpAdmin);

		DeploymentPackage gasAlarmDP1 = installDeploymentPackage("gas-alarm-test-app-dp-1");
		
		Assert.assertNotNull(manager);


		// Only one app installed
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(1));

		Assert.assertNotNull(manager.getApplicationOfBundle("test-bundle-2"));
		Assert.assertEquals("gas-alarm-test-app", manager.getApplicationOfBundle("test-bundle-2").getName());

		Assert.assertNull(manager.getApplicationOfBundle("test-bundle-3"));

		DeploymentPackage gasAlarmDP2 = installDeploymentPackage("gas-alarm-test-app-dp-2");

		Assert.assertNotNull(manager.getApplication("gas-alarm-test-app"));
				
								
				
		// No more of expected bundles in the list
		TestUtils.testConditionWithTimeout(new BundleNumberCondition("gas-alarm-test-app", 2));

		
		DeploymentPackage controlDP1 = installDeploymentPackage("control-test-app-dp-1");
		
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(2));
		
		DeploymentPackage controlDP2 = installDeploymentPackage("control-test-app-dp-2");
		
				
				
		// No more of expected bundles in the list
		TestUtils.testConditionWithTimeout(new BundleNumberCondition("gas-alarm-test-app", 2));
				
		gasAlarmDP1.uninstall();
				
		// DP2 contains 2 bundles
		TestUtils.testConditionWithTimeout(new BundleNumberCondition("gas-alarm-test-app", 2));
		
		// Only one app installed
		Assert.assertEquals(2, manager.getApplications().size());
		
		gasAlarmDP2.uninstall();		

		// Only one application remain
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(1));
		
		
		controlDP1.uninstall();
		
		
		TestUtils.testConditionWithTimeout(new BundleNumberCondition("control-test-app", 2));
		
		// Only one app installed
		Assert.assertEquals(1, manager.getApplications().size());
		
		controlDP2.uninstall();
		
		// All applications removed	
		TestUtils.testConditionWithTimeout(new ApplicationNumberCondition(0));

	}
	

	private Object getService(Class clazz) {
		ServiceReference reference = context.getServiceReference(clazz.getName());
		if (reference != null)
			return context.getService(reference);
		return null;
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
	
	
	class BundleNumberCondition implements Condition {

		private String m_appName;
		private int m_expectedBundles;
		
		public BundleNumberCondition(String appName, int expectedBundles) {
			m_appName = appName;
			m_expectedBundles = expectedBundles;
		}
		
		public boolean isChecked() {
			return (manager.getApplication(m_appName).getBundles().size() == m_expectedBundles);
      }

		public String getDescription() {
			return "Expected " + m_expectedBundles + " bundles in application " + m_appName +" in Application Manager";
      }
		
	}
	
	class ApplicationNumberCondition implements Condition {

		private int m_number;
		
		public ApplicationNumberCondition(int number) {
			m_number = number;
		}
		
		public boolean isChecked() {
	      return (m_number == manager.getApplications().size());
      }

		public String getDescription() {
	      return "Expected " + m_number + " applications in Application Manager";
      }
		
	}
	
}
