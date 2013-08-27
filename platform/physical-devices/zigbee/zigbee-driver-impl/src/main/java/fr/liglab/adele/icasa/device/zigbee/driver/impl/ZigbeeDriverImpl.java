/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
/**
 * 
 */
package fr.liglab.adele.icasa.device.zigbee.driver.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.TypeCode;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDriver;
import fr.liglab.adele.icasa.device.zigbee.driver.serial.SerialPortHandler;
import fr.liglab.adele.icasa.device.zigbee.driver.serial.model.ResponseType;

/**
 * Implementation class for the Zigbee Driver interface.
 * 
 * @author tfqg0024
 */
@Component(name = "zigbee.driver.impl")
@Provides(specifications = { ZigbeeDriver.class })
public class ZigbeeDriverImpl implements ZigbeeDriver {

	private SerialPortHandler handler;
	
	private static final Logger logger = LoggerFactory
			.getLogger(ZigbeeDriverImpl.class);

	@Property(mandatory = false, value = "COM3", name = "serial.port")
	private String port;

	@Property(name = "baud.rate", mandatory = false, value = "115200")
	private Integer baud;

	/* @GardedBy(trackers) */
	private List<ZigbeeDeviceTracker> trackers;
	
	public List<ZigbeeDeviceTracker> getTrackers() {
		synchronized(trackers) {
			return new ArrayList<ZigbeeDeviceTracker>(trackers);
		}
	}

	@Bind(id="zigbeeDeviceTrackers", optional=true, aggregate=true)
	private void bindZigbeeDeviceTracker(ZigbeeDeviceTracker tracker){
		synchronized(trackers) {
			List<DeviceInfo> deviceInfos = handler.getDeviceInfos();
			trackers.add(tracker);
			for (DeviceInfo deviceInfo : deviceInfos) {
				try {
					tracker.deviceAdded(deviceInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Unbind(id="zigbeeDeviceTrackers")
	private void unbindZigbeeDeviceTracker(ZigbeeDeviceTracker tracker){
		synchronized(trackers) {
			List<DeviceInfo> deviceInfos = handler.getDeviceInfos();
			trackers.remove(tracker);
			for (DeviceInfo deviceInfo : deviceInfos) {
				try {
					tracker.deviceRemoved(deviceInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ZigbeeDriverImpl() throws IOException {
		handler = new SerialPortHandler(this);
		trackers = new ArrayList<ZigbeeDeviceTracker>();
	}

	@Validate
	private void start() throws IOException {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					handler.startListening(port, baud);
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
			
		}, "zigbee driver listenning port thread");
		thread.start();
	}

	@Invalidate
	private void stop() {
		handler.stopListening();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getCOMPort()
	 */
	@Override
	public String getCOMPort() {
		return port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getBaud()
	 */
	@Override
	public int getBaud() {
		return baud;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getDeviceInfos
	 * ()
	 */
	@Override
	public Set<DeviceInfo> getDeviceInfos() {
		return new TreeSet<DeviceInfo>(handler.getDeviceInfos());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getDeviceInfos
	 * (fr.liglab.adele.habits.monitoring.zigbee.driver.TypeCode)
	 */
	@Override
	public Set<DeviceInfo> getDeviceInfos(TypeCode typeCode) {
		Set<DeviceInfo> typedDevices = new HashSet<DeviceInfo>();
		for (DeviceInfo deviceInfo : handler.getDeviceInfos()) {
			if (deviceInfo.getTypeCode().equals(typeCode)) {
				typedDevices.add(deviceInfo);
			}
		}
		return typedDevices;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#getData(
	 * java.lang.String)
	 */
	@Override
	public Data getData(String moduleAddress) {
		DeviceInfo deviceInfo = handler.getDeviceInfo(moduleAddress);
		if (deviceInfo == null)
			return null;
		
		return deviceInfo.getDeviceData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.liglab.adele.habits.monitoring.zigbee.driver.ZigbeeDriver#setData(
	 * String moduleAddress, String dataToSet)
	 */
	@Override
	public void setData(String moduleAddress, String dataToSet) {
		try {
			logger.debug("sending request response to device : " + moduleAddress + " with value : " + dataToSet);
			handler.write(ResponseType.REQUEST, moduleAddress, dataToSet);
//			handler.write(buildResponseWithNewValue(ResponseType.REQUEST,
//					moduleAddress, dataValue.getData()
//								.equals("1") ? "0" : "1"));
			
		} catch (IOException e) {
			logger.error("IOException caught while sending data to device : " + moduleAddress , e);
		}
	}

}
