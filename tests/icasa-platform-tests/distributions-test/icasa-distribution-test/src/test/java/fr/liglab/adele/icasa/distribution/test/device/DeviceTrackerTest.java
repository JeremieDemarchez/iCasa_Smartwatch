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
package fr.liglab.adele.icasa.distribution.test.device;

import static fr.liglab.adele.icasa.distribution.test.util.DeviceTestUtil.registerDevice;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.util.LocatedDeviceTracker;
import fr.liglab.adele.icasa.device.util.LocatedDeviceTrackerCustomizer;
import fr.liglab.adele.icasa.distribution.test.device.util.DeviceTrackedNumberCondition;
import fr.liglab.adele.icasa.distribution.test.device.util.Type1Device;
import fr.liglab.adele.icasa.distribution.test.device.util.Type1DeviceImpl;
import fr.liglab.adele.icasa.distribution.test.util.DeviceTestUtil;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import org.ow2.chameleon.runner.test.ChameleonRunner;
import org.ow2.chameleon.runner.test.utils.TestUtils;
import org.ow2.chameleon.testing.helpers.OSGiHelper;

@RunWith(ChameleonRunner.class)
public class DeviceTrackerTest {

	@Inject
	public BundleContext context;

    OSGiHelper helper;

	@Before
	public void setUp() {
		helper = new OSGiHelper(context);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testDevicesFirstThenTracker() {
		ContextManager contextMgr = helper.getServiceObject(ContextManager.class);
		Assert.assertNotNull(contextMgr);

		GenericDevice device1 = new Type1DeviceImpl("dev1");
		ServiceRegistration dev1SReg = registerDevice(context, device1, GenericDevice.class, Type1Device.class);

		GenericDevice device2 = new Type1DeviceImpl("dev2");
		ServiceRegistration dev2SReg = registerDevice(context, device2, GenericDevice.class, Type1Device.class);

		LocatedDeviceTracker tracker = new LocatedDeviceTracker(context, Type1Device.class, null);
		tracker.open();

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 2));

		dev1SReg.unregister();

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));

		dev2SReg.unregister();

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 0));
	}
	

	@Test
	public void testTrackerWithoutCustomizerAndFilter() {
		ContextManager contextMgr = helper.getServiceObject(ContextManager.class);
		Assert.assertNotNull(contextMgr);

		LocatedDeviceTracker tracker = new LocatedDeviceTracker(context, Type1Device.class, null);
		tracker.open();
		Assert.assertEquals(0, tracker.size());

		GenericDevice device1 = mock(Type1Device.class);
		String dev1SN = "dev1";
		when(device1.getSerialNumber()).thenReturn(dev1SN);
		
		ServiceRegistration dev1SReg = DeviceTestUtil.registerDevice(context, device1, GenericDevice.class, Type1Device.class);

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));

		GenericDevice device2 = mock(Type1Device.class);
		String dev2SN = "dev2";
		when(device2.getSerialNumber()).thenReturn(dev2SN);
		ServiceRegistration dev2SReg = DeviceTestUtil.registerDevice(context, device2, GenericDevice.class, Type1Device.class);

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 2));

		// cleanup
		dev1SReg.unregister();
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));
		
		dev2SReg.unregister();
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 0));
	}

	@Test
	public void testTrackerWithCustomizerAndFilter() {
		ContextManager contextMgr = helper.getServiceObject(ContextManager.class);
		Assert.assertNotNull(contextMgr);

		LocatedDeviceTrackerCustomizer customizer = mock(LocatedDeviceTrackerCustomizer.class);

		when(customizer.addingDevice(any(LocatedDevice.class))).thenReturn(true);

		LocatedDeviceTracker tracker = new LocatedDeviceTracker(context, Type1Device.class, customizer);
		tracker.open();
		Assert.assertEquals(0, tracker.size());

		GenericDevice device1 = new Type1DeviceImpl("dev1");

		// Register the device dev1
		ServiceRegistration dev1SReg = registerDevice(context, device1, GenericDevice.class, Type1Device.class);

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));

		LocatedDevice lDevice1 = contextMgr.getDevice("dev1");

		Assert.assertNotNull(lDevice1);

		Position originalPosition = lDevice1.getCenterAbsolutePosition();
		Position newPosition = new Position(10, 10);

		verify(customizer, times(1)).addingDevice(lDevice1);
		verify(customizer, times(1)).addedDevice(lDevice1);

		// Move device
		lDevice1.setCenterAbsolutePosition(newPosition);

		verify(customizer, times(1)).movedDevice(lDevice1, originalPosition, newPosition);

		// Modify device property
		lDevice1.setPropertyValue("property-1", "value-1");

		// the callback was called
		verify(customizer, times(1)).modifiedDevice(lDevice1, "property-1", null, "value-1");

		lDevice1.setPropertyValue("property-1", "value-2");

		// the callback was called again
		verify(customizer, times(1)).modifiedDevice(lDevice1, "property-1", "value-1", "value-2");

		GenericDevice device2 = new Type1DeviceImpl("dev2");

		ServiceRegistration dev2SReg = registerDevice(context, device2, GenericDevice.class, Type1Device.class);

		LocatedDevice lDevice2 = contextMgr.getDevice("dev2");

		Assert.assertNotNull(lDevice2);

		verify(customizer, times(1)).addingDevice(lDevice2);

		verify(customizer, times(1)).addedDevice(lDevice2);

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 2));

		// cleanup
		dev1SReg.unregister();

		// Only one located device match the tracker
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));

		// The removedDevice is invoked one time with lDevcie1 as argument
		verify(customizer, times(1)).removedDevice(lDevice1);

		dev2SReg.unregister();

		// No located device match the tracker
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 0));

		// The removedDevice is invoked one time with lDevcie2 as argument
		verify(customizer, times(1)).removedDevice(lDevice2);

	}
	

	
	@Test
	public void testTrackerUsingProperties() {
		ContextManager contextMgr = helper.getServiceObject(ContextManager.class);
		Assert.assertNotNull(contextMgr);

		LocatedDeviceTracker tracker = new LocatedDeviceTracker(context, Type1Device.class, null, "property1");
		tracker.open();
		Assert.assertEquals(0, tracker.size());

		GenericDevice device1 = new Type1DeviceImpl("dev1");
		
		ServiceRegistration dev1SReg = DeviceTestUtil.registerDevice(context, device1, GenericDevice.class, Type1Device.class);
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 0));
				
		device1.setPropertyValue("property1", "value1");

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));
		
		device1.removeProperty("property1");
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 0));
		
		device1.setPropertyValue("property1", "value1");
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));		
		
		GenericDevice device2 = new Type1DeviceImpl("dev2");

		ServiceRegistration dev2SReg = DeviceTestUtil.registerDevice(context, device2, GenericDevice.class, Type1Device.class);

		device2.setPropertyValue("property2", "value2");
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));
		
		device2.setPropertyValue("property1", "value2");
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 2));
		
		device2.removeProperty("property1");
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));
		
		device2.setPropertyValue("property1", "value2");
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 2));

		dev1SReg.unregister();
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));
				
		dev2SReg.unregister();
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 0));
	}

}
