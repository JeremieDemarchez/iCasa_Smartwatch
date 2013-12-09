/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.device.handler.test;

//import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import fr.liglab.adele.icasa.exception.AccessViolationException;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.InstanceManager;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.ow2.chameleon.runner.test.ChameleonRunner;
import test.component.handler.ComponentOnlyRequireDevice;
import test.component.handler.ComponentUsingArray;
import test.component.handler.ComponentUsingBindMethods;
import test.component.handler.ComponentUsingList;
import test.component.handler.ComponentUsingVector;
import fr.liglab.adele.icasa.access.DeviceAccessPolicy;
import fr.liglab.adele.icasa.access.MemberAccessPolicy;

import fr.liglab.adele.icasa.device.light.BinaryLight;

@RunWith(ChameleonRunner.class)
public class CoreDeviceHandlerTest extends BaseDeviceHandlerTest {

    /**
     * Test the creation of a new zone.
     * 
     * @throws Exception
     * @throws
     */
    @Test
    public void bindAndUnbindDeviceTest() throws Exception {

        String bl1 = "BinaryLight-001" + UUID.randomUUID();
        String bl2 = "BinaryLight-002" + UUID.randomUUID();
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingBindMethods");

        ComponentUsingBindMethods pojo = (ComponentUsingBindMethods) manager.getPojoObject();

        createBinaryLigth(bl1);
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);

        // No service injected because not access given
        assertEquals(0, pojo.lights.size());

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.TOTAL);

        // Service injected when access modified
        assertEquals(1, pojo.lights.size());

        BinaryLight injectedLigth = pojo.lights.get(0);

        assertEquals(bl1, injectedLigth.getSerialNumber());
        
        createBinaryLigth(bl2);
        
        createBinaryLigth("BinaryLight-003");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.TOTAL);
        
        // Two (of three) services have been injected
        assertEquals(2, pojo.lights.size());
        
        List<String> deviceIDsList = new ArrayList<String>();
        
        for (BinaryLight light : pojo.lights) {
            deviceIDsList.add(light.getSerialNumber());
        }
        
        // Verifies if the right services have been injected
        //assertThat(deviceIDsList, is(Arrays.asList(bl1, bl2)));
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        assertEquals(1, pojo.lights.size());
        injectedLigth = pojo.lights.get(0);
        
        // Verifies if the right device is always injected
        assertEquals(bl2, injectedLigth.getSerialNumber());
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.HIDDEN);
        
        // Verifies that all devices have been removed
        assertEquals(0, pojo.lights.size());
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.HIDDEN);
    }
    
    @Test
    public void requiresWithArrayTest() throws Exception {
        String bl1 = "BinaryLight-001" + UUID.randomUUID();
        String bl2 = "BinaryLight-002" + UUID.randomUUID();
        String bl3 = "BinaryLight-003" + UUID.randomUUID();
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingArray");

        ComponentUsingArray pojo = (ComponentUsingArray) manager.getPojoObject();

        createBinaryLigth(bl1);
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        
        // No service injected because not access given
        assertEquals(0, pojo.getLights().length);

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.TOTAL);
        
        // Service injected when access modified
        assertEquals(1, pojo.getLights().length);
        
        BinaryLight injectedLigth = pojo.getLights()[0];

        assertEquals(bl1, injectedLigth.getSerialNumber());
        
        createBinaryLigth(bl1);
        
        createBinaryLigth(bl3);
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.TOTAL);
        
        // Two (of three) services have been injected
        assertEquals(2, pojo.getLights().length);
        

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.HIDDEN);
        
        // Verifies that all devices have been removed
        assertEquals(0, pojo.getLights().length);

        
    }
    
    

    @Test
    public void requiresWithVectorTest() throws Exception {
        String bl1 = "BinaryLight-001" + UUID.randomUUID();
        String bl2 = "BinaryLight-002" + UUID.randomUUID();
        String bl3 = "BinaryLight-003" + UUID.randomUUID();
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingVector");

        ComponentUsingVector pojo = (ComponentUsingVector) manager.getPojoObject();

        createBinaryLigth(bl1);
                
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        
        // No service injected because not access given
        assertEquals(0, pojo.getLights().size());

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.TOTAL);
        
        // Service injected when access modified
        assertEquals(1, pojo.getLights().size());
        
        BinaryLight injectedLigth = (BinaryLight) pojo.getLights().get(0);

        assertEquals(bl1, injectedLigth.getSerialNumber());
        
        createBinaryLigth(bl2);
        
        createBinaryLigth(bl3);
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.TOTAL);
        
        // Two (of three) services have been injected
        assertEquals(2, pojo.getLights().size());
        
        // Verifies if the right services have been injected
        //assertThat(deviceIDsList, is(Arrays.asList(bl1, bl2)));
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        assertEquals(1, pojo.getLights().size());
        injectedLigth = (BinaryLight) pojo.getLights().get(0);
        
        // Verifies if the right device is always injected
        assertEquals(bl2, injectedLigth.getSerialNumber());
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.HIDDEN);
        
        // Verifies that all devices have been removed
        assertEquals(0, pojo.getLights().size());
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.HIDDEN);
        
    }

    @Test
    public void requiresWithListTest() throws Exception {
        String bl1 = "BinaryLight-001" + UUID.randomUUID();
        String bl2 = "BinaryLight-002" + UUID.randomUUID();
        String bl3 = "BinaryLight-003" + UUID.randomUUID();
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingList");

        ComponentUsingList pojo = (ComponentUsingList) manager.getPojoObject();

        createBinaryLigth(bl1);
                
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        
        // No service injected because not access given
        assertEquals(0, pojo.getLights().size());

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.TOTAL);
        
        // Service injected when access modified
        assertEquals(1, pojo.getLights().size());
        
        BinaryLight injectedLigth = (BinaryLight) pojo.getLights().get(0);

        assertEquals(bl1, injectedLigth.getSerialNumber());
        
        createBinaryLigth(bl2);
        
        createBinaryLigth(bl3);
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.TOTAL);
        
        // Two (of three) services have been injected
        assertEquals(2, pojo.getLights().size());
        
        List<String> deviceIDsList = new ArrayList<String>();
        
        for (Object obj : pojo.getLights()) {
            BinaryLight light = (BinaryLight) obj;
            deviceIDsList.add(light.getSerialNumber());
        }
        
        // Verifies if the right services have been injected
        //assertThat(deviceIDsList, is(Arrays.asList(bl1, bl2)));
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        assertEquals(1, pojo.getLights().size());
        injectedLigth = (BinaryLight) pojo.getLights().get(0);
        
        // Verifies if the right device is always injected
        assertEquals(bl2, injectedLigth.getSerialNumber());
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.HIDDEN);
        
        // Verifies that all devices have been removed
        assertEquals(0, pojo.getLights().size());

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl2, DeviceAccessPolicy.HIDDEN);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl3, DeviceAccessPolicy.HIDDEN);
    }
    
    @Test
    public void injectingFieldTest() throws Exception {
        String bl1 = "BinaryLight-001" + UUID.randomUUID();
        String t1 = "Thermometer-001" + UUID.randomUUID();
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentOnlyRequireDevice");

        ComponentOnlyRequireDevice pojo = (ComponentOnlyRequireDevice) manager.getPojoObject();

        createBinaryLigth(bl1);
        
        try {
            pojo.getLight().getSerialNumber();
            fail(); // The exception has to be thrown
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());           
        }
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.TOTAL);
        
        assertEquals(bl1, pojo.getLight().getSerialNumber());
                
        createThermometer(t1);
        
        try {
            pojo.getThermometer().getSerialNumber();
            fail(); // The exception has to be thrown
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());           
        }

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, t1, DeviceAccessPolicy.TOTAL);

        assertEquals(t1, pojo.getThermometer().getSerialNumber());
        
        assertEquals(ComponentInstance.VALID, manager.getState());

        assertNotNull(pojo.getLight());
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, t1, DeviceAccessPolicy.HIDDEN);

    }

    
    @Test
    public void accesingMethodsTest() throws Exception {
        String bl1 = "BinaryLight-001" + UUID.randomUUID();
        String t1 = "Thermometer-001" + UUID.randomUUID();
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentOnlyRequireDevice");

        ComponentOnlyRequireDevice pojo = (ComponentOnlyRequireDevice) manager.getPojoObject();

        createBinaryLigth(bl1);
        createThermometer(t1);
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.PARTIAL);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, t1, DeviceAccessPolicy.PARTIAL);
        
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
        
        accessManager.setMethodAccess(TEST_APPLICATION_NAME, bl1, "getPowerStatus", MemberAccessPolicy.READ_WRITE);
        accessManager.setMethodAccess(TEST_APPLICATION_NAME, t1, "getTemperature", MemberAccessPolicy.READ_WRITE);
        
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

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, bl1, DeviceAccessPolicy.HIDDEN);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, t1, DeviceAccessPolicy.HIDDEN);

        
    }
    

}
