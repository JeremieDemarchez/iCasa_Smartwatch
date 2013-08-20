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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.InstanceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import test.component.handler.ComponentOnlyRequireDevice;
import test.component.handler.ComponentPropertiesRequireDevice;
import test.component.handler.ComponentUsingArray;
import test.component.handler.ComponentUsingBindMethods;
import test.component.handler.ComponentUsingList;
import test.component.handler.ComponentUsingVector;
import fr.liglab.adele.icasa.access.DeviceAccessPolicy;
import fr.liglab.adele.icasa.access.MemberAccessPolicy;
import fr.liglab.adele.icasa.dependency.manager.exception.AccessViolationException;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.location.LocatedDevice;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class CoreDeviceHandlerTest extends BaseDeviceHandlerTest {

    /**
     * Test the creation of a new zone.
     * 
     * @throws IOException
     * @throws
     */
    @Test
    public void bindAndUnbindDeviceTest() throws Exception {

        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingBindMethods");

        ComponentUsingBindMethods pojo = (ComponentUsingBindMethods) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);

        // No service injected because not access given
        assertEquals(0, pojo.lights.size());

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.TOTAL);

        // Service injected when access modified
        assertEquals(1, pojo.lights.size());

        BinaryLight injectedLigth = pojo.lights.get(0);

        assertEquals("BinaryLight-001", injectedLigth.getSerialNumber());
        
        createBinaryLigth("BinaryLight-002");
        
        createBinaryLigth("BinaryLight-003");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-002", DeviceAccessPolicy.TOTAL);
        
        // Two (of three) services have been injected
        assertEquals(2, pojo.lights.size());
        
        List<String> deviceIDsList = new ArrayList<String>();
        
        for (BinaryLight light : pojo.lights) {
            deviceIDsList.add(light.getSerialNumber());
        }
        
        // Verifies if the right services have been injected
        assertThat(deviceIDsList, is(Arrays.asList("BinaryLight-001", "BinaryLight-002")));
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);
        assertEquals(1, pojo.lights.size());
        injectedLigth = pojo.lights.get(0);
        
        // Verifies if the right device is always injected
        assertEquals("BinaryLight-002", injectedLigth.getSerialNumber());
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-002", DeviceAccessPolicy.HIDDEN);
        
        // Verifies that all devices have been removed
        assertEquals(0, pojo.lights.size());
    }
    
    @Test
    public void requiresWithArrayTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingArray");

        ComponentUsingArray pojo = (ComponentUsingArray) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);
        
        // No service injected because not access given
        assertEquals(0, pojo.getLights().length);

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.TOTAL);
        
        // Service injected when access modified
        assertEquals(1, pojo.getLights().length);
        
        BinaryLight injectedLigth = pojo.getLights()[0];

        assertEquals("BinaryLight-001", injectedLigth.getSerialNumber());
        
        createBinaryLigth("BinaryLight-002");
        
        createBinaryLigth("BinaryLight-003");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-002", DeviceAccessPolicy.TOTAL);
        
        // Two (of three) services have been injected
        assertEquals(2, pojo.getLights().length);
        
        List<String> deviceIDsList = new ArrayList<String>();
        
        for (BinaryLight light : pojo.getLights()) {
            deviceIDsList.add(light.getSerialNumber());
        }
        
        // Verifies if the right services have been injected
        assertThat(deviceIDsList, is(Arrays.asList("BinaryLight-001", "BinaryLight-002")));
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);
        assertEquals(1, pojo.getLights().length);
        injectedLigth = pojo.getLights()[0];
        
        // Verifies if the right device is always injected
        assertEquals("BinaryLight-002", injectedLigth.getSerialNumber());
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-002", DeviceAccessPolicy.HIDDEN);
        
        // Verifies that all devices have been removed
        assertEquals(0, pojo.getLights().length);
        
    }
    
    

    @Test
    public void requiresWithVectorTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingVector");

        ComponentUsingVector pojo = (ComponentUsingVector) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");
                
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);
        
        // No service injected because not access given
        assertEquals(0, pojo.getLights().size());

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.TOTAL);
        
        // Service injected when access modified
        assertEquals(1, pojo.getLights().size());
        
        BinaryLight injectedLigth = (BinaryLight) pojo.getLights().get(0);

        assertEquals("BinaryLight-001", injectedLigth.getSerialNumber());
        
        createBinaryLigth("BinaryLight-002");
        
        createBinaryLigth("BinaryLight-003");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-002", DeviceAccessPolicy.TOTAL);
        
        // Two (of three) services have been injected
        assertEquals(2, pojo.getLights().size());
        
        List<String> deviceIDsList = new ArrayList<String>();
        
        for (Object obj : pojo.getLights()) {
            BinaryLight light = (BinaryLight) obj;
            deviceIDsList.add(light.getSerialNumber());
        }
        
        // Verifies if the right services have been injected
        assertThat(deviceIDsList, is(Arrays.asList("BinaryLight-001", "BinaryLight-002")));
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);
        assertEquals(1, pojo.getLights().size());
        injectedLigth = (BinaryLight) pojo.getLights().get(0);
        
        // Verifies if the right device is always injected
        assertEquals("BinaryLight-002", injectedLigth.getSerialNumber());
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-002", DeviceAccessPolicy.HIDDEN);
        
        // Verifies that all devices have been removed
        assertEquals(0, pojo.getLights().size());
        
    }

    @Test
    public void requiresWithListTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingList");

        ComponentUsingList pojo = (ComponentUsingList) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");
                
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);
        
        // No service injected because not access given
        assertEquals(0, pojo.getLights().size());

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.TOTAL);
        
        // Service injected when access modified
        assertEquals(1, pojo.getLights().size());
        
        BinaryLight injectedLigth = (BinaryLight) pojo.getLights().get(0);

        assertEquals("BinaryLight-001", injectedLigth.getSerialNumber());
        
        createBinaryLigth("BinaryLight-002");
        
        createBinaryLigth("BinaryLight-003");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-002", DeviceAccessPolicy.TOTAL);
        
        // Two (of three) services have been injected
        assertEquals(2, pojo.getLights().size());
        
        List<String> deviceIDsList = new ArrayList<String>();
        
        for (Object obj : pojo.getLights()) {
            BinaryLight light = (BinaryLight) obj;
            deviceIDsList.add(light.getSerialNumber());
        }
        
        // Verifies if the right services have been injected
        assertThat(deviceIDsList, is(Arrays.asList("BinaryLight-001", "BinaryLight-002")));
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);
        assertEquals(1, pojo.getLights().size());
        injectedLigth = (BinaryLight) pojo.getLights().get(0);
        
        // Verifies if the right device is always injected
        assertEquals("BinaryLight-002", injectedLigth.getSerialNumber());
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-002", DeviceAccessPolicy.HIDDEN);
        
        // Verifies that all devices have been removed
        assertEquals(0, pojo.getLights().size());
        
    }
    
    @Test
    public void injectingFieldTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentOnlyRequireDevice");

        ComponentOnlyRequireDevice pojo = (ComponentOnlyRequireDevice) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");
        
        try {
            pojo.getLight().getSerialNumber();
            fail(); // The exception has to be thrown
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());           
        }
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.TOTAL);
        
        assertEquals("BinaryLight-001", pojo.getLight().getSerialNumber());
                
        createThermometer("Thermometer-001");
        
        try {
            pojo.getThermometer().getSerialNumber();
            fail(); // The exception has to be thrown
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());           
        }

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "Thermometer-001", DeviceAccessPolicy.TOTAL);

        assertEquals("Thermometer-001", pojo.getThermometer().getSerialNumber());
        
        assertEquals(ComponentInstance.VALID, manager.getState());

        assertNotNull(pojo.getLight());

    }

    
    @Test
    public void accesingMethodsTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentOnlyRequireDevice");

        ComponentOnlyRequireDevice pojo = (ComponentOnlyRequireDevice) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");
        createThermometer("Thermometer-001");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.PARTIAL);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "Thermometer-001", DeviceAccessPolicy.PARTIAL);
        
        assertEquals(ComponentInstance.VALID, manager.getState());
        

        
        try {
            pojo.getLight().getPowerStatus();
            fail(); // The exception has to be thrown
        } catch (AccessViolationException e) {
            System.out.println(e.getMessage());           
        }
        
        try {
            pojo.getThermometer().getTemperature();
            fail(); // The exception has to be thrown
        } catch (AccessViolationException e) {
            System.out.println(e.getMessage());           
        }
        
        accessManager.setMethodAccess(TEST_APPLICATION_NAME, "BinaryLight-001", "getPowerStatus", MemberAccessPolicy.READ_WRITE);
        accessManager.setMethodAccess(TEST_APPLICATION_NAME, "Thermometer-001", "getTemperature", MemberAccessPolicy.READ_WRITE);
        
        try {
            pojo.getLight().getPowerStatus();
        } catch (AccessViolationException e) {
            System.out.println(e.getMessage());      
            fail(); // The exception has not to be thrown            
        }
        
        try {
            pojo.getThermometer().getTemperature();
        } catch (AccessViolationException e) {
            System.out.println(e.getMessage());    
            fail(); // The exception has not to be thrown            
        }        
        
    }
    
    @Override
    protected Boolean getAccessPolicyPropertyValue() {
        return Boolean.FALSE;
    }

}
