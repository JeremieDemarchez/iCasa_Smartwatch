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

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.handlers.dependency.DependencyDescription;
import org.apache.felix.ipojo.handlers.dependency.DependencyHandlerDescription;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

import test.component.handler.ComponentOnlyRequireDevice;
import test.component.handler.ComponentUsingBindMethods;
import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.access.DeviceAccessPolicy;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.handler.test.mock.devices.BinaryLightMockImpl;
import fr.liglab.adele.icasa.device.handler.test.mock.devices.ThermometerMockImpl;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.temperature.Thermometer;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class DeviceHandlerTest extends AbstractDistributionBaseTest {

    @Inject
    public BundleContext context;

    private DeploymentAdmin dpAdmin;

    private ContextManager contextManager;

    private AccessManager accessManager;

    private final String TEST_APPLICATION_NAME = "test-handler-app";

    @Before
    public void setUp() throws Exception {
        waitForStability(context);
        dpAdmin = (DeploymentAdmin) getService(context, DeploymentAdmin.class);
        contextManager = (ContextManager) getService(context, ContextManager.class);
        accessManager = (AccessManager) getService(context, AccessManager.class);
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
    // @Test
    public void bindAndUnbindDeviceTest() throws Exception {

        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingBindMethods");

        ComponentUsingBindMethods pojo = (ComponentUsingBindMethods) manager.getPojoObject();

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);

        createBinaryLigth("BinaryLight-001");

        assertEquals(0, pojo.lights.size());

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.TOTAL);

        assertEquals(1, pojo.lights.size());

        BinaryLight injectedLigth = pojo.lights.get(0);

        assertEquals("BinaryLight-001", injectedLigth.getSerialNumber());

        accessManager.setDeviceAccess("test-handler-app", "BinaryLight-001", DeviceAccessPolicy.HIDDEN);

        assertEquals(0, pojo.lights.size());
    }

    @Test
    public void injectingFieldTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentOnlyRequireDevice");

        ComponentOnlyRequireDevice pojo = (ComponentOnlyRequireDevice) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");

        
        try {
            pojo.getLight().getSerialNumber();
            fail();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            assertEquals("No device injected yet", e.getMessage());            
        }
        

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.TOTAL);

        createThermometer("Thermometer-001");

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "Thermometer-001", DeviceAccessPolicy.TOTAL);

        assertEquals(ComponentInstance.VALID, manager.getState());

        assertNotNull(pojo.getLight());

    }

    public ComponentInstance createComponentInstance(String factoryName) throws Exception {
        Factory factory = (Factory) getService(context, Factory.class, "(factory.name=" + factoryName + ")");
        assertNotNull(factory);

        ComponentInstance instance = factory.createComponentInstance(null);
        assertNotNull(instance);

        return instance;
    }

    private void createBinaryLigth(String deviceId) {
        BinaryLightMockImpl mockImpl = new BinaryLightMockImpl(deviceId);

        Dictionary properties = new Hashtable();
        properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);

        context.registerService(new String[] { GenericDevice.class.getName(), BinaryLight.class.getName() }, mockImpl,
                properties);
    }

    private void createThermometer(String deviceId) {
        ThermometerMockImpl mockImpl = new ThermometerMockImpl(deviceId);

        Dictionary properties = new Hashtable();
        properties.put(GenericDevice.DEVICE_SERIAL_NUMBER, deviceId);

        context.registerService(new String[] { GenericDevice.class.getName(), Thermometer.class.getName() }, mockImpl,
                properties);
    }

    public void testSimpleHandlerOld() throws Exception {
        ContextManager ctxManager = (ContextManager) getService(context, ContextManager.class);
        assertNotNull(ctxManager);

        AccessManager accessManager = (AccessManager) getService(context, AccessManager.class);
        assertNotNull(accessManager);

        Factory factory = (Factory) getService(context, Factory.class, "(factory.name=ComponentUsingBindMethods)");
        assertNotNull(factory);

        ComponentInstance instance = factory.createComponentInstance(null);
        assertNotNull(instance);

        InstanceManager manager = (InstanceManager) instance;

        ComponentUsingBindMethods pojo = (ComponentUsingBindMethods) manager.getPojoObject();

        accessManager.setDeviceAccess("test-handler-app", "BinaryLight-001", DeviceAccessPolicy.HIDDEN);

        createBinaryLigth("BinaryLight-001");

        Assert.assertEquals(0, pojo.lights.size());

        accessManager.setDeviceAccess("test-handler-app", "BinaryLight-001", DeviceAccessPolicy.TOTAL);

        Assert.assertEquals(1, pojo.lights.size());

        BinaryLight injectedLigth = pojo.lights.get(0);

        Assert.assertEquals("BinaryLight-001", injectedLigth.getSerialNumber());

        accessManager.setDeviceAccess("test-handler-app", "BinaryLight-001", DeviceAccessPolicy.HIDDEN);

        Assert.assertEquals(0, pojo.lights.size());

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

    private void printBundles(String header) {
        Bundle[] bundles = context.getBundles();

        System.out.println("=================== " + header + " =================");
        System.out.println("Total " + bundles.length);
        for (Bundle bundle : bundles) {
            System.out.println(" ---> " + bundle.getSymbolicName());
        }

    }

}
