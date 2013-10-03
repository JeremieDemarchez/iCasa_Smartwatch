package fr.liglab.adele.icasa.device.zigbee.driver.impl.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.TypeCode;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;
import fr.liglab.adele.icasa.device.zigbee.driver.impl.ZigbeeDriverImpl;
import fr.liglab.adele.icasa.device.zigbee.driver.serial.SerialPortHandler;
import fr.liglab.adele.icasa.device.zigbee.driver.serial.model.DeviceCategory;

@RunWith(MockitoJUnitRunner.class)
public class SerialPortHandlerTest {

	@Mock
	ZigbeeDriverImpl trackerMgr;

	private SerialPortHandler handler;

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void newZigbeeDataFrameWithTypeParsingTest() throws IOException {

		String moduleAddress = "1234";
		String dataValue = "1";
		String batteryValue = "7";

		handler = new SerialPortHandler(trackerMgr);
		when(trackerMgr.getTrackers()).thenReturn(
				new ArrayList<ZigbeeDeviceTracker>());

		DeviceInfo deviceInfo = handler.parseData(generateZigbeeNewFrame(
				moduleAddress, dataValue, "D", batteryValue,
				DeviceCategory.ACTUATOR, "001"));

		Assert.assertEquals(deviceInfo.getBatteryLevel(),
				Float.valueOf(batteryValue) / 10);
		Assert.assertEquals(deviceInfo.getModuleAddress(), moduleAddress);
		Assert.assertEquals(deviceInfo.getDeviceData().getData(), dataValue);
		Assert.assertEquals(deviceInfo.getTypeCode(), TypeCode.A001);

	}

	@Test
	public void newZigbeeDataFrameWithLongerDataParsingTest()
			throws IOException {

		String moduleAddress = "1234";
		String dataValue = "13421";
		String batteryValue = "7";

		handler = new SerialPortHandler(trackerMgr);
		when(trackerMgr.getTrackers()).thenReturn(
				new ArrayList<ZigbeeDeviceTracker>());

		DeviceInfo deviceInfo = handler.parseData(generateZigbeeNewFrame(
				moduleAddress, dataValue, "D", batteryValue,
				DeviceCategory.ACTUATOR, "001"));

		Assert.assertEquals(deviceInfo.getBatteryLevel(),
				Float.valueOf(batteryValue) / 10);
		Assert.assertEquals(deviceInfo.getModuleAddress(), moduleAddress);
		Assert.assertEquals(deviceInfo.getDeviceData().getData(), dataValue);
		Assert.assertEquals(deviceInfo.getTypeCode(), TypeCode.A001);

	}

	@Test
	public void newZigbeeDataFrameWithBInsideDataParsingTest()
			throws IOException {

		String moduleAddress = "1234";
		String dataValue = "13B421";
		String batteryValue = "7";

		handler = new SerialPortHandler(trackerMgr);
		when(trackerMgr.getTrackers()).thenReturn(
				new ArrayList<ZigbeeDeviceTracker>());

		DeviceInfo deviceInfo = handler.parseData(generateZigbeeNewFrame(
				moduleAddress, dataValue, "D", batteryValue,
				DeviceCategory.ACTUATOR, "001"));

		Assert.assertEquals(deviceInfo.getBatteryLevel(),
				Float.valueOf(batteryValue) / 10);
		Assert.assertEquals(deviceInfo.getModuleAddress(), moduleAddress);
		Assert.assertEquals(deviceInfo.getDeviceData().getData(), dataValue);
		Assert.assertEquals(deviceInfo.getTypeCode(), TypeCode.A001);

	}

	@Test
	public void malformedNewZigbeeDataFrameParsingTest()
			throws IOException {

		String moduleAddress = "1234";
		String dataValue = "13421";
		String batteryValue = "7";

		handler = new SerialPortHandler(trackerMgr);
		when(trackerMgr.getTrackers()).thenReturn(
				new ArrayList<ZigbeeDeviceTracker>());

		List<Byte> sb = generateZigbeeNewFrame(
				moduleAddress, dataValue, "D", batteryValue,
				DeviceCategory.ACTUATOR, "001");
		sb.remove(0);
		DeviceInfo deviceInfo = handler.parseData(sb);

		Assert.assertEquals(deviceInfo,null);

	}

	private List<Byte> generateZigbeeOldFrame(String moduleAddress,
			String dataValue, String frameType, String batteryValue) {

		List<Byte> sb = new ArrayList<Byte>();
		int csum = 0;

		sb.add(frameType.getBytes()[0]);
		csum += frameType.getBytes()[0];
		for (Byte b : moduleAddress.getBytes()) {
			sb.add(b);
			csum += b;
		}
		for (Byte b : dataValue.getBytes()) {
			sb.add(b);
			csum += b;
		}

		sb.add("B".getBytes()[0]);
		csum += "B".getBytes()[0];

		sb.add(batteryValue.getBytes()[0]);
		csum += batteryValue.getBytes()[0];

		csum &= 0xff;
		sb.add((byte) ((csum >> 4) + 0x30));
		sb.add((byte) ((csum & 0x0F) + 0x30));
		return sb;
	}

	public List<Byte> generateZigbeeNewFrame(String moduleAddress,
			String dataValue, String frameType, String batteryValue,
			DeviceCategory category, String type) {

		int csum = 0;
		List<Byte> sb = generateZigbeeOldFrame(moduleAddress, dataValue,
				frameType, batteryValue);

		// remove checksum
		sb.remove(sb.size() - 1);
		sb.remove(sb.size() - 1);

		int insertPos = sb.lastIndexOf((byte) 'B') + 2;
		sb.add(insertPos, (byte) category.getValue());
		insertPos++;

		for (Byte b : type.getBytes()) {
			sb.add(insertPos, b);
			insertPos++;
		}

		// recompute checksum
		for (Byte b : sb) {
			csum += b;
		}
		csum &= 0xff;
		sb.add(insertPos, (byte) ((csum >> 4) + 0x30));
		sb.add(insertPos + 1, (byte) ((csum & 0x0F) + 0x30));
		return sb;
	}
}
