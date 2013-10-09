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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.InstanceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import test.component.handler.ComponentPropertiesRequireDevice;
import test.component.handler.ComponentUsingBindProperties;
import fr.liglab.adele.icasa.access.DeviceAccessPolicy;
import fr.liglab.adele.icasa.device.GenericDevice;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class PropertiesDeviceHandlerTest extends BaseDeviceHandlerTest {

    @Test
    public void mandatoryPropertiesTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentPropertiesRequireDevice");

        ComponentPropertiesRequireDevice pojo = (ComponentPropertiesRequireDevice) manager.getPojoObject();

        GenericDevice lightDevice = createBinaryLigth("BinaryLight-501");
        GenericDevice thermometerDevice = createThermometer("Thermometer-501");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-501", DeviceAccessPolicy.TOTAL);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "Thermometer-501", DeviceAccessPolicy.TOTAL);
        
        assertEquals(ComponentInstance.INVALID, manager.getState());
        
        
        try {
            pojo.getLight().getPowerStatus();
            fail(); // The exception has to be thrown
        } catch (Exception e) {
            System.out.println(e.getMessage());           
        }
        
        try {
            pojo.getThermometer().getTemperature();
            fail(); // The exception has to be thrown
        } catch (Exception e) {
            System.out.println(e.getMessage());           
        }
        
              

        lightDevice.setPropertyValue("test-prop1", "test-value");
        lightDevice.setPropertyValue("test-prop2", "test-value");
        
        thermometerDevice.setPropertyValue("test-prop3", "test-value");
                
        assertEquals(ComponentInstance.VALID, manager.getState());
        
        try {
            pojo.getLight().getPowerStatus();
        } catch (Exception e) {
            System.out.println(e.getMessage());      
            fail(); // The exception has not to be thrown            
        }
        
        try {
            pojo.getThermometer().getTemperature();
        } catch (Exception e) {
            System.out.println(e.getMessage());    
            fail(); // The exception has not to be thrown            
        }       
        
        lightDevice.removeProperty("test-prop1");
        
        assertEquals(ComponentInstance.INVALID, manager.getState());
        
        try {
            pojo.getLight().getPowerStatus();
            fail(); // The exception has to be thrown
        } catch (Exception e) {
            System.out.println(e.getMessage());           
        }
        
        
    }
    
    @Test
    public void mandatoryPropertiesFirstTest() throws Exception {
        GenericDevice lightDevice = createBinaryLigth("BinaryLight-501");
        GenericDevice thermometerDevice = createThermometer("Thermometer-501");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-501", DeviceAccessPolicy.TOTAL);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "Thermometer-501", DeviceAccessPolicy.TOTAL);
        
        
        lightDevice.setPropertyValue("test-prop1", "test-value");
        lightDevice.setPropertyValue("test-prop2", "test-value");
        
        thermometerDevice.setPropertyValue("test-prop3", "test-value");
        
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentPropertiesRequireDevice");

        ComponentPropertiesRequireDevice pojo = (ComponentPropertiesRequireDevice) manager.getPojoObject();
                
        assertEquals(ComponentInstance.VALID, manager.getState());
        
        try {
            pojo.getLight().getPowerStatus();
        } catch (Exception e) {
            System.out.println(e.getMessage());      
            fail(); // The exception has not to be thrown            
        }
        
        try {
            pojo.getThermometer().getTemperature();
        } catch (Exception e) {
            System.out.println(e.getMessage());    
            fail(); // The exception has not to be thrown            
        }  
                  
    }
        
    
    @Test
    public void mandatoryPropertiesWithBindCallback() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentUsingBindProperties");

        ComponentUsingBindProperties pojo = (ComponentUsingBindProperties) manager.getPojoObject();

        GenericDevice lightDevice = createBinaryLigth("BinaryLight-501");
        
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-501", DeviceAccessPolicy.TOTAL);
        
        // Service injected when access modified
        assertEquals(0, pojo.lights.size());
        
        lightDevice.setPropertyValue("test-prop1", "test-value");
        lightDevice.setPropertyValue("test-prop2", "test-value");
        
        assertEquals(1, pojo.lights.size());
        
        lightDevice.removeProperty("test-prop1");
        lightDevice.removeProperty("test-prop2");
        
        assertEquals(0, pojo.lights.size()); 
        
        
    }

}
