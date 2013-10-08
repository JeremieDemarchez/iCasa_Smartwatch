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
package org.apache.felix.usb.basedriver.descriptor.impl;

import java.util.Dictionary;

import org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers;
import org.apache.felix.usb.basedriver.descriptor.UsbDevice;
//import org.apache.log4j.Logger;
import org.osgi.service.device.Device;

import ch.ntb.usb.Usb_Device_Descriptor;

/**
 * TODO comment the methods and the fields TODO set private the fields.
 * 
 * 
 */
public class UsbDeviceImpl implements UsbDevice, Device {

    /** The vendor id. */
    private short vendorId;

    /** The product id. */
    private short productId;

    /** The handle. */
    private Long handle;

    /** The lib usb device descriptor. */
    private Object usbDeviceDescriptor;

    /** The filename. */
    private String filename;

    /** The devnum. */
    private byte devNum;

    /** The interface class. */
    private Dictionary<Integer, Byte> interfaceClass;

    /** The interface sub class. */
    private Dictionary<Integer, Byte> interfaceSubClass;

    /** The interface protocol. */
    private Dictionary<Integer, Byte> interfaceProtocol;

    /** The device manufacturer. */
    private byte manufacturer;

    /** The device SN. */
    private byte serial;

    /** the interface class description. */
    private Dictionary<Integer, String> interfaceClassDescription;

    /** The usb bridge. */
    private UsbBridgeDrivers usbBridge;

    /** The logger. */
//    private Logger logger = Logger.getLogger(UsbDevice.class);

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UsbDeviceImpl)) {
            return false;
        }
        UsbDeviceImpl other = (UsbDeviceImpl) obj;
        if (this.devNum != other.devNum) {
            return false;
        }
        if (this.filename == null) {
            if (other.filename != null) {
                return false;
            }
        } else if (!this.filename.equals(other.filename)) {
            return false;
        }
        if (this.productId != other.productId) {
            return false;
        }
        if (this.vendorId != other.vendorId) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getFilename()
     */
    public String getFilename() {
        return this.filename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getHandle()
     */
    public Long getHandle() {
        return this.handle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getLibUsbDeviceDescriptor()
     */
    public Object getLibUsbDeviceDescriptor() {
        return this.usbDeviceDescriptor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getNum()
     */
    public byte getDevNum() {
        return this.devNum;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getProductId()
     */
    public short getProductId() {
        return this.productId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getVendorId()
     */
    public short getVendorId() {
        return this.vendorId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.devNum;
        result = prime * result + ((this.filename == null) ? 0 : this.filename.hashCode());
        result = prime * result + this.productId;
        result = prime * result + this.vendorId;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "	Devnum = " + this.devNum + "\n	" + ((Usb_Device_Descriptor) this.usbDeviceDescriptor).toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#bulkRead(int, byte[], int, int)
     */
    public boolean bulkRead(int ep, byte[] bytes, int size, int timeout) {
        if (this.handle != null) {
            return (this.usbBridge.bulkRead(this.handle, ep, bytes, size, timeout) == 0);
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#bulkWrite(int, byte[], int, int)
     */
    public boolean bulkWrite(int ep, byte[] bytes, int size, int timeout) {
        if (this.handle != null) {
            return (this.usbBridge.bulkWrite(this.handle, ep, bytes, size, timeout) == 0);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#claimInterface(int)
     */
    public boolean claimInterface(int interface_) {
        if (this.handle != null) {
            return (this.usbBridge.claimInterface(this.handle, interface_) == 0);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#clearHalt(int)
     */
    public boolean clearHalt(int ep) {
        if (this.handle != null) {
            return (this.usbBridge.clearHalt(this.handle, ep) == 0);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#close()
     */
    /**
     * Close.
     * 
     * @return true, if successful
     */
    public boolean close() {
        if (this.handle != null) {
            return (this.usbBridge.close(this.handle) == 0);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#controlMsg(int, int, int, int, byte[], int,
     * int)
     */
    public int controlMsg(int requesttype, int request, int value, int index, byte[] bytes, int size, int timeout) {
        if (this.handle != null) {
            return this.usbBridge.controlMsg(this.handle, requesttype, request, value, index, bytes, size, timeout);
        }
        return -2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getDescriptor(byte, byte, int)
     */
    public String getDescriptor(byte type, byte index, int size) {
        if (this.handle != null) {
            return this.usbBridge.getDescriptor(this.handle, type, index, size);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getDescriptorByEndpoint(int, byte, byte, int)
     */
    public String getDescriptorByEndpoint(int ep, byte type, byte index, int size) {
        if (this.handle != null) {
            return this.usbBridge.getDescriptorByEndpoint(this.handle, ep, type, index, size);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getString(int, int)
     */
    public String getString(int index, int langid) {
        if (this.handle != null) {
            return this.usbBridge.getString(this.handle, index, langid);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#getStringSimple(int)
     */
    public String getStringSimple(int index) {
        if (this.handle != null) {
            return this.usbBridge.getStringSimple(this.handle, index);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.felix.usb.basedriver.descriptor.UsbDevice#getInterfaceClass()
     */
    public Dictionary<Integer, Byte> getInterfaceClass() {
        return this.interfaceClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.felix.usb.basedriver.descriptor.UsbDevice#getInterfaceSubClass()
     */
    public Dictionary<Integer, Byte> getInterfaceSubClass() {
        return this.interfaceSubClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.felix.usb.basedriver.descriptor.UsbDevice#getInterfaceProtocol()
     */
    public Dictionary<Integer, Byte> getInterfaceProtocol() {
        return this.interfaceProtocol;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.felix.usb.basedriver.descriptor.UsbDevice#getManufacturer()
     */
    public byte getManufacturer() {
        return this.manufacturer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.felix.usb.basedriver.descriptor.UsbDevice#getSerial()
     */
    public byte getSerial() {
        return this.serial;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.felix.usb.basedriver.descriptor.UsbDevice#getInterfaceClassDescription()
     */
    public Dictionary<Integer, String> getInterfaceClassDescription() {
        return this.interfaceClassDescription;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#interruptRead(int, byte[], int, int)
     */
    public int interruptRead(int ep, byte[] bytes, int size, int timeout) {
        if (this.handle != null) {
            return this.usbBridge.interruptRead(this.handle, ep, bytes, size, timeout);
        }
        return -2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#interruptWrite(int, byte[], int, int)
     */
    public int interruptWrite(int ep, byte[] bytes, int size, int timeout) {
        if (this.handle != null) {
            return this.usbBridge.interruptWrite(this.handle, ep, bytes, size, timeout);
        }
        return -2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#open()
     */
    /**
     * Open.
     * 
     * @return true, if successful
     */
    public boolean open() {
        this.handle = this.usbBridge.open(this);
        if (this.handle != 0) {
            return true;
        }
        this.handle = null;
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#releaseInterface(int)
     */
    public boolean releaseInterface(int interface_) {
        if (this.handle != null) {
            return (this.usbBridge.releaseInterface(this.handle, interface_) == 0);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#setAltinterface(int)
     */
    public boolean setAltinterface(int alternate) {
        if (this.handle != null) {
            return (this.usbBridge.setAltinterface(this.handle, alternate) == 0);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#setConfiguration(int)
     */
    public boolean setConfiguration(int configuration) {
        if (this.handle != null) {
            return (this.usbBridge.setConfiguration(this.handle, configuration) == 0);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#setDebug(int)
     */
    public void setDebug(int level) {
        this.usbBridge.setDebug(level);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#strerror()
     */
    public String strerror() {
        return this.usbBridge.strerror();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ow2.chameleon.usb.descriptor.UsbDevice#usbReset()
     */
    public boolean usbReset() {
        if (this.handle != null) {
            if (this.usbBridge.usbReset(this.handle) == 0) {
                open();
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.service.device.Device#noDriverFound()
     */
    public void noDriverFound() {
        // TODO check the OBR or other repositories or show info message
        System.out.println("DRIVER NOT FOUND BY DEVICE ACCESS...SEARCHING THE DRIVER ....NOT YET IMPLEMENTED");
    }

    /**
     * Start.
     */
    public void start() {
        boolean state = open();
//        this.logger.debug("start device " + this.usbDeviceDescriptor + " -->>" + state);
    }

    /**
     * Stop.
     */
    public void stop() {
        boolean state = close();
//        this.logger.debug("stop device " + this.usbDeviceDescriptor + " -->>" + state);
    }
}
