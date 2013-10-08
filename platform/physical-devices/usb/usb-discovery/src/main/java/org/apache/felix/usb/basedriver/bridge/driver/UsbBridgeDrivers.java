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
package org.apache.felix.usb.basedriver.bridge.driver;

import org.apache.felix.usb.basedriver.descriptor.UsbDevice;

/**
 * The USB Bridge manage Usb devices using LibUsb and LibusbJava Library.
 */

public interface UsbBridgeDrivers {

    /** The Constant REQ_GET_STATUS. */
    public static final int REQ_GET_STATUS = 0;

    /** The Constant REQ_CLEAR_FEATURE. */
    public static final int REQ_CLEAR_FEATURE = 1;

    /** The Constant REQ_SET_FEATURE. */
    public static final int REQ_SET_FEATURE = 3;

    /** The Constant REQ_SET_ADDRESS. */
    public static final int REQ_SET_ADDRESS = 5;

    /** The Constant REQ_GET_DESCRIPTOR. */
    public static final int REQ_GET_DESCRIPTOR = 6;

    /** The Constant REQ_SET_DESCRIPTOR. */
    public static final int REQ_SET_DESCRIPTOR = 7;

    /** The Constant REQ_GET_CONFIGURATION. */
    public static final int REQ_GET_CONFIGURATION = 8;

    /** The Constant REQ_SET_CONFIGURATION. */
    public static final int REQ_SET_CONFIGURATION = 9;

    /** The Constant REQ_GET_INTERFACE. */
    public static final int REQ_GET_INTERFACE = 10;

    /** The Constant REQ_SET_INTERFACE. */
    public static final int REQ_SET_INTERFACE = 11;

    /** The Constant REQ_SYNCH_FRAME. */
    public static final int REQ_SYNCH_FRAME = 12;

    /** The Constant REQ_TYPE_DIR_HOST_TO_DEVICE. */
    public static final int REQ_TYPE_DIR_HOST_TO_DEVICE = 0;

    /** The Constant REQ_TYPE_DIR_DEVICE_TO_HOST. */
    public static final int REQ_TYPE_DIR_DEVICE_TO_HOST = 128;

    /** The Constant REQ_TYPE_TYPE_STANDARD. */
    public static final int REQ_TYPE_TYPE_STANDARD = 0;

    /** The Constant REQ_TYPE_TYPE_CLASS. */
    public static final int REQ_TYPE_TYPE_CLASS = 32;

    /** The Constant REQ_TYPE_TYPE_VENDOR. */
    public static final int REQ_TYPE_TYPE_VENDOR = 64;

    /** The Constant REQ_TYPE_TYPE_RESERVED. */
    public static final int REQ_TYPE_TYPE_RESERVED = 96;

    /** The Constant REQ_TYPE_RECIP_DEVICE. */
    public static final int REQ_TYPE_RECIP_DEVICE = 0;

    /** The Constant REQ_TYPE_RECIP_INTERFACE. */
    public static final int REQ_TYPE_RECIP_INTERFACE = 1;

    /** The Constant REQ_TYPE_RECIP_ENDPOINT. */
    public static final int REQ_TYPE_RECIP_ENDPOINT = 2;

    /** The Constant REQ_TYPE_RECIP_OTHER. */
    public static final int REQ_TYPE_RECIP_OTHER = 3;

    /**
     * Bulk read.
     * 
     * @param usbDevice the usb device
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the int
     */
    public int bulkRead(long usbDevice, int ep, byte[] bytes, int size, int timeout);

    /**
     * Bulk write.
     * 
     * @param usbDevice the usb device
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the int
     */
    public int bulkWrite(long usbDevice, int ep, byte[] bytes, int size, int timeout);

    /**
     * Claim interface.
     * 
     * @param usbDevice the usb device
     * @param interface_ the interface_
     * @return the int
     */
    public int claimInterface(long usbDevice, int interface_);

    /**
     * Clear halt.
     * 
     * @param usbDevice the usb device
     * @param ep the ep
     * @return the int
     */
    public int clearHalt(long usbDevice, int ep);

    /**
     * Close.
     * 
     * @param usbDevice the usb device
     * @return the int
     */
    public int close(long usbDevice);

    /**
     * Control msg.
     * 
     * @param usbDevice the usb device
     * @param requesttype the requesttype
     * @param request the request
     * @param value the value
     * @param index the index
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the int
     */
    public int controlMsg(long usbDevice, int requesttype, int request, int value, int index, byte[] bytes, int size, int timeout);

    /**
     * Gets the descriptor.
     * 
     * @param usbDevice the usb device
     * @param type the type
     * @param index the index
     * @param size the size
     * @return the descriptor
     */
    public String getDescriptor(long usbDevice, byte type, byte index, int size);

    /**
     * Gets the descriptor by endpoint.
     * 
     * @param usbDevice the usb device
     * @param ep the ep
     * @param type the type
     * @param index the index
     * @param size the size
     * @return the descriptor by endpoint
     */
    public String getDescriptorByEndpoint(long usbDevice, int ep, byte type, byte index, int size);

    /**
     * Gets the string.
     * 
     * @param usbDevice the usb device
     * @param index the index
     * @param langid the langid
     * @return the string
     */
    public String getString(long usbDevice, int index, int langid);

    /**
     * Gets the string simple.
     * 
     * @param usbDevice the usb device
     * @param index the index
     * @return the string simple
     */
    public String getStringSimple(long usbDevice, int index);

    /**
     * Interrupt read.
     * 
     * @param usbDevice the usb device
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the int
     */
    public int interruptRead(long usbDevice, int ep, byte[] bytes, int size, int timeout);

    /**
     * Interrupt write.
     * 
     * @param usbDevice the usb device
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the int
     */
    public int interruptWrite(long usbDevice, int ep, byte[] bytes, int size, int timeout);

    /**
     * Open.
     * 
     * @param usbDevice the usb device
     * @return a handle used in future communication with the device. 0 if an error has occurred.
     */
    public long open(UsbDevice usbDevice);

    /**
     * Release interface.
     * 
     * @param usbDevice the usb device
     * @param interface_ the interface_
     * @return the int
     */
    public int releaseInterface(long usbDevice, int interface_);

    /**
     * Sets the altinterface.
     * 
     * @param usbDevice the usb device
     * @param alternate the alternate
     * @return the int
     */
    public int setAltinterface(long usbDevice, int alternate);

    /**
     * Sets the configuration.
     * 
     * @param usbDevice the usb device
     * @param configuration the configuration
     * @return the int
     */
    public int setConfiguration(long usbDevice, int configuration);

    /**
     * Sets the debug.
     * 
     * @param level the new debug
     */
    public void setDebug(int level);

    /**
     * Strerror.
     * 
     * @return the string
     */
    public String strerror();

    /**
     * Usb reset.
     * 
     * @param usbDevice the usb device
     * @return the int
     */
    public int usbReset(long usbDevice);
}
