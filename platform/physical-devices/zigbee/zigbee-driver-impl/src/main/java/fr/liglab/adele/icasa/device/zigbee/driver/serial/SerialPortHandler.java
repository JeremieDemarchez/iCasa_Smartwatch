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
package fr.liglab.adele.icasa.device.zigbee.driver.serial;

/**
 *
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.device.zigbee.driver.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.liglab.adele.icasa.device.zigbee.driver.DeviceInfo;
import fr.liglab.adele.icasa.device.zigbee.driver.TypeCode;
import fr.liglab.adele.icasa.device.zigbee.driver.ZigbeeDeviceTracker;
import fr.liglab.adele.icasa.device.zigbee.driver.impl.DataImpl;
import fr.liglab.adele.icasa.device.zigbee.driver.impl.DeviceInfoImpl;
import fr.liglab.adele.icasa.device.zigbee.driver.impl.ZigbeeDriverImpl;
import fr.liglab.adele.icasa.device.zigbee.driver.serial.model.DeviceDiscoveryTimeoutTask;
import fr.liglab.adele.icasa.device.zigbee.driver.serial.model.ResponseType;
import gnu.io.NRSerialPort;

/**
 * Class managing the serial port data input/output.
 *
 * @author Kettani Mehdi.
 */
public class SerialPortHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(Constants.ICASA_LOG_DEVICE+".zigbee");
	private volatile boolean socketOpened = true;
	private DataInputStream ins = null;
	private DataOutputStream ous = null;
    private Object streamLock = new Object();
    //requests to sent to devices.
    private Queue<List<Byte>> toWriteData = new LinkedList<List<Byte>>();//handle write in the same thread as read.
    private Map<String/*module*/,String /*Expected data*/>  requestData = new Hashtable();
	private Map<String /* module address */, ScheduledFuture<?>> deviceDiscoveryList = new HashMap<String, ScheduledFuture<?>>();
	/* @GardedBy(deviceList) */
	private Map<String /* module address */, DeviceInfo> deviceList = new HashMap<String, DeviceInfo>();
	/* @GardedBy(deviceList) */
	private Map<String /* module address */, String/*TypeCode*/> deviceTypes = new HashMap<String, String>();
	private ZigbeeDriverImpl trackerMgr;
	private ScheduledExecutorService executor;

	public SerialPortHandler(ZigbeeDriverImpl zigbeeDriverImpl) {
		this.trackerMgr = zigbeeDriverImpl;
		executor = Executors.newSingleThreadScheduledExecutor();
		deviceTypes.put("1234", TypeCode.IC001.getFriendlyName()); // Push button
		deviceTypes.put("5001", TypeCode.IA001.getFriendlyName()); // binary light
		deviceTypes.put("2345", TypeCode.IC004.getFriendlyName());//motion sensor
        deviceTypes.put("0001", TypeCode.IC004.getFriendlyName());//motion sensor
        deviceTypes.put("0002", TypeCode.IA001.getFriendlyName()); // binary light
	}

	public List<DeviceInfo> getDeviceInfos() {
		synchronized (deviceList) {
			return new ArrayList<DeviceInfo>(deviceList.values());
		}
	}

	/**
	 * Start listening on the given serial port.
	 *
	 * @param port
	 * @throws IOException
	 */
	public void startListening(String port, int baud) throws IOException {

		NRSerialPort serial = new NRSerialPort(port, baud);
		serial.connect();
        synchronized (streamLock){
		    ins = new DataInputStream(serial.getInputStream());
		    ous = new DataOutputStream(serial.getOutputStream());
        }

		try {
			while (socketOpened) {
                List<Byte> sb = read();
                if(sb.size()>1){
                    parseData(sb);
                    sb.clear();
                }
            }
		} finally {
			closeStreams();
			serial.disconnect();
		}
	}

	public void stopListening() {
		this.socketOpened = false;
	}

	/**
	 * Parse data from the list of byte read.
	 *
	 * @param sb
	 * @return
	 * @throws IOException
	 */
	public DeviceInfo parseData(List<Byte> sb) throws IOException {

		DeviceInfo deviceInfos = null;
		DataImpl dataValue = null;
		String moduleAddress = null;

		// verify checksum, otherwise discard
		if (verifyChecksum(sb)) {
			// depending on frame we got, send a response
			char type = getTrameType(sb);
			switch (type) {
			case 'W':
				moduleAddress = parseModuleAddress(sb);
				DeviceInfoImpl device = (DeviceInfoImpl) deviceList
						.get(moduleAddress);
				if (device != null) {
					device.setLastConnexionDate(new Date());
				}
				write(buildResponse(ResponseType.WATCHDOG, moduleAddress));
				break;
			case 'I':
				// identification frame not handled
				break;
			case 'R':
			case 'D':
				moduleAddress = parseModuleAddress(sb);
				boolean existingDevice = deviceList.containsKey(moduleAddress);
				if (existingDevice) {
					deviceInfos = deviceList.get(moduleAddress);
				} else {
					deviceInfos = new DeviceInfoImpl();
					((DeviceInfoImpl) deviceInfos)
							.setModuleAddress(moduleAddress);
					ScheduledFuture<?> scheduledDeviceTimeout = executor
							.scheduleWithFixedDelay(
									new DeviceDiscoveryTimeoutTask(
											moduleAddress), 0, 120,
									TimeUnit.SECONDS);
					deviceDiscoveryList.put(moduleAddress,
							scheduledDeviceTimeout);
					Runnable extendDeviceTimeoutTask = new ExtendDeviceTimeoutTask(
							scheduledDeviceTimeout, moduleAddress);
					executor.schedule(extendDeviceTimeoutTask, 120,
							TimeUnit.SECONDS);
				}
				dataValue = new DataImpl();
                Data oldData = deviceInfos.getDeviceData();
                float oldBatteryLevel = deviceInfos.getBatteryLevel();
				// parse to get data
				dataValue.setTimeStamp(new Date());
				dataValue.setData(parseDataValue(sb));
				// parse to get battery level
				((DeviceInfoImpl) deviceInfos).setBatteryLevel(Integer
						.valueOf(parseBatteryLevel(sb)));
				((DeviceInfoImpl) deviceInfos).setDeviceData(dataValue);
				((DeviceInfoImpl) deviceInfos).setLastConnexionDate(new Date());
				((DeviceInfoImpl) deviceInfos).setTypeCode(TypeCode
						.getTypeCodeByFriendlyName(deviceTypes
								.get(moduleAddress)));
                //notify battery level change and data change.
                //It will notify only when value has already changed.
                notifyBatteryLevelChange(deviceInfos, oldBatteryLevel);
                notifyDataChange(deviceInfos, oldData);
				if (type == 'D') {
                    logger.debug("Sent D to " + deviceInfos.getModuleAddress());
                    write(buildResponse(ResponseType.DATA,
                            deviceInfos.getModuleAddress()));
				}
                //sent writing to set data in device.
                if(type == 'R'){
                    handleWrite(moduleAddress);
                }
                if (!existingDevice) {
                    logger.debug("notifying tracker about new device.");
                    //notify to trackers.
                    notifyDeviceAdded(deviceInfos);
                }
				deviceList.put(moduleAddress, deviceInfos);
				break;
			default:
				logger.debug("unknown frame received : " + sb);
				break;
			}

		}
		return deviceInfos;
	}

	/**
	 * Build a response for the specified frame type.
	 *
	 * @param responseType
	 * @param moduleAddress
	 * @return
	 */
	private List<Byte> buildResponse(ResponseType responseType,
			String moduleAddress) {

		List<Byte> response = new ArrayList<Byte>();
		int csum = 0;

		response.add((byte) responseType.getValue());
		csum += responseType.getValue();
		for (Byte b : moduleAddress.getBytes()) {
			response.add(b);
			csum += b;
		}
		csum &= 0xff;
		response.add((byte) ((csum >> 4) + 0x30));
		response.add((byte) ((csum & 0x0F) + 0x30));
		response.add((byte) '\r');
		return response;
	}

	/**
	 * Build a response for the specified frame type and sets a new value for
	 * the device.
	 *
	 * @param responseType
	 * @param moduleAddress
	 * @param newValue
	 * @return
	 */
	private List<Byte> buildResponseWithNewValue(ResponseType responseType,
			String moduleAddress, String newValue) {
		List<Byte> response = new ArrayList<Byte>();
		int csum = 0;

		response.add((byte) responseType.getValue());
		csum += responseType.getValue();
		for (Byte b : moduleAddress.getBytes()) {
			response.add(b);
			csum += b;
		}
		response.add(newValue.getBytes()[0]);
		csum += newValue.getBytes()[0];
		csum &= 0xff;
		response.add((byte) ((csum >> 4) + 0x30));
		response.add((byte) ((csum & 0x0F) + 0x30));
		response.add((byte) '\r');
		return response;
	}

	/**
	 * Parse the frame type from the frame list of bytes given in parameter.
	 *
	 * @param sb
	 * @return
	 */
	private char getTrameType(List<Byte> sb) {
		return (char) sb.get(0).intValue();
	}

	/**
	 * Parse the module address in this frame.
	 *
	 * @param sb
	 * @return
	 */
	private String parseModuleAddress(List<Byte> sb) {
		return ByteToChar(sb).substring(1, 5);
	}

	/**
	 * Parse the battery level in this frame.
	 *
	 * @param sb
	 * @return
	 */
	private String parseBatteryLevel(List<Byte> sb) {
		return Character.toString((char) sb.get(sb.size() - 3).intValue());
	}

	/**
	 * Parse the data value in this frame.
	 *
	 * @param sb
	 * @return
	 */
	private String parseDataValue(List<Byte> sb) {
		return Character.toString((char) sb.get(5).intValue());
	}

	/**
	 * Check if checksum is correct.
	 *
	 * @param sb
	 * @return
	 */
	private boolean verifyChecksum(List<Byte> sb) {

		int csum, val;

		csum = (sb.get(sb.size() - 2).byteValue() & 0x0F) << 4;
		val = (sb.get(sb.size() - 1).byteValue()) & 0x0F;
		csum += val;

		val = 0;
		for (int i = 0; i < sb.size() - 2; i++) {
			val += sb.get(i);
		}
		val = val & 0xff;

		return (csum == val);
	}

	/**
	 * Convert a list of bytes into characters.
	 *
	 * @param byteList
	 * @return
	 */
	private StringBuffer ByteToChar(List<Byte> byteList) {

		StringBuffer charList = null;
		if (byteList != null && byteList.size() > 0) {
			charList = new StringBuffer();
			for (Byte b : byteList) {
				charList.append(new Character((char) b.byteValue()));
			}
		}
		return charList;
	}

	/**
	 * Write a list of bytes into this outputStream.
	 *
	 * @param data
	 * @throws IOException
	 */
	private void write(List<Byte> data) throws IOException {
        synchronized (streamLock){
            if (ous != null) {
                for (Byte b : data) {
                    ous.write(b.byteValue());
                }
            }
        }
	}

    /**
     * Read from the serial port. It finish read when the 0x0d(EOS) byte is read.
     * @return the list of read bytes without the EOS(0x0d) byte.
     * @throws IOException
     */
    private List<Byte> read() throws IOException {
        List<Byte> sb = new ArrayList<Byte>();
        byte readByte = 0x0d;
        do{
            synchronized (streamLock){
                readByte = (byte) ins.read();
                if(readByte != -1 && readByte != 0x0d) { //no data && end of stream.
                    sb.add(readByte);
                }
            }
        } while(readByte != 0x0d && socketOpened);
        return sb;
    }

	/**
	 * Write a response built from the given informations.
	 *
	 * @param responseType
	 *            Type of frame to respond to.
	 * @param moduleAddress
	 *            address of module to send response to.
	 * @param newValue
	 *            the new value to send.
	 * @throws IOException
	 */
	public void write(ResponseType responseType, String moduleAddress,
			String newValue) {
         synchronized (streamLock){ // add to queue.
             requestData.put(moduleAddress, newValue);//set expected data
         }
	}
    /**
     * Handles writing events.
     */
    private void handleWrite(String module){
        synchronized (streamLock){
            List<Byte> dataToSend = handleRequests(module);
            if(dataToSend != null){
                try {
                    write(dataToSend);
                } catch (IOException e) {
                    logger.error("Unable to write request. ", e);

                }
            }
        }
    }

    private List<Byte> handleRequests(String moduleAddress){
        String expected = requestData.get(moduleAddress);
        DeviceInfo info = deviceList.get(moduleAddress);
        if(info != null ){
            if(expected != null && info.getDeviceData().getData().compareTo(expected) != 0 ) {
                logger.debug("Resent request expected value");
                List<Byte> response = buildResponseWithNewValue(ResponseType.REQUEST, moduleAddress, expected);
                logger.trace("response sent to device " + moduleAddress + " : " + response.toString());
                return(response);
            } else {
                //logger.debug("Sent request value");
                requestData.remove(moduleAddress);
                return(buildResponseWithNewValue(ResponseType.REQUEST, moduleAddress, info.getDeviceData().getData()));
            }
        }
        return null;
    }

    /**
     * Close the serial port streams.
     */
    private void closeStreams(){
        synchronized (streamLock){
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    logger.error("Exception while closing inputstream : ", e);
                }
            }
            if (ous != null) {
                try {
                    ous.close();
                } catch (IOException e) {
                    logger.error("Exception while closing outputstream : ", e);
                }
            }
        }
    }

	/**
	 * Task to extend devices timeouts.
	 *
	 * @author Kettani Mehdi.
	 *
	 */
	private final class ExtendDeviceTimeoutTask implements Runnable {

		private ScheduledFuture<?> fSchedFuture;
		private String moduleAddress;

		ExtendDeviceTimeoutTask(ScheduledFuture<?> aSchedFuture,
				String moduleAddress) {
			fSchedFuture = aSchedFuture;
			this.moduleAddress = moduleAddress;
		}

		public void run() {
			DeviceInfo infos = deviceList.get(moduleAddress);
			if (infos.getLastConnexionDate().getTime() < (new Date().getTime() - 120000)) {
				// last connexion was before 2min, unregister device
				fSchedFuture.cancel(true);
				deviceList.remove(moduleAddress);
                //notify to trackers.
                notifyDeviceRemoved(infos);
			} else {
				// extend timeout
				Runnable extendDeviceTimeoutTask = new ExtendDeviceTimeoutTask(
						fSchedFuture, moduleAddress);
				executor.schedule(extendDeviceTimeoutTask, 120,
						TimeUnit.SECONDS);
			}
		}
	}

	public DeviceInfo getDeviceInfo(String moduleAddress) {
		synchronized (deviceList) {
			return deviceList.get(moduleAddress);
		}
	}

    /**
     * Notify trackers when there is a new ZigBee device.
     * @param deviceInfo
     */
    private void notifyDeviceAdded(DeviceInfo deviceInfo){
        logger.trace("Device Added");
        logInfo(deviceInfo);
        List<ZigbeeDeviceTracker> listeners = trackerMgr.getTrackers();
        for (ZigbeeDeviceTracker tracker : listeners) {
            try {
                tracker.deviceAdded(deviceInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Notify trackers when a ZigBee device is no longer available..
     * @param deviceInfo
     */
    private void notifyDeviceRemoved(DeviceInfo deviceInfo){
        logger.trace("Device Removed");
        logInfo(deviceInfo);
        List<ZigbeeDeviceTracker> listeners = trackerMgr.getTrackers();
        for (ZigbeeDeviceTracker tracker : listeners) {
            try {
                tracker.deviceRemoved(deviceInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Notify the new battery level.
     * @param info
     * @param oldLevel
     */
    private void notifyBatteryLevelChange(DeviceInfo info, float oldLevel){
        if(info.getBatteryLevel() != oldLevel){ //only notify when data has changed.
            logger.trace("Battery level changed");
            logInfo(info);
            List<ZigbeeDeviceTracker> listeners = trackerMgr.getTrackers();
            for (ZigbeeDeviceTracker tracker : listeners) {
                try {
                    tracker.deviceBatteryLevelChanged(info.getModuleAddress(),info.getBatteryLevel(), oldLevel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Notify the data value.
     * @param info
     * @param oldData
     */
    private void notifyDataChange(DeviceInfo info, Data oldData){
        String oldDataValue = oldData!= null ? oldData.getData(): "";

        if(oldData == null || info.getDeviceData().getData().compareTo(oldDataValue) != 0){ //only notify when data has changed.
            logger.trace("Data changed (Old value:" + oldDataValue + ")");
            logInfo(info);
            List<ZigbeeDeviceTracker> listeners = trackerMgr.getTrackers();
            for (ZigbeeDeviceTracker tracker : listeners) {
                try {
                    tracker.deviceDataChanged(info.getModuleAddress(), oldData, info.getDeviceData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * log device info.
     * @param deviceInfo
     */
    private void logInfo(DeviceInfo deviceInfo){
        try{
            logger.trace("battery : " + deviceInfo.getBatteryLevel());
            logger.trace("ModuleAddress : "
                    + deviceInfo.getModuleAddress());
            logger.trace("data value : " + deviceInfo.getDeviceData().getData());
            logger.trace("type code : " + deviceInfo.getTypeCode() == null ? "unknown" : deviceInfo.getTypeCode().getFriendlyName());
        }catch(Exception ex) {
            logger.error("Unable to log DeviceIngo" + deviceInfo.getModuleAddress());
        }
    }
}
