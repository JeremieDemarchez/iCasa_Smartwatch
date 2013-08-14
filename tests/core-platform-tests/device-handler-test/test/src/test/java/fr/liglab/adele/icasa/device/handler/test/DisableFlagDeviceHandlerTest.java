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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.util.List;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.InstanceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.access.DeviceAccessPolicy;
import fr.liglab.adele.icasa.access.MemberAccessPolicy;
import fr.liglab.adele.icasa.dependency.manager.exception.AccessViolationException;

import test.component.handler.ComponentOnlyRequireDevice;
import test.component.handler.ComponentOnlyRequirePojo;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class DisableFlagDeviceHandlerTest extends BaseDeviceHandlerTest {

       
    @Test
    public void noPlatformComponentAccesingDeviceTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentOnlyRequirePojo");

        // Component using a requires iPOJO standard
        ComponentOnlyRequirePojo pojo = (ComponentOnlyRequirePojo) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");
        
        assertEquals(ComponentInstance.VALID, manager.getState());
    }
    
    @Test
    public void accesingMethodsTest() throws Exception {
        InstanceManager manager = (InstanceManager) createComponentInstance("ComponentOnlyRequireDevice");

        ComponentOnlyRequireDevice pojo = (ComponentOnlyRequireDevice) manager.getPojoObject();

        createBinaryLigth("BinaryLight-001");
        createThermometer("Thermometer-001");
        
        assertEquals(ComponentInstance.VALID, manager.getState());


        try {
            pojo.getLight().getPowerStatus();
        } catch (AccessViolationException e) {
            fail(); // The exception has not to be thrown
            System.out.println(e.getMessage());
        }
        
        try {
            pojo.getThermometer().getTemperature();
        } catch (AccessViolationException e) {
            fail(); // The exception has not to be thrown
            System.out.println(e.getMessage());           
        }

        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "BinaryLight-001", DeviceAccessPolicy.HIDDEN);
        accessManager.setDeviceAccess(TEST_APPLICATION_NAME, "Thermometer-001", DeviceAccessPolicy.HIDDEN);

        assertEquals(ComponentInstance.VALID, manager.getState());
    }
    
    
    @Override
    protected Boolean getAccessPolicyPropertyValue() {
        return Boolean.TRUE;
    }

}
