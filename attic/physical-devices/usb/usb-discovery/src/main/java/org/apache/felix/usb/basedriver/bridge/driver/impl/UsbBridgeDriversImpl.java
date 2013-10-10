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
package org.apache.felix.usb.basedriver.bridge.driver.impl;

import org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers;
import org.apache.felix.usb.basedriver.descriptor.UsbDevice;
import org.apache.felix.usb.basedriver.descriptor.impl.UsbDeviceImpl;
import org.apache.felix.usb.basedriver.tools.Utils;
//import org.apache.log4j.Logger;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.Usb_Bus;
import ch.ntb.usb.Usb_Device;
import ch.ntb.usb.Usb_Device_Descriptor;

/**
 * The Class UsbBridgeDriversImpl.
 */
public class UsbBridgeDriversImpl implements UsbBridgeDrivers {

    /** The logger. */
//    private Logger logger = Logger.getLogger(UsbBridgeDriversImpl.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#bulkRead(long, int,
     * byte[], int, int)
     */
    public int bulkRead(long dev_handle, int ep, byte[] bytes, int size, int timeout) {
        return LibusbJava.usb_bulk_read(dev_handle, ep, bytes, size, timeout);
    }

    /**
     * Bulk write.
     * 
     * @param dev_handle the dev_handle
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#bulkWrite(UsbDeviceImpl,
     *      int, byte[], int, int)
     */
    public int bulkWrite(long dev_handle, int ep, byte[] bytes, int size, int timeout) {
        return LibusbJava.usb_bulk_write(dev_handle, ep, bytes, size, timeout);
    }

    /**
     * Claim interface.
     * 
     * @param dev_handle the dev_handle
     * @param interface_ the interface_
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#claimInterface(UsbDeviceImpl,
     *      int)
     */
    public int claimInterface(long dev_handle, int interface_) {
        return LibusbJava.usb_claim_interface(dev_handle, interface_);
    }

    /**
     * Clear halt.
     * 
     * @param dev_handle the dev_handle
     * @param ep the ep
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#clearHalt(UsbDeviceImpl,
     *      int)
     */
    public int clearHalt(long dev_handle, int ep) {
        return LibusbJava.usb_clear_halt(dev_handle, ep);
    }

    /**
     * Close.
     * 
     * @param dev_handle the dev_handle
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#close(UsbDeviceImpl)
     */
    public int close(long dev_handle) {
        return LibusbJava.usb_close(dev_handle);
    }

    /**
     * Control msg.
     * 
     * @param dev_handle the dev_handle
     * @param requesttype the requesttype
     * @param request the request
     * @param value the value
     * @param index the index
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#controlMsg(UsbDeviceImpl,
     *      int, int, int, int, byte[], int, int)
     */
    public int controlMsg(long dev_handle, int requesttype, int request, int value, int index, byte[] bytes, int size, int timeout) {
        return LibusbJava.usb_control_msg(dev_handle, requesttype, request, value, index, bytes, size, timeout);
    }

    /**
     * Find usb device.
     * 
     * @param vendorID the vendor id
     * @param productID the product id
     * @param filename the filename
     * @return the usb_ device
     */
    private synchronized Usb_Device findUsbDevice(short vendorID, short productID, String filename) {
        Usb_Device temp = null;
        Usb_Device device = null;
        Usb_Device_Descriptor descriptor = null;
        boolean trouve = false;
        Usb_Bus tempbus = LibusbJava.usb_get_busses();
        while (!trouve && (tempbus != null)) {
            temp = tempbus.getDevices();
            while (!trouve && (temp != null)) {
                descriptor = temp.getDescriptor();
//                this.logger.debug("USB Device : VendorID: " + Utils.toCompleteHex(descriptor.getIdVendor()) + ", ProductID: "
//                        + Utils.toCompleteHex(descriptor.getIdProduct()));

                if ((descriptor.getIdVendor() == vendorID) && (descriptor.getIdProduct() == productID) && (temp.getFilename().equals(filename))) {
                    trouve = true;
                    device = temp;
                }
                temp = temp.getNext();
            }
            tempbus = tempbus.getNext();
        }
        return device;
    }

    /**
     * Gets the descriptor.
     * 
     * @param dev_handle the dev_handle
     * @param type the type
     * @param index the index
     * @param size the size
     * @return the descriptor
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#getDevice(Long)
     */
    public String getDescriptor(long dev_handle, byte type, byte index, int size) {
        return LibusbJava.usb_get_descriptor(dev_handle, type, index, size);
    }

    /**
     * Gets the descriptor by endpoint.
     * 
     * @param dev_handle the dev_handle
     * @param ep the ep
     * @param type the type
     * @param index the index
     * @param size the size
     * @return the descriptor by endpoint
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#getDescriptorByEndpoint(UsbDeviceImpl,
     *      int, byte, byte, int)
     */
    public String getDescriptorByEndpoint(long dev_handle, int ep, byte type, byte index, int size) {
        return LibusbJava.usb_get_descriptor_by_endpoint(dev_handle, ep, type, index, size);
    }

    /**
     * Gets the string.
     * 
     * @param dev_handle the dev_handle
     * @param index the index
     * @param langid the langid
     * @return the string
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#getString(UsbDeviceImpl,
     *      int, int)
     */
    public String getString(long dev_handle, int index, int langid) {
        return LibusbJava.usb_get_string(dev_handle, index, langid);
    }

    /**
     * Gets the string simple.
     * 
     * @param dev_handle the dev_handle
     * @param index the index
     * @return the string simple
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#getStringSimple(UsbDeviceImpl,
     *      int)
     */
    public String getStringSimple(long dev_handle, int index) {
        return LibusbJava.usb_get_string_simple(dev_handle, index);
    }

    /**
     * Interrupt read.
     * 
     * @param dev_handle the dev_handle
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#interruptRead(UsbDeviceImpl,
     *      int, byte[], int, int)
     */
    public int interruptRead(long dev_handle, int ep, byte[] bytes, int size, int timeout) {
        return LibusbJava.usb_interrupt_read(dev_handle, ep, bytes, size, timeout);
    }

    /**
     * Interrupt write.
     * 
     * @param dev_handle the dev_handle
     * @param ep the ep
     * @param bytes the bytes
     * @param size the size
     * @param timeout the timeout
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#interruptWrite(UsbDeviceImpl,
     *      int, byte[], int, int)
     */
    public int interruptWrite(long dev_handle, int ep, byte[] bytes, int size, int timeout) {
        return LibusbJava.usb_interrupt_write(dev_handle, ep, bytes, size, timeout);
    }

    /**
     * Open.
     * 
     * @param usbDevice the usb device
     * @return the long
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#open(UsbDeviceImpl)
     */
    public long open(UsbDevice usbDevice) {

        Usb_Device dev = findUsbDevice(usbDevice.getVendorId(), usbDevice.getProductId(), usbDevice.getFilename());
        if (dev != null) {
            return LibusbJava.usb_open(dev);
        }
        return 0;
    }

    /**
     * Release interface.
     * 
     * @param dev_handle the dev_handle
     * @param interface_ the interface_
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#releaseInterface(UsbDeviceImpl,
     *      int)
     */
    public int releaseInterface(long dev_handle, int interface_) {
        return LibusbJava.usb_release_interface(dev_handle, interface_);
    }

    /**
     * Sets the altinterface.
     * 
     * @param dev_handle the dev_handle
     * @param alternate the alternate
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#setAltinterface(UsbDeviceImpl,
     *      int)
     */
    public int setAltinterface(long dev_handle, int alternate) {
        return LibusbJava.usb_set_altinterface(dev_handle, alternate);
    }

    /**
     * Sets the configuration.
     * 
     * @param dev_handle the dev_handle
     * @param configuration the configuration
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#setConfiguration(UsbDeviceImpl,
     *      int)
     */
    public int setConfiguration(long dev_handle, int configuration) {
        return LibusbJava.usb_set_configuration(dev_handle, configuration);
    }

    /**
     * Sets the debug.
     * 
     * @param level the new debug
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#setDebug(int)
     */
    public void setDebug(int level) {
        LibusbJava.usb_set_debug(level);
    }

    /**
     * Start.
     */
    public void start() {
//        this.logger.info(" <-> Usb Bridge Drivers Started  <-> ");

    }

    /**
     * Stop.
     */
    public void stop() {
//        this.logger.info(" <-> Usb Bridge Drivers Stopped  <-> ");
    }

    /**
     * Strerror.
     * 
     * @return the string
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#strerror()
     */
    public String strerror() {
        return LibusbJava.usb_strerror();
    }

    /**
     * Usb reset.
     * 
     * @param dev_handle the dev_handle
     * @return the int
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#usbReset(UsbDeviceImpl)
     */
    public int usbReset(long dev_handle) {
        return LibusbJava.usb_reset(dev_handle);
    }

}
