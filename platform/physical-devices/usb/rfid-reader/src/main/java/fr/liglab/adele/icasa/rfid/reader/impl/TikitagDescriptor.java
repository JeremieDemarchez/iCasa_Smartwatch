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
package fr.liglab.adele.icasa.rfid.reader.impl;

import org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers;
import org.apache.felix.usb.basedriver.descriptor.UsbDevice;

public class TikitagDescriptor {

	private static final int TIKITAG_ENDPOINT_IN = 0x82;
	private static final int TIKITAG_ENDPOINT_OUT = 0x02;
	private static final int TIMEOUT = 2000;
	private static final int READ_TIMEOUT = 750;
	private static final byte POWEROFF[] = { 0x62, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00 };
	private static final byte POWERON[] = { 0x62, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x00, 0x00 };
	private static final byte GETUID_0[] = { 0x6F, 0x0E, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, (byte) 0xFF,
	      0x00, 0x00, 0x00, 0x09, (byte) 0xD4, 0x60, 0x01, 0x01, 0x20, 0x23, 0x11, 0x04, 0x10 };
	private static final byte GETUID_1[] = { 0x6F, 0x05, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, (byte) 0xFF,
	      (byte) 0xC0, 0x00, 0x00, 0x13 };
	private byte[] buffer = new byte[64];;
	private boolean debug = false;
	private boolean opened = false;
	private String id;
	private long handle;
	private UsbDevice usb_Device;
	private UsbBridgeDrivers usbManager;

	public TikitagDescriptor(String id2, UsbDevice usbDev, UsbBridgeDrivers usblib) {
		this.id = id2;
		this.usb_Device = usbDev;
		this.usbManager = usblib;
	}

	public synchronized void open() {
		if ((opened == false) && (usb_Device != null)) {
			handle = usbManager.open(usb_Device);
			System.out.println("DEBUG_JTikitag open -> usb_open : " + handle);

			int lDebug;
			if (System.getProperty("os.name").equals("Linux")) {
				printDebug("os -> Running on Linux");
				// lDebug = usbManager.claim_interface(usb_Device.getHandle(),
				// 1);
				lDebug = usbManager.claimInterface(usb_Device.getHandle(), 1);
			} else {
				printDebug("os -> Running on " + System.getProperty("os.name"));
				// lDebug = usbManager.set_configuration(usb_Device, 1);
				lDebug = usbManager.setConfiguration(usb_Device.getHandle(), 1);
				printDebug("open -> usb_set_configuration : " + lDebug);
				// lDebug = usbManager.claim_interface(usb_Device, 0);
				lDebug = usbManager.claimInterface(usb_Device.getHandle(), 0);
			}

			printDebug("open -> usb_claim_interface : " + lDebug);
			// lDebug = usbManager.set_altinterface(usb_Device, 0);
			lDebug = usbManager.setAltinterface(usb_Device.getHandle(), 0);
			printDebug("open -> usb_set_altinterface : " + lDebug);
			// int lWrite = usbManager.bulk_write(usb_Device,
			// TIKITAG_ENDPOINT_OUT, POWEROFF, POWEROFF.length, TIMEOUT);
			int lWrite = usbManager.bulkWrite(usb_Device.getHandle(), TIKITAG_ENDPOINT_OUT, POWEROFF, POWEROFF.length,
			      TIMEOUT);
			// int lRead = usbManager.bulk_read(usb_Device, TIKITAG_ENDPOINT_IN,
			// buffer, buffer.length, TIMEOUT);
			int lRead = usbManager.bulkRead(usb_Device.getHandle(), TIKITAG_ENDPOINT_IN, buffer, buffer.length, TIMEOUT);
			printDebug("open -> " + lWrite + " " + lRead);

			// lWrite = usbManager.bulk_write(usb_Device, TIKITAG_ENDPOINT_OUT,
			// POWERON, POWERON.length, TIMEOUT);
			lWrite = usbManager.bulkWrite(usb_Device.getHandle(), TIKITAG_ENDPOINT_OUT, POWERON, POWERON.length, TIMEOUT);
			// lRead = usbManager.bulk_read(usb_Device, TIKITAG_ENDPOINT_IN,
			// buffer, buffer.length, TIMEOUT);
			lRead = usbManager.bulkRead(usb_Device.getHandle(), TIKITAG_ENDPOINT_IN, buffer, buffer.length, TIMEOUT);
			printDebug("open -> " + lWrite + " " + lRead);
			opened = true;
		}

	}

	public String getTag() {
		if (opened == true) {

			@SuppressWarnings("unused")
			// int lWrite = usbManager.bulk_write(usb_Device,
			// TIKITAG_ENDPOINT_OUT, GETUID_0, GETUID_0.length, TIMEOUT);
			int lWrite = usbManager.bulkWrite(usb_Device.getHandle(), TIKITAG_ENDPOINT_OUT, GETUID_0, GETUID_0.length,
			      TIMEOUT);

			// int lRead = usbManager.bulk_read(usb_Device, TIKITAG_ENDPOINT_IN,
			// buffer, buffer.length, READ_TIMEOUT);

			int lRead = usbManager.bulkRead(usb_Device.getHandle(), TIKITAG_ENDPOINT_IN, buffer, buffer.length,
			      READ_TIMEOUT);

			if (lRead == 12 && buffer[10] == (byte) 0x61 && buffer[11] == (byte) 0x13) {
				lWrite = usbManager.bulkWrite(usb_Device.getHandle(), TIKITAG_ENDPOINT_OUT, GETUID_1, GETUID_1.length,
				      TIMEOUT);
				lRead = usbManager.bulkRead(usb_Device.getHandle(), TIKITAG_ENDPOINT_IN, buffer, buffer.length, TIMEOUT);
				if (lRead == 29) {
					// return String.format("%02X%02X%02X%02X%02X%02X%02X",
					// buffer[20],buffer[21],buffer[22],buffer[23],buffer[24],buffer[25],buffer[26]);
					return new String("" + Integer.toHexString(buffer[20] & 0xFF) + Integer.toHexString(buffer[21] & 0xFF)
					      + Integer.toHexString(buffer[22] & 0xFF) + Integer.toHexString(buffer[23] & 0xFF)
					      + Integer.toHexString(buffer[24] & 0xFF) + Integer.toHexString(buffer[25] & 0xFF)
					      + Integer.toHexString(buffer[26] & 0xFF)).toUpperCase();
				}
			}
		}
		return null;
	}

	public void setDebug(boolean pState) {
		debug = pState;
	}

	public void close() {
		try {
			if (opened == true) {
				usbManager.releaseInterface(usb_Device.getHandle(), 0);
				Thread.sleep(500);
				usbManager.close(usb_Device.getHandle());
			}
			// usblib.close(handle);
			opened = false;

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void printDebug(String pStr) {
		if (debug == true)
			System.out.println("DEBUG_JTikitag " + pStr);
	}

	/**
	 * @return the opened
	 */
	public boolean isOpened() {
		return opened;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

}
