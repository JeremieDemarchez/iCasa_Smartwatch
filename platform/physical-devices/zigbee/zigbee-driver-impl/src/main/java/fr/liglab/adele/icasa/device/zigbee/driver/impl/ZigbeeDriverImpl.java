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
/**
 *
 */
package fr.liglab.adele.icasa.device.zigbee.driver.impl;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.device.zigbee.driver.*;
import fr.liglab.adele.icasa.device.zigbee.driver.serial.SerialPortHandler;
import fr.liglab.adele.icasa.device.zigbee.driver.serial.model.ResponseType;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Implementation class for the Zigbee Driver interface.
 *
 */
@Component(immediate = true)
@Provides(specifications = { ZigbeeDriver.class })
public class ZigbeeDriverImpl implements ZigbeeDriver {

	private SerialPortHandler handler;

	private final BundleContext context;

	private static final Logger logger = LoggerFactory
			.getLogger(Constants.ICASA_LOG_DEVICE + ".zigbee");

	private static final String SERIAL_PORT_PROPERTY = "zigbee.driver.port";

	private static final String BAUD_RATE_PROPERTY = "baud.rate";

	@Property(mandatory = true, name = SERIAL_PORT_PROPERTY)
	private String port;

	@Property(name = BAUD_RATE_PROPERTY, mandatory = true)// value = "115200"
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

	public ZigbeeDriverImpl( BundleContext bundleContext) throws IOException {
		handler = new SerialPortHandler(this);
		trackers = new ArrayList<ZigbeeDeviceTracker>();
		context = bundleContext;
	}

	@Validate
	private void start() {
		String lport = getCOMPort();
		if(lport.compareTo("NONE") == 0 && getBaud() == 0){
			logger.warn("Please set a port for ZigBee Driver");
			return;
		}
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					logger.info("Try to start listenning " + getCOMPort() + " with baud rate " + getBaud());
					handler.startListening(getCOMPort(), getBaud());
				} catch (Exception e) {
					logger.warn("Unable to connect into port: " + getCOMPort());
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
		logger.debug("sending request response to device : " + moduleAddress + " with value : " + dataToSet);
		handler.write(ResponseType.REQUEST, moduleAddress, dataToSet);
	}

}
