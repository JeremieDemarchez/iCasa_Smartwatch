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
package org.apache.felix.usb.basedriver.descriptor;

import java.util.Dictionary;

// TODO: Auto-generated Javadoc
/**
 * The Interface UsbDevice.
 * 
 */

public interface UsbDevice {

    /** The Constant VENDOR_ID. */
    public static final String VENDOR_ID = "org.usb.vendor.id";

    /** The Constant PRODUCT_ID. */
    public static final String PRODUCT_ID = "org.usb.product.id";

    /** The Constant DEV_NUM. */
    public static final String DEV_NUM = "org.usb.dev.num";

    /** The Constant FILE_NAME. */
    public static final String FILE_NAME = "org.usb.file.name";

    /** The Constant USB_DEVICE_DESCRIPTOR. */
    public static final String USB_DEVICE_DESCRIPTOR = "org.usb.device.descriptor";

    /** The Constant USB. */
    public static final String USB = "usb";

    /** The Constant CLASS. */
    public static final String CLASS = "org.usb.interface.class";

    /** The Constant SUB_CLASS. */
    public static final String SUB_CLASS = "org.usb.interface.sub.class";

    /** The Constant PROTOCOL. */
    public static final String PROTOCOL = "org.usb.interface.protocol";

    /** The Constant MANUFACTURER. */
    public static final String MANUFACTURER = "org.usb.device.manufacturer";

    /** The Constant SERIAL. */
    public static final String SERIAL = "org.usb.device.serial.number";

    /** The Constant USB_CLASS_DESCRIPTION. */
    public static final String USB_CLASS_DESCRIPTION = "org.usb.interface.class.description";

    /** The MATCH of product id. */
    public int MATCH_PRODUCT_ID = 10;

    /** The MATCH of vendor id. */
    public int MATCH_VENDOR_ID = 2;

    /** The MATCH of interface (class, sub_class and protocol). */
    public int MATCH_INTERFACE = 6;

    /**
     * Gets the filename.
     * 
     * @return the filename
     */
    public String getFilename();

    /**
     * Gets the handle.
     * 
     * @return the handles
     */
    public Long getHandle();

    /**
     * Gets the lib usb device descriptor.
     * 
     * @return the libUsbDeviceDescriptor
     */
    public Object getLibUsbDeviceDescriptor();

    /**
     * Gets the device number.
     * 
     * @return the device number
     */
    public byte getDevNum();

    /**
     * Gets the product id.
     * 
     * @return the productId
     */
    public short getProductId();

    /**
     * Gets the vendor id.
     * 
     * @return the vendorId
     */
    public short getVendorId();

    /**
     * Gets the device class.
     * 
     * @return the device class
     */
    public Dictionary<Integer, Byte> getInterfaceClass();

    /**
     * Gets the device sub class.
     * 
     * @return the device sub class
     */
    public Dictionary<Integer, Byte> getInterfaceSubClass();

    /**
     * Gets the device protocol.
     * 
     * @return the device protocol
     */
    public Dictionary<Integer, Byte> getInterfaceProtocol();

    /**
     * Gets the manufacturer.
     * 
     * @return the manufacturer
     */
    public byte getManufacturer();

    /**
     * Gets the serial.
     * 
     * @return the serial
     */
    public byte getSerial();

    /**
     * Gets the device class description.
     * 
     * @return the device class description
     */
    public Dictionary<Integer, String> getInterfaceClassDescription();

    /**
     * Bulk read.
     * 
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return true, if successful
     */
    public boolean bulkRead(int ep, byte[] bytes, int size, int timeout);

    /**
     * Bulk write.
     * 
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return true, if successful
     */
    public boolean bulkWrite(int ep, byte[] bytes, int size, int timeout);

    /**
     * Claim interface.
     * 
     * @param interface_ the interface_
     * @return true, if successful
     */
    public boolean claimInterface(int interface_);

    /**
     * Clear halt.
     * 
     * @param ep the ep
     * @return true, if successful
     */
    public boolean clearHalt(int ep);

    /**
     * Control msg.
     * 
     * @param requesttype the requesttype
     * @param request the request
     * @param value the value
     * @param index the index
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the number of bytes written/read or < 0 on error.
     */
    public int controlMsg(int requesttype, int request, int value, int index, byte[] bytes, int size, int timeout);

    /**
     * Gets the descriptor.
     * 
     * @param type the type
     * @param index the index
     * @param size number of characters which will be retrieved (the length of the resulting String)
     * @return the descriptor String or null
     */
    public String getDescriptor(byte type, byte index, int size);

    /**
     * Gets the descriptor by endpoint.
     * 
     * @param ep the ep
     * @param type the type
     * @param index the index
     * @param size the size
     * @return the descriptor by endpoint
     */
    public String getDescriptorByEndpoint(int ep, byte type, byte index, int size);

    /**
     * Gets the string.
     * 
     * @param index the index
     * @param langid the langid
     * @return the string
     */
    public String getString(int index, int langid);

    /**
     * Gets the string simple.
     * 
     * @param index the index
     * @return the string simple
     */
    public String getStringSimple(int index);

    /**
     * Interrupt read.
     * 
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the number of bytes read on success or < 0 on error.
     */
    public int interruptRead(int ep, byte[] bytes, int size, int timeout);

    /**
     * Interrupt write.
     * 
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the number of bytes written on success or < 0 on error.
     */
    public int interruptWrite(int ep, byte[] bytes, int size, int timeout);

    /**
     * Release interface.
     * 
     * @param interface_ the interface_
     * @return true, if successful
     */
    public boolean releaseInterface(int interface_);

    /**
     * Sets the altinterface.
     * 
     * @param alternate the alternate
     * @return true, if successful
     */
    public boolean setAltinterface(int alternate);

    /**
     * Sets the configuration.
     * 
     * @param configuration the configuration
     * @return true, if successful
     */
    public boolean setConfiguration(int configuration);

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
     * @return true, if successful
     */
    public boolean usbReset();

}