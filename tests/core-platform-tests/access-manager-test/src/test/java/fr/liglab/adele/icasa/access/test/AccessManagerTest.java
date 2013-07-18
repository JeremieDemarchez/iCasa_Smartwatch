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
import fr.liglab.adele.icasa.access.AccessManager;
import fr.liglab.adele.icasa.access.AccessRight;
import fr.liglab.adele.icasa.access.AccessRightListener;
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
        Assert.assertFalse(right.hasAccess());
        service.updateAccess(applicationID, deviceID, true);//It change right access.
        Assert.assertTrue(right.hasAccess());
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
        Assert.assertFalse(right.hasAccess());
        service.updateAccess(applicationID, deviceID, true);//It change right access.
        service.updateAccess(applicationID,deviceID,methodName,true);
        Assert.assertTrue(right.hasAccess());//access to device
        Assert.assertTrue(right.hasAccess(methodName));//access to method in device
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
        Assert.assertFalse(right.hasAccess());
        //give access to the method.
        service.updateAccess(applicationID,deviceID,methodName,true);

        Assert.assertFalse(right.hasAccess());
        Assert.assertFalse(right.hasAccess(methodName));//It must be false since there is any access to device
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
        service.updateAccess(applicationID, deviceID, true);//must call the listeners
        verify(mockListener, atLeast(1)).onAccessRightModified(right);
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
        service.updateAccess(applicationID, deviceID, true);//must call the listeners
        service.updateAccess(applicationID, deviceID, methodName, true);//must call the listeners
        verify(mockListener, atLeast(1)).onAccessRightModified(right);
        verify(mockListener, atLeast(1)).onMethodAccessRightModified(right, methodName);
    }
}
