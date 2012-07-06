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
package org.medical.device.manager.test;

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
import org.medical.common.StateVariable;
import org.medical.common.VariableType;
import org.medical.common.impl.StateVariableImpl;
import org.medical.device.manager.ApplicationDevice;
import org.medical.device.manager.AvailableDevice;
import org.medical.device.manager.Device;
import org.medical.device.manager.DeviceDependencies;
import org.medical.device.manager.DeviceManager;
import org.medical.device.manager.GlobalDeviceManager;
import org.medical.device.manager.KnownDevice;
import org.medical.device.manager.ProvidedDevice;
import org.medical.device.manager.test.app1.App1;
import org.medical.device.manager.test.app1.App1Activator;
import org.medical.device.manager.test.app2.App2;
import org.medical.device.manager.test.app2.App2Activator;
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
public class DeviceManagerTest extends ICasaAbstractTest {
    

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
                mavenBundle().groupId("fr.liglab.adele.icasa").artifactId("device.manager.impl").versionAsInProject()
				),
				
				CoreOptions.provision(
						newBundle().add(DeviceManagerTest.class)
						.set(Constants.IMPORT_PACKAGE, "org.osgi.service.http,org.osgi.util.tracker,org.osgi.framework,org.junit," + 
						"org.medical.test.common,org.medical.application,org.medical.device.manager")
						.set(Constants.BUNDLE_SYMBOLICNAME, DeviceManagerTest.class.getSimpleName()).build(),
						
						newBundle().add(App1.class).add(App1Activator.class)
						.set(Constants.BUNDLE_ACTIVATOR, App1Activator.class.getName())
						.set(Constants.DYNAMICIMPORT_PACKAGE, "*")
						.set(Constants.EXPORT_PACKAGE, "org.medical.device.manager.test.app1")
						.set(Constants.BUNDLE_SYMBOLICNAME, App1.class.getSimpleName())
						.set(Application.APP_ID_BUNDLE_HEADER, "app1")
						.set(Application.APP_VERSION_BUNDLE_HEADER, "1.1")
						.build(),
						
						newBundle().add(App2.class).add(App2Activator.class)
						.set(Constants.BUNDLE_ACTIVATOR, App2Activator.class.getName())
						.set(Constants.DYNAMICIMPORT_PACKAGE, "*")
						.set(Constants.EXPORT_PACKAGE, "org.medical.device.manager.test.app2")
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
    
    /**
     * Basic Test, in order to know if the {@link DeviceManager} service implementation is correctly provided.
     */
    @Test
    public void testGlobalDeviceMgrAvailability() {
    	//wait for the service to be available.
        waitForIt(100);
        
        GlobalDeviceManager devManagerDelegate = getGlobalDeviceManagerService(); 
        
        assertNotNull(devManagerDelegate); 
    }
    
    @Test
    public void testApplicationMgrAvailability() {
    	//wait for the service to be available.
        waitForIt(100);
        
        ApplicationManager appManager = getApplicationManagerService(); 
        
        assertNotNull(appManager); 
    }
    
    @Test
    public void testListApplicationsAndDeviceManagerAvailability() {
    	//wait for the service to be available.
        waitForIt(100);
        
        ApplicationManager appMgr = getApplicationManagerService(); 
        final List<Application> applications = appMgr.getApplications();
        assertEquals(2, applications.size());
        
        boolean app1Found = false;
        boolean app2Found = false;
        for (Application app : applications) {
        	if (APP1_ID.equals(app.getId())) {
        		app1Found = true;
        		final Set<Bundle> bundles = app.getBundles();
        		assertEquals(1, bundles.size());
        		for (Bundle bundle : bundles) {
        			assertEquals(App1.class.getSimpleName(), bundle.getSymbolicName());
        		}
        		
        		BundleContext app1Context = getBundleContext(app.getId());
        		DeviceManager deviceMgr = (DeviceManager) getServiceObject(DeviceManager.class, app1Context);
        		assertNotNull(deviceMgr);
        		assertEquals(deviceMgr.getApplication().getId(), app.getId());
        	}
        	if (APP2_ID.equals(app.getId())) {
        		app2Found = true;
        		final Set<Bundle> bundles = app.getBundles();
        		assertEquals(1, bundles.size());
        		for (Bundle bundle : bundles) {
        			assertEquals(App2.class.getSimpleName(), bundle.getSymbolicName());
        		}
        		
        		BundleContext app2Context = getBundleContext(app.getId());
        		DeviceManager deviceMgr = (DeviceManager) getServiceObject(DeviceManager.class, app2Context);
        		assertNotNull(deviceMgr);
        		assertEquals(deviceMgr.getApplication().getId(), app.getId());
        	}
        }
        assertTrue(app1Found);
        assertTrue(app2Found);
    }
    
    @Test
    public void testTechnicalServicesVisibility() {
    	//wait for the service to be available.
        waitForIt(100);
        
        BundleContext app1Context = getBundleContext(APP1_ID);
        
        GlobalDeviceManager devManagerDelegate = (GlobalDeviceManager) getServiceObject(GlobalDeviceManager.class, app1Context);
        assertNull(devManagerDelegate); 
        
        ApplicationManager appManager = (ApplicationManager) getServiceObject(ApplicationManager.class, app1Context);
        assertNull(appManager); 
    }
    
    @Test
    public void testProvidedDevicesVisibility() {
    	//wait for the service to be available.
        waitForIt(100);
        
        ProvidedDevice deviceImpl = mock(ProvidedDevice.class);
        final String devId = "123f";
		when(deviceImpl.getId()).thenReturn(devId);
		
        BundleContext app1Context = getBundleContext(APP1_ID);
        final DevServListener devServListener = new DevServListener();
		app1Context.addServiceListener(devServListener);
        
        ServiceRegistration sr = icasa.registerService(deviceImpl, ProvidedDevice.class);
        
        ProvidedDevice device = (ProvidedDevice) getServiceObject(ProvidedDevice.class, app1Context);
        assertNull(device);
        
        // check events
        waitForIt(100);
        
        assertEquals(false, devServListener.hasProvidedDevEvent());

        //cleanup
        sr.unregister();
    }
    
    @Test
    public void testAvailableDeviceCreation() {
    	//wait for the service to be available.
        waitForIt(100);
        
        ProvidedDevice deviceImpl = mock(ProvidedDevice.class);
        final String devId = "123f1";
		when(deviceImpl.getId()).thenReturn(devId);
		ServiceRegistration sr = icasa.registerService(deviceImpl, ProvidedDevice.class);
        
        List<AvailableDevice> devices = getGlobalDeviceManagerService().getAvailableDevices();
        assertNotNull(devices);
        assertEquals(1, devices.size());
        assertEquals(devId, devices.get(0).getId());

        //cleanup
        sr.unregister();
    }
    
    @Test
    public void testKnownDeviceCreation() {
    	//wait for the service to be available.
        waitForIt(100);
        
        ProvidedDevice deviceImpl = mock(ProvidedDevice.class);
        final String devId = "123f2";
		when(deviceImpl.getId()).thenReturn(devId);
		ServiceRegistration sr = icasa.registerService(deviceImpl, ProvidedDevice.class);
        
        List<KnownDevice> devices = getGlobalDeviceManagerService().getKnownDevices();
        assertNotNull(devices);
        assertEquals(1, devices.size());
        final KnownDevice knownDevice = devices.get(0);
		assertEquals(devId, knownDevice.getId());
        
        AvailableDevice availDev = knownDevice.getAvailableDevice();
        assertNotNull(availDev);
        assertEquals(devId, availDev.getId());
        
        KnownDevice navigatedKnownDev = availDev.getKnownDevice();
        assertNotNull(navigatedKnownDev);
        assertEquals(devId, navigatedKnownDev.getId());

        //cleanup
        sr.unregister();
    }
    
    @Test
    public void testAvailableKnownAndApplicationAvailability() {
    	//wait for the service to be available.
        waitForIt(100);
        
        ProvidedDevice deviceImpl = mock(ProvidedDevice.class);
        final String devId = "123f3";
		when(deviceImpl.getId()).thenReturn(devId);
		
		BundleContext app1Context = getBundleContext(APP1_ID);
		
		final DevServListener devServListener = new DevServListener();
		app1Context.addServiceListener(devServListener);
		
        ServiceRegistration sr = icasa.registerService(deviceImpl, ProvidedDevice.class);
        
        KnownDevice app1KnownDevice = (KnownDevice) getServiceObject(KnownDevice.class, app1Context);
        assertNull(app1KnownDevice);
        
        ApplicationDevice app1AppDevice = (ApplicationDevice) getServiceObject(ApplicationDevice.class, app1Context);
        assertNull(app1AppDevice);
        
        AvailableDevice app1Device = (AvailableDevice) getServiceObject(AvailableDevice.class, app1Context);
        assertNull(app1Device);
        
        //check service events
        waitForIt(100);
        
        assertEquals(true, devServListener.hasNoEvent());
        
        //cleanup
        sr.unregister();
    }
    
    @Test
    public void testDeviceAvailability() {
    	//wait for the service to be available.
        waitForIt(100);
        
        ProvidedDevice deviceImpl = mock(ProvidedDevice.class);
        final String devId = "123f4";
		when(deviceImpl.getId()).thenReturn(devId);
		ServiceRegistration sr = icasa.registerService(deviceImpl, ProvidedDevice.class);
        
        BundleContext app1Context = getBundleContext(APP1_ID);
        ApplicationDevice app1Device = (ApplicationDevice) getServiceObject(ApplicationDevice.class, app1Context);
        assertNull(app1Device);
        
        DeviceManager deviceMgr = (DeviceManager) getServiceObject(DeviceManager.class, app1Context);
        DeviceDependencies dependencies = new DeviceDependencies();
        dependencies.includes().all();
        
        deviceMgr.addDependencies(dependencies);
        
        app1Device = (ApplicationDevice) getServiceObject(ApplicationDevice.class, app1Context);
        assertNotNull(app1Device);
		assertEquals(devId, app1Device.getId());
		assertFalse("devices from applications must not implement KnwonDevice interface.", app1Device instanceof KnownDevice);
		assertFalse("devices from applications must not implement AvailableDevice interface.", app1Device instanceof AvailableDevice);
		assertFalse("devices from applications must not implement ProvidedDevice interface.", app1Device instanceof ProvidedDevice);
		assertTrue("devices from applications must be protected.", app1Device.isProtected());
		
        //cleanup
        sr.unregister();
        
        waitForIt(100);
        
        app1Device = (ApplicationDevice) getServiceObject(ApplicationDevice.class, app1Context);
        assertNull(app1Device);
    }
    
    @Test
    public void testDeviceAttr() {
    	//wait for the service to be available.
        waitForIt(100);
        
        final String devId = "123f5";
        String devName = "deviceName0";
        String vendor = "vendor0";
        String devType = "devType0";
        ProvidedDeviceMock deviceImpl = new ProvidedDeviceMock(devId, devName, vendor, devType);
        
        StateVariable var1 = new StateVariableImpl("var1", Boolean.FALSE, Boolean.class, VariableType.ID, "var1 Desc", false, false, deviceImpl);
        deviceImpl.addVariable(var1);
        StateVariable var2 = new StateVariableImpl("var2", "init", String.class, VariableType.HUMAN_READABLE_DESCRIPTION, "var2 Desc", true, false, deviceImpl);
        deviceImpl.addVariable(var2);
        StateVariable var3 = new StateVariableImpl("var3", 1, Integer.class, VariableType.FORMAL_DESCRIPTION, "var3 Desc", true, true, deviceImpl);
        deviceImpl.addVariable(var3);
		
        ServiceRegistration sr = icasa.registerService(deviceImpl, ProvidedDevice.class);
        
        BundleContext app1Context = getBundleContext(APP1_ID);
        DeviceManager deviceMgr = (DeviceManager) getServiceObject(DeviceManager.class, app1Context);
        DeviceDependencies dependencies = new DeviceDependencies();
        dependencies.includes().all();
        
        deviceMgr.addDependencies(dependencies);
        
        // predefined methods
        Device app1device = (Device) getServiceObject(ApplicationDevice.class, app1Context);
        assertNotNull(app1device);
		assertEquals(devId, app1device.getId());
		assertEquals(devName, app1device.getName());
		assertEquals(vendor, app1device.getVendor());
		assertEquals(devType, app1device.getTypeId());

		// predefined attributes
		StateVariable idAttr = getAttrByValue(devId, app1device);
		StateVariable nameAttr = getAttrByValue(devName, app1device);
		StateVariable vendorAttr = getAttrByValue(vendor, app1device);
		StateVariable typeAttr = getAttrByValue(devType, app1device);

		assertNotNull(idAttr);
		assertEquals(String.class, idAttr.getValueType());
		
		assertNotNull(nameAttr);
		assertEquals(String.class, nameAttr.getValueType());
		
		assertNotNull(vendorAttr);
		assertEquals(String.class, vendorAttr.getValueType());
		
		assertNotNull(typeAttr);
		assertEquals(String.class, typeAttr.getValueType());
		
		// static device attributes
		StateVariable var1Found = getAttrByName(var1.getName(), app1device);
		checkEqualVar(var1Found, var1);
		StateVariable var2Found = getAttrByName(var2.getName(), app1device);
		checkEqualVar(var2Found, var2);
		StateVariable var3Found = getAttrByName(var3.getName(), app1device);
		checkEqualVar(var3Found, var3);
		
		//dynamic attributes
		StateVariable var4 = new StateVariableImpl("var4", 1, Long.class, VariableType.FORMAL_DESCRIPTION, "var4 Desc", false, true, deviceImpl);
        deviceImpl.addVariable(var4);
        
        StateVariable var4Found = getAttrByName(var4.getName(), app1device);
        checkEqualVar(var4Found, var4);
		
        //cleanup
        sr.unregister();
    }
    
    @Test
    public void testServiceAttr() {
    	//wait for the service to be available.
        waitForIt(100);
        
        final String devId = "123f6";
        String devName = "deviceName0";
        String vendor = "vendor0";
        String devType = "devType0";
        ProvidedDeviceMock deviceImpl = new ProvidedDeviceMock(devId, devName, vendor, devType);
        
        //TODO put attributes on services
        StateVariable var1 = new StateVariableImpl("var1", Boolean.FALSE, Boolean.class, VariableType.ID, "var1 Desc", false, false, deviceImpl);
        deviceImpl.addVariable(var1);
        StateVariable var2 = new StateVariableImpl("var2", "init", String.class, VariableType.HUMAN_READABLE_DESCRIPTION, "var2 Desc", true, false, deviceImpl);
        deviceImpl.addVariable(var2);
        StateVariable var3 = new StateVariableImpl("var3", 1, Integer.class, VariableType.FORMAL_DESCRIPTION, "var3 Desc", true, true, deviceImpl);
        deviceImpl.addVariable(var3);
		
        ServiceRegistration sr = icasa.registerService(deviceImpl, ProvidedDevice.class);
        
        BundleContext app1Context = getBundleContext(APP1_ID);
        DeviceManager deviceMgr = (DeviceManager) getServiceObject(DeviceManager.class, app1Context);
        DeviceDependencies dependencies = new DeviceDependencies();
        dependencies.includes().all();
        
        deviceMgr.addDependencies(dependencies);
        
        // predefined methods
        Device app1device = (Device) getServiceObject(ApplicationDevice.class, app1Context);
        assertNotNull(app1device);
		assertEquals(devId, app1device.getId());
		assertEquals(devName, app1device.getName());
		assertEquals(vendor, app1device.getVendor());
		assertEquals(devType, app1device.getTypeId());

		// predefined attributes
		StateVariable idAttr = getAttrByValue(devId, app1device);
		StateVariable nameAttr = getAttrByValue(devName, app1device);
		StateVariable vendorAttr = getAttrByValue(vendor, app1device);
		StateVariable typeAttr = getAttrByValue(devType, app1device);

		assertNotNull(idAttr);
		assertEquals(String.class, idAttr.getValueType());
		
		assertNotNull(nameAttr);
		assertEquals(String.class, nameAttr.getValueType());
		
		assertNotNull(vendorAttr);
		assertEquals(String.class, vendorAttr.getValueType());
		
		assertNotNull(typeAttr);
		assertEquals(String.class, typeAttr.getValueType());
		
		// static device attributes
		StateVariable var1Found = getAttrByName(var1.getName(), app1device);
		checkEqualVar(var1Found, var1);
		StateVariable var2Found = getAttrByName(var2.getName(), app1device);
		checkEqualVar(var2Found, var2);
		StateVariable var3Found = getAttrByName(var3.getName(), app1device);
		checkEqualVar(var3Found, var3);
		
		//dynamic attributes
		StateVariable var4 = new StateVariableImpl("var4", 1, Long.class, VariableType.FORMAL_DESCRIPTION, "var4 Desc", false, true, deviceImpl);
        deviceImpl.addVariable(var4);
        
        StateVariable var4Found = getAttrByName(var4.getName(), app1device);
        checkEqualVar(var4Found, var4);
		
        //cleanup
        sr.unregister();
    }
    
    private void checkEqualVar(StateVariable varFound, StateVariable var) {
		assertEquals(var.getName(), varFound.getName());
		assertEquals(var.getDescription(), varFound.getDescription());
		assertEquals(var.getType(), varFound.getType());
		assertEquals(var.getValue(), varFound.getValue());
		assertEquals(var.getValueType(), varFound.getValueType());
    }

	private StateVariable getAttrByValue(Object valueToFind, Device dev) {
    	for (StateVariable var : dev.getStateVariables()) {
			Object value = var.getValue();
			if (valueToFind.equals(value))
				return var;
    	}
    	
    	return null;
    }
    
    private StateVariable getAttrByName(String nameToFind, Device dev) {
    	for (StateVariable var : dev.getStateVariables()) {
			Object name = var.getName();
			if (nameToFind.equals(name))
				return var;
    	}
    	
    	return null;
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
		public boolean unprotectedAppDevEvent = false;
		public boolean protectedAppDevEvent = false;
		public boolean availableDevEvent = false;
		
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
				if (ApplicationDevice.class.getName().equals(interf)) {
					ApplicationDevice appDev = (ApplicationDevice) context.getService(sr);
					context.ungetService(sr);
					if (appDev.isProtected())
						protectedAppDevEvent = true;
					else
						unprotectedAppDevEvent = true;
				}
			}
		}
		
		public boolean hasNoEvent() {
			return !providedDevEvent && !knownDevEvent && !unprotectedAppDevEvent && !availableDevEvent && !protectedAppDevEvent; 
		}

		public boolean hasProvidedDevEvent() {
			return providedDevEvent;
		}

		public boolean hasOnlyProtectedDevEvent() {
			return !providedDevEvent && !knownDevEvent && !unprotectedAppDevEvent && !availableDevEvent && protectedAppDevEvent; 
		}
		
		public boolean hasProtectedDevEvent() {
			return protectedAppDevEvent; 
		}
		
	}
}

