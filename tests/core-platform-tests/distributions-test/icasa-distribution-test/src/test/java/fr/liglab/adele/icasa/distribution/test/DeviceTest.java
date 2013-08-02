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
package fr.liglab.adele.icasa.distribution.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.commons.test.utils.TestUtils;
import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.util.AbstractDevice;
import fr.liglab.adele.icasa.device.util.LocatedDeviceTracker;
import fr.liglab.adele.icasa.device.util.LocatedDeviceTrackerCustomizer;
import fr.liglab.adele.icasa.distribution.test.device.DeviceTrackedNumberCondition;
import fr.liglab.adele.icasa.distribution.test.device.Type1Device;
import fr.liglab.adele.icasa.distribution.test.device.Type1DeviceImpl;
import fr.liglab.adele.icasa.distribution.test.util.DeviceTestUtil;
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.LocatedDeviceListener;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.ZoneListener;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class DeviceTest extends AbstractDistributionBaseTest {

	@Inject
	public BundleContext context;

	@Before
	public void setUp() {
		waitForStability(context);
	}

	@After
	public void tearDown() {

	}

	/**
	 * Test that there is no device at stratup.
	 */
	@Ignore
	@Test
	public void getDevicesWithoutDevicesTest() {
		ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
		if (contextMgr == null) {
			Assert.fail("Unable to get ServiceReference for ContextManager");
		}

		List<LocatedDevice> devices = contextMgr.getDevices();
		Assert.assertNotNull(devices);
		Assert.assertEquals(0, devices.size());
	}

	@Ignore
	@Test
	public void testDeviceTrackerWithoutCustomizerAndFilter() {
		ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
		Assert.assertNotNull(contextMgr);

		LocatedDeviceTracker tracker = new LocatedDeviceTracker(context, Type1Device.class, null);
		tracker.open();
		Assert.assertEquals(0, tracker.size());

		GenericDevice dev1 = mock(Type1Device.class);
		String dev1SN = "dev1";
		when(dev1.getSerialNumber()).thenReturn(dev1SN);
		
		ServiceRegistration dev1SReg = DeviceTestUtil.registerDevice(context, dev1, GenericDevice.class, Type1Device.class);

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));

		GenericDevice dev2 = mock(Type1Device.class);
		String dev2SN = "dev2";
		when(dev2.getSerialNumber()).thenReturn(dev2SN);
		ServiceRegistration dev2SReg = DeviceTestUtil.registerDevice(context, dev2, GenericDevice.class, Type1Device.class);

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 2));

		// cleanup
		dev1SReg.unregister();
		dev2SReg.unregister();
	}

	@Ignore
	@Test
	public void testDeviceTrackerWithoutCustomizerAndFilterFirtsDevices() {
		ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
		Assert.assertNotNull(contextMgr);


		GenericDevice device1 = new Type1DeviceImpl("dev1");
		ServiceRegistration dev1SReg = DeviceTestUtil.registerDevice(context, device1, GenericDevice.class, Type1Device.class);
		
		GenericDevice device2 = new Type1DeviceImpl("dev2");
		ServiceRegistration dev2SReg = DeviceTestUtil.registerDevice(context, device1, GenericDevice.class, Type1Device.class);		
		
		
		
		LocatedDeviceTracker tracker = new LocatedDeviceTracker(context, Type1Device.class, null);
		tracker.open();

		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 2));

		dev1SReg.unregister();
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));
		
		dev2SReg.unregister();
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 0));
	}
	
	@Test
	public void testDeviceTrackerUsingProperties() {
		ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
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

		// cleanup
		dev1SReg.unregister();
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 1));
				
		dev2SReg.unregister();
		
		TestUtils.testConditionWithTimeout(new DeviceTrackedNumberCondition(tracker, 0));
	}
	
	

	
	@Ignore
	@Test
	public void testAttachDeviceEventTest() {
		final String zoneId = "zone1";
		final String deviceId = "device1HXKJ";

		ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
		Assert.assertNotNull(contextMgr);
		// Register new Device
		ServiceRegistration deviceRegistration = registerEmptyDevice(deviceId);
		// create new zone
		contextMgr.createZone(zoneId, 20, 20, 0, 50, 50, 50);
		Zone zone = contextMgr.getZone(zoneId);
		Assert.assertNotNull(zone);
		ZoneListener zoneListener = mock(ZoneListener.class);
		zone.addListener(zoneListener);
		// Retrieve the located Device and spy it.
		LocatedDevice device = contextMgr.getDevice(deviceId);
		Assert.assertNotNull(device);
		// attach device to zone
		zone.attachObject(device);
		verify(zoneListener, atLeast(1)).deviceAttached(zone, device);// verify that device is attached
		deviceRegistration.unregister();// unregister device service
	}

	@Ignore
	@Test
	public void testAttachedDeviceToZoneMoveZoneTest() {
		final String zoneId = "zone1";
		final String deviceId = "device1HXKJ";

		ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
		Assert.assertNotNull(contextMgr);
		// Register new Device
		ServiceRegistration deviceRegistration = registerEmptyDevice(deviceId);
		// Initial configuration
		Position zonePosition = new Position(20, 20, 0);// first position
		Position newZonePosition = new Position(zonePosition.x + 10, zonePosition.y + 10, 0);// the position when moved
																														 // zone

		contextMgr.createZone(zoneId, zonePosition.x, zonePosition.y, zonePosition.z, 70, 70, 70);
		contextMgr.moveDeviceIntoZone(deviceId, zoneId);
		Position devicePosition = contextMgr.getDevicePosition(deviceId);
		Position newDevicePosition = new Position(devicePosition.x + 10, devicePosition.y + 10, 0); // the expected
																																  // position
		Zone zone = contextMgr.getZone(zoneId);
		Assert.assertNotNull(zone);

		// Retrieve the located Device and spy it.
		LocatedDevice device = contextMgr.getDevice(deviceId);
		LocatedDeviceListener locatedDeviceListener = mock(LocatedDeviceListener.class);
		Assert.assertNotNull(device);
		device.addListener(locatedDeviceListener);
		// attach device to zone
		zone.attachObject(device);
		try {
			contextMgr.moveZone(zone.getId(), newZonePosition.x, newZonePosition.y, newZonePosition.z);
		} catch (Exception e) {
			Assert.fail("Unable to move zone");
		}
		verify(locatedDeviceListener, atLeast(1)).deviceMoved(device, devicePosition, newDevicePosition);
		deviceRegistration.unregister();// unregister device service
	}

	@Ignore
	@Test
	public void testAttachedZoneToDeviceMovedDeviceTest() {
		final String zoneId = "zone1";
		final String deviceId = "device1HXKJ";
		final int moveLength = 10;

		ContextManager contextMgr = (ContextManager) getService(context, ContextManager.class);
		Assert.assertNotNull(contextMgr);
		// Register new Device
		ServiceRegistration deviceRegistration = registerEmptyDevice(deviceId);
		// Initial configuration
		Position zonePosition = new Position(20, 20, 0);// first position
		contextMgr.createZone(zoneId, zonePosition.x, zonePosition.y, zonePosition.z, 70, 70, 70);
		Position devicePosition = contextMgr.getDevicePosition(deviceId);
		Position newDevicePosition = new Position(devicePosition.x + moveLength, devicePosition.y + moveLength, 0); // the
																																						// expected
																																						// position
		Zone zone = contextMgr.getZone(zoneId);
		Assert.assertNotNull(zone);

		// Retrieve the located Device and attach a zone.
		LocatedDevice device = contextMgr.getDevice(deviceId);
		ZoneListener zoneListener = mock(ZoneListener.class);
		Assert.assertNotNull(device);
		// attach device to zone
		device.attachObject(zone);
		// Listen zone.
		zone.addListener(zoneListener);
		try {
			device.setCenterAbsolutePosition(newDevicePosition);
		} catch (Exception e) {
			Assert.fail("Unable to move device");
		}
		verify(zoneListener, atLeast(1)).zoneMoved(zone, zonePosition,
		      new Position(zonePosition.x + moveLength, zonePosition.y + moveLength, 0));
		deviceRegistration.unregister();// unregister device service
	}

	private ServiceRegistration registerEmptyDevice(final String serialNumber) {
		// new device object
		AbstractDevice deviceService = new AbstractDevice() {
			public String getSerialNumber() {
				return serialNumber;
			}
		};
		Dictionary serviceProperties = new Hashtable();
		serviceProperties.put(GenericDevice.DEVICE_SERIAL_NUMBER, serialNumber);// add serial number service prop
		ServiceRegistration registration = context.registerService(GenericDevice.class.getName(), deviceService,
		      serviceProperties);
		return registration;
	}





}
