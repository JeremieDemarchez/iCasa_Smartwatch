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
package fr.liglab.adele.icasa.access.test;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.access.*;
import fr.liglab.adele.icasa.clock.Clock;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import static org.mockito.Mockito.*;



@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class AccessManagerTest extends AbstractDistributionBaseTest {


	@Inject
	public BundleContext context;

    @Inject
    public Clock clock;
	
	@Before
	public void setUp() {
		waitForStability(context);
	}

	@After
	public void tearDown() {

	}

	/**
	 * Test the service availability
	 */
	@Test
	public void testServiceAvailability(){
        AccessManager service = (AccessManager) this.getService(context, AccessManager.class);
        Assert.assertNotNull(service);
    }

    /**
     * Test the access right assignment
     */
    @Test
    public void testAccessRightAssignment(){
        AccessManager service = (AccessManager) this.getService(context, AccessManager.class);
        String applicationID = "App1";
        String deviceID = "device1";
        Assert.assertNotNull(service);
        AccessRight right = service.getAccessRight(applicationID, deviceID);
        Assert.assertNotNull(right);
        Assert.assertFalse(right.isVisible());
        service.setDeviceAccess(applicationID, deviceID, DeviceAccessPolicy.TOTAL);//It change right access.
        Assert.assertTrue(right.isVisible());
    }

    /**
     * Test the access right assignment
     */
    @Test
    public void testPartialAccessRightAssignment(){
        AccessManager service = (AccessManager) this.getService(context, AccessManager.class);
        String applicationID = "App1";
        String deviceID = "device1";
        Assert.assertNotNull(service);
        AccessRight right = service.getAccessRight(applicationID, deviceID);
        Assert.assertNotNull(right);
        Assert.assertFalse(right.isVisible());
        service.setDeviceAccess(applicationID, deviceID, DeviceAccessPolicy.PARTIAL);//It change right access.
        Assert.assertTrue(right.isVisible());
        //This should be ok since is in the valid method list for visible.
        Assert.assertTrue(right.hasMethodAccess("getSerialNumber"));
        //It does not have right.
        Assert.assertFalse(right.hasMethodAccess("pingPong"));
        service.setMethodAccess(applicationID, deviceID, "pingPong", MemberAccessPolicy.READ_ONLY);
        Assert.assertTrue(right.hasMethodAccess("pingPong"));
    }

    /**
     * Test the access right assignment
     */
    @Test
    public void testVisibleAccessRightAssignment(){
        AccessManager service = (AccessManager) this.getService(context, AccessManager.class);
        String applicationID = "App1";
        String deviceID = "device1";
        Assert.assertNotNull(service);
        AccessRight right = service.getAccessRight(applicationID, deviceID);
        Assert.assertNotNull(right);
        Assert.assertFalse(right.isVisible());
        service.setDeviceAccess(applicationID, deviceID, DeviceAccessPolicy.VISIBLE);//It change right access.
        Assert.assertTrue(right.isVisible());
        service.setMethodAccess(applicationID, deviceID, "pingPong", MemberAccessPolicy.READ_ONLY);
        service.setMethodAccess(applicationID, deviceID, "pingPong2", MemberAccessPolicy.READ_ONLY);

        //This should be ok since is in the valid method list for visible.
        Assert.assertTrue(right.hasMethodAccess("getSerialNumber"));
        //It does not have right since the device access is only visible.
        Assert.assertFalse(right.hasMethodAccess("pingPong"));
        Assert.assertFalse(right.hasMethodAccess("pingPong2"));
        Assert.assertFalse(right.hasMethodAccess("pingPong3"));

        //Change access right to partial,
        service.setDeviceAccess(applicationID, deviceID, DeviceAccessPolicy.PARTIAL);//It change right access.
        Assert.assertTrue(right.hasMethodAccess("pingPong"));
        Assert.assertTrue(right.hasMethodAccess("pingPong2"));
        Assert.assertFalse(right.hasMethodAccess("pingPong3"));
    }

    /**
     * Test the method access right assignment
     */
    @Test
    public void testMethodAccessRightAssignment(){
        AccessManager service = (AccessManager) this.getService(context, AccessManager.class);
        String applicationID = "App1";
        String deviceID = "device1";
        String methodName = "getLight";
        Assert.assertNotNull(service);
        AccessRight right = service.getAccessRight(applicationID, deviceID);
        Assert.assertNotNull(right);
        Assert.assertFalse(right.isVisible());
        service.setDeviceAccess(applicationID, deviceID, DeviceAccessPolicy.TOTAL);//It change right access.
        service.setMethodAccess(applicationID, deviceID, methodName, MemberAccessPolicy.READ_WRITE);
        Assert.assertTrue(right.isVisible());//access to device
        Assert.assertTrue(right.hasMethodAccess(methodName));//access to method in device
    }

    /**
     * Test the method access right assignment
     */
    @Test
    public void testMethodAccessRight(){
        AccessManager service = (AccessManager) this.getService(context, AccessManager.class);
        String applicationID = "App1";
        String deviceID = "device1";
        String methodName = "getLight";
        Assert.assertNotNull(service);
        AccessRight right = service.getAccessRight(applicationID, deviceID);
        Assert.assertNotNull(right);
        Assert.assertFalse(right.isVisible());
        //give access to the method.
        service.setMethodAccess(applicationID, deviceID, methodName, MemberAccessPolicy.READ_WRITE);

        Assert.assertFalse(right.isVisible());
        Assert.assertFalse(right.hasMethodAccess(methodName));//It must be false since there is any access to device
    }

    /**
     * Test the callback listeners.
     */
    @Test
    public void testListenerChangedRight(){
        AccessManager service = (AccessManager) this.getService(context, AccessManager.class);
        String applicationID = "App1";
        String deviceID = "device1";
        String methodName = "getLight";
        Assert.assertNotNull(service);
        AccessRight right = service.getAccessRight(applicationID, deviceID);
        AccessRightListener mockListener = mock(AccessRightListener.class);
        right.addListener(mockListener);
        service.setDeviceAccess(applicationID, deviceID, DeviceAccessPolicy.TOTAL);//must call the listeners
        verify(mockListener, times(1)).onAccessRightModified(right);
        verify(mockListener, never()).onMethodAccessRightModified(any(AccessRight.class), any(String.class));
    }

    /**
     * Test the callback listener
     */
    @Test
    public void testListenerChangedMethodRight(){
        AccessManager service = (AccessManager) this.getService(context, AccessManager.class);
        String applicationID = "App1";
        String deviceID = "device1";
        String methodName = "getLight";
        Assert.assertNotNull(service);
        AccessRight right = service.getAccessRight(applicationID, deviceID);
        AccessRightListener mockListener = mock(AccessRightListener.class);
        right.addListener(mockListener);
        service.setDeviceAccess(applicationID, deviceID, DeviceAccessPolicy.TOTAL);//must call the listeners
        service.setMethodAccess(applicationID, deviceID, methodName, MemberAccessPolicy.READ_WRITE);//must call the listeners
        verify(mockListener, times(1)).onAccessRightModified(right);
        verify(mockListener, times(1)).onMethodAccessRightModified(right, methodName);
    }
}
