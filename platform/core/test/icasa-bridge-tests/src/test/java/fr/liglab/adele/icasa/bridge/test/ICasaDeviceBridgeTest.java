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
package fr.liglab.adele.icasa.bridge.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.medical.test.common.ICasaHelper.waitForIt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.newBundle;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.medical.application.Application;
import org.medical.application.ApplicationManager;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.DependRegistration;
import org.medical.device.manager.Device;
import org.medical.device.manager.DeviceDependencies;
import org.medical.device.manager.DeviceManager;
import org.medical.device.manager.Fault;
import org.medical.device.manager.GlobalDeviceManager;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.ProvidedDevice;
import fr.liglab.adele.icasa.bridge.test.app1.App1;
import fr.liglab.adele.icasa.bridge.test.app1.App1Activator;
import fr.liglab.adele.icasa.bridge.test.app2.App2;
import fr.liglab.adele.icasa.bridge.test.app2.App2Activator;
import fr.liglab.adele.icasa.device.GenericDevice;

import org.medical.test.common.ICasaAbstractTest;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.container.def.PaxRunnerOptions;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Integration test for the device manager component.
 * @author Thomas Leveque
 */
@RunWith(JUnit4TestRunner.class)
public class ICasaDeviceBridgeTest extends ICasaAbstractTest {
    

    private static final String APP1_ID = "app1";
	private static final String APP2_ID = "app2";

	@Before
    @Override
    public void setUp() {
    	super.setUp();
    }
    
    @Configuration
	public static Option[] deployBundles() {
		return CoreOptions.options(
				
				CoreOptions.provision(
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo.handler.extender").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo.composite").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo.api").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.http.jetty").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.gogo.command").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.gogo.shell").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo.arch.gogo").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.webconsole").versionAsInProject(),
				mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo.webconsole").versionAsInProject(),
                mavenBundle().groupId("commons-logging").artifactId("org.ow2.chameleon.commons.logging").versionAsInProject(),
                mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("test.common").versionAsInProject(),
                mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("common").versionAsInProject(),
                mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("application.api").versionAsInProject(),
                mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("application.impl").versionAsInProject(),
                mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("device.manager.api").versionAsInProject(),
                mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("device.manager.impl").versionAsInProject(),
                mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("icasa.device.model.bridge").versionAsInProject(),
                mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("device.api").versionAsInProject()
				),
				
				CoreOptions.provision(
						newBundle().add(ICasaDeviceBridgeTest.class)
						.set(Constants.IMPORT_PACKAGE, "org.osgi.service.http,org.osgi.util.tracker,org.osgi.framework,org.junit," + 
						"org.medical.test.common,org.medical.application,org.medical.device.manager," +
								"fr.liglab.adele.icasa.device")
						.set(Constants.EXPORT_PACKAGE, "fr.liglab.adele.icasa.bridge.test")
						.set(Constants.BUNDLE_SYMBOLICNAME, ICasaDeviceBridgeTest.class.getSimpleName()).build(),
						
						newBundle().add(App1.class).add(App1Activator.class)
						.set(Constants.BUNDLE_ACTIVATOR, App1Activator.class.getName())
						.set(Constants.DYNAMICIMPORT_PACKAGE, "*")
						.set(Constants.EXPORT_PACKAGE, "fr.liglab.adele.icasa.bridge.test.app1")
						.set(Constants.BUNDLE_SYMBOLICNAME, App1.class.getSimpleName())
						.set(Application.APP_ID_BUNDLE_HEADER, "app1")
						.set(Application.APP_VERSION_BUNDLE_HEADER, "1.1")
						.build(),
						
						newBundle().add(App2.class).add(App2Activator.class)
						.set(Constants.BUNDLE_ACTIVATOR, App2Activator.class.getName())
						.set(Constants.DYNAMICIMPORT_PACKAGE, "*")
						.set(Constants.EXPORT_PACKAGE, "fr.liglab.adele.icasa.bridge.test.app2")
						.set(Constants.BUNDLE_SYMBOLICNAME, App2.class.getSimpleName())
						.set(Application.APP_ID_BUNDLE_HEADER, "app2")
						.set(Application.APP_VERSION_BUNDLE_HEADER, "3.2.5")
						.build()
				),
				
				CoreOptions.when( Boolean.getBoolean( "isDebugEnabled" ) ).useOptions(
						PaxRunnerOptions.vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"),
						CoreOptions.waitForFrameworkStartup()
				)
				
				);
	}
    
    @Test
    public void testGenericDeviceVisibility() {
    	//wait for the service to be available.
        waitForIt(100);
        
        GenericDevice deviceImpl = mock(GenericDevice.class);
        final String devId = "123f4";
		when(deviceImpl.getSerialNumber()).thenReturn(devId);
		when(deviceImpl.getFault()).thenReturn(GenericDevice.FAULT_YES);
		when(deviceImpl.getState()).thenReturn(GenericDevice.STATE_ACTIVATED);
		when(deviceImpl.getLocation()).thenReturn("Undefined");
		ServiceRegistration sr = icasa.registerService(deviceImpl, GenericDevice.class);
        
        BundleContext app1Context = getBundleContext(APP1_ID);
        GenericDevice app1GenDevice = (GenericDevice) getServiceObject(GenericDevice.class, app1Context);
        assertNull(app1GenDevice);
        
        DeviceManager deviceMgr = (DeviceManager) getServiceObject(DeviceManager.class, app1Context);
        DeviceDependencies dependencies = new DeviceDependencies();
        dependencies.includes().all();
        
        DependRegistration depReg = deviceMgr.addDependencies(dependencies);
        waitForResolution(depReg);
        
        ApplicationDevice app1device = (ApplicationDevice) getServiceObject(ApplicationDevice.class, app1Context);
        assertNotNull(app1device);
        assertEquals(deviceImpl.getSerialNumber(), app1device.getId());
        assertEquals(deviceImpl.getLocation(), app1device.getPropertyValue("Location"));
        
        app1GenDevice = (GenericDevice) getServiceObject(GenericDevice.class, app1Context);
        assertNull(app1GenDevice);
        
        depReg.getDependencies().exportsTo(GenericDevice.class);
        depReg.updates();
        
        app1GenDevice = (GenericDevice) getServiceObject(GenericDevice.class, app1Context);
        assertNotNull(app1GenDevice);
        assertEquals(deviceImpl.getSerialNumber(), app1GenDevice.getSerialNumber());
        
        //cleanup
		depReg.unregister();
        sr.unregister();
    }
    
    @Test
    public void testDeviceAttr() {
    	//wait for the service to be available.
        waitForIt(100);
        
        GenericDevice deviceImpl = mock(PPDevice.class);
        final String devId = "123f5";
		when(deviceImpl.getSerialNumber()).thenReturn(devId);
		when(deviceImpl.getFault()).thenReturn(GenericDevice.FAULT_YES);
		when(deviceImpl.getState()).thenReturn(GenericDevice.STATE_ACTIVATED);
		when(deviceImpl.getLocation()).thenReturn("Undefined");
		ServiceRegistration sr = icasa.registerService(deviceImpl, GenericDevice.class);
        
        BundleContext app1Context = getBundleContext(APP1_ID);
        GenericDevice app1GenDevice = (GenericDevice) getServiceObject(GenericDevice.class, app1Context);
        assertNull(app1GenDevice);
        
        DeviceManager deviceMgr = (DeviceManager) getServiceObject(DeviceManager.class, app1Context);
        DeviceDependencies dependencies = new DeviceDependencies();
        dependencies.includes().all();
        
        DependRegistration depReg = deviceMgr.addDependencies(dependencies);
        waitForResolution(depReg);
        
        ApplicationDevice app1device = (ApplicationDevice) getServiceObject(ApplicationDevice.class, app1Context);
        assertNotNull(app1device);
        assertEquals(deviceImpl.getSerialNumber(), app1device.getId());
        assertEquals(deviceImpl.getLocation(), app1device.getPropertyValue("Location"));
        assertEquals(deviceImpl.getState(), app1device.getPropertyValue("State"));
        assertEquals(deviceImpl.getFault().equals(GenericDevice.FAULT_YES), app1device.getGlobalFault().equals(Fault.YES));
        
        //cleanup
		depReg.unregister();
        sr.unregister();
    }
    
    @Test
    public void testExportedGenericDeviceAttr() {
    	//wait for the service to be available.
        waitForIt(100);
        
        GenericDevice deviceImpl = mock(GenericDevice.class);
        final String devId = "123f5";
		when(deviceImpl.getSerialNumber()).thenReturn(devId);
		when(deviceImpl.getFault()).thenReturn(GenericDevice.FAULT_NO);
		when(deviceImpl.getState()).thenReturn(GenericDevice.STATE_ACTIVATED);
		when(deviceImpl.getLocation()).thenReturn("Undefined");
		ServiceRegistration sr = icasa.registerService(deviceImpl, GenericDevice.class);
        
        BundleContext app1Context = getBundleContext(APP1_ID);
        GenericDevice app1GenDevice = (GenericDevice) getServiceObject(GenericDevice.class, app1Context);
        assertNull(app1GenDevice);
        
        DeviceManager deviceMgr = (DeviceManager) getServiceObject(DeviceManager.class, app1Context);
        DeviceDependencies dependencies = new DeviceDependencies();
        dependencies.exportsTo(GenericDevice.class).includes().all();
        
        DependRegistration depReg = deviceMgr.addDependencies(dependencies);
        waitForResolution(depReg);
        
        GenericDevice app1device = (GenericDevice) getServiceObject(GenericDevice.class, app1Context);
        assertNotNull(app1device);
        assertEquals(deviceImpl.getSerialNumber(), app1device.getSerialNumber());
        assertEquals(deviceImpl.getFault(), app1device.getFault());
        assertEquals(deviceImpl.getState(), app1device.getState());
        assertEquals(deviceImpl.getLocation(), app1device.getLocation());
        
        //TODO check device service attributes
        
        //cleanup
		depReg.unregister();
        sr.unregister();
    }

	private void waitForResolution(DependRegistration depReg) {
		int NB_TIMES = 5;
		for (int i = 0; i < NB_TIMES; i++) {
			if (depReg.isResolved()) {
				break;
			}
			if (i == NB_TIMES - 1)
				waitForIt(100);
		}
	}

	private Application getApplication(String appId) {
		return getApplicationManagerService().getApplication(appId); 
	}
	
	private BundleContext getBundleContext(String appId) {
		Application app = getApplicationManagerService().getApplication(appId);
		
		return app.getBundles().iterator().next().getBundleContext();
	}

	private ApplicationManager getApplicationManagerService() {
		return icasa.getServiceObject(ApplicationManager.class);
	}

	private GlobalDeviceManager getGlobalDeviceManagerService() {
		return icasa.getServiceObject(GlobalDeviceManager.class);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getServiceObject(Class<T> klass, BundleContext specContext) {
		ServiceReference sref = null;

		sref = specContext.getServiceReference(klass.getName());

		if (sref != null) {
			T service = (T) specContext.getService(sref);
			specContext.ungetService(sref);
			return service;
		} else {
			return null;
		}
	}
	
	public class DevServListener implements ServiceListener {

		public boolean knownDevEvent = false;
		public boolean providedDevEvent = false;
		public boolean appDevEvent = false;
		public boolean availableDevEvent = false;
		public boolean devEvent = false;
		
		public void serviceChanged(ServiceEvent event) {
			final ServiceReference sr = event.getServiceReference();
			String[] interfaces = (String[]) sr.getProperty(Constants.OBJECTCLASS);
			for (String interf : interfaces) {
				if (ProvidedDevice.class.getName().equals(interf))
					providedDevEvent = true;
				if (KnownDevice.class.getName().equals(interf))
					knownDevEvent = true;
				if (AvailableDevice.class.getName().equals(interf))
					availableDevEvent = true;
				if (ApplicationDevice.class.getName().equals(interf))
					appDevEvent = true;
				if (Device.class.getName().equals(interf))
					devEvent = true;
			}
		}
		
		public boolean hasProvidedDevEvent() {
			return providedDevEvent;
		}

		public boolean hasOnlyDevEvent() {
			return !providedDevEvent && !knownDevEvent && !appDevEvent && !availableDevEvent && devEvent; 
		}
		
		public boolean hasDevEvent() {
			return devEvent; 
		}
		
	}
}

