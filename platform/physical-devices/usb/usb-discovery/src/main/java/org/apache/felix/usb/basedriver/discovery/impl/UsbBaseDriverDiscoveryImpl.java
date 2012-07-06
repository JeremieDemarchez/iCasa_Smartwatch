/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
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
package org.apache.felix.usb.basedriver.discovery.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.usb.basedriver.descriptor.UsbDevice;
import org.apache.felix.usb.basedriver.discovery.UsbBaseDriverDiscovery;
import org.apache.felix.usb.basedriver.tools.UsbClassUtils;
import org.apache.felix.usb.basedriver.tools.Utils;
//import org.apache.log4j.Logger;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.Usb_Bus;
import ch.ntb.usb.Usb_Config_Descriptor;
import ch.ntb.usb.Usb_Device;
import ch.ntb.usb.Usb_Device_Descriptor;
import ch.ntb.usb.Usb_Interface;
import ch.ntb.usb.Usb_Interface_Descriptor;

/**
 * The Class UsbBaseDriverDiscoveryImpl.
 * 
 * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers
 */
public class UsbBaseDriverDiscoveryImpl implements UsbBaseDriverDiscovery, Runnable {

    /** The all usb device. */
    private ConcurrentMap<String, ComponentInstance> allUsbDevice = null;

    /** The usb device factory. */
    private Factory usbDeviceFactory;

    /** The timer. */
    private ScheduledThreadPoolExecutor timer;

    /** The sched future. */
    private ScheduledFuture<?> schedFuture;

    /** The libusb java_debug. */
    private int libusbJava_debug;

    /** The logger. */
//    private Logger logger = Logger.getLogger(UsbBaseDriverDiscoveryImpl.class.getName());

    /** The changes. */
    private int changes = 0;

    /**
     * Check disapear device.
     * 
     * @param currentDevice the current device
     */
    private void checkDisapearDevice(Set<String> currentDevice) {
        Iterator<String> iterator = this.allUsbDevice.keySet().iterator();
        int tempChange = this.changes;
        while ((tempChange > 0) && iterator.hasNext()) {
            String filename = iterator.next();

            if (!currentDevice.contains(filename)) {
                ComponentInstance compInst = this.allUsbDevice.remove(filename);
                this.allUsbDevice.keySet().remove(filename);
                removeDevice(compInst);
                tempChange--;
            }
        }
    }

    /**
     * Removes the device.
     * 
     * @param compInst the comp inst
     */
    private void removeDevice(ComponentInstance compInst) {
        compInst.dispose();
    }

    /**
     * Find devices.
     * 
     * @param vendorID the vendor id
     * @param productID the product id
     * @return the map
     * @see org.apache.felix.usb.basedriver.bridge.driver.UsbBridgeDrivers#findDevices(short, short)
     */
    public Map<String, UsbDevice> findDevices(short vendorID, short productID) {
        Map<String, UsbDevice> devicesTemp = new HashMap<String, UsbDevice>();
        for (String idUsbDevice : this.allUsbDevice.keySet()) {
            UsbDevice usbDevice = (UsbDevice) ((InstanceManager) this.allUsbDevice.get(idUsbDevice)).getPojoObject();
            long tempVdId = usbDevice.getVendorId();
            long tempPrdId = usbDevice.getProductId();
            if ((tempVdId == vendorID) && (tempPrdId == productID)) {
                devicesTemp.put(idUsbDevice, usbDevice);
            }
        }
        return devicesTemp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.felix.usb.basedriver.discovery.UsbBaseDriverDiscovery#getDevice(java.lang.String)
     */
    public UsbDevice getDevice(String id) {
        UsbDevice usbDevice = null;
        ComponentInstance compInstance = this.allUsbDevice.get(id);
        if (compInstance != null) {
            usbDevice = (UsbDevice) ((InstanceManager) compInstance).getPojoObject();
        }
        return usbDevice;
    }

    /**
     * Initialzing.
     */
    private void initialzing() {
        LibusbJava.usb_init();
        // pour le debogage
        LibusbJava.usb_set_debug(0);
        LibusbJava.usb_find_busses();
        LibusbJava.usb_find_devices();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        updateListDevice();

    }

    /**
     * Start.
     */
    public void start() {
        // if you don't use the ch.ntb.usb.Device class you must initialise
        // Libusb before use Device device ;
        initialzing();
        this.allUsbDevice = new ConcurrentHashMap<String, ComponentInstance>();
        updateListDevice();
//        this.logger.info(" <-> Usb BaseDriver Discovery started (" + this.allUsbDevice.size() + " devices) <-> ");
        this.timer = new ScheduledThreadPoolExecutor(1);
        this.schedFuture = this.timer.scheduleWithFixedDelay(this, 2, 2, TimeUnit.SECONDS);
    }

    /**
     * Stop.
     */
    public void stop() {
        this.schedFuture.cancel(false);
        this.timer.purge();
//        this.logger.info(" <-> Usb BaseDriver Discovery stopped <-> ");
    }

    /**
     * Update list device.
     */
    private synchronized void updateListDevice() {
        this.changes = LibusbJava.usb_find_devices();
        Usb_Device libUsbDevicetemp = null;
        Usb_Bus busTemp = LibusbJava.usb_get_busses();
        // UsbDeviceImpl deviceDescriptor = null;
        Set<String> currentDevice = new HashSet<String>();
        while (busTemp != null) {
            libUsbDevicetemp = busTemp.getDevices();
            while (libUsbDevicetemp != null) {
                currentDevice.add(libUsbDevicetemp.getFilename());
                if (!this.allUsbDevice.keySet().contains(libUsbDevicetemp.getFilename())) {
                    // if the device is unknown, add it to the list
                    addNewUsbDevice(libUsbDevicetemp);
                }

                libUsbDevicetemp = libUsbDevicetemp.getNext();
            }
            busTemp = busTemp.getNext();
        }

        checkDisapearDevice(currentDevice);
//        this.logger.debug(" : " + this.allUsbDevice.size() + " devices found.");
    }

    // private void createEndpoint(Map<String, Object> properties) {
    //
    // EndpointDescription ep = new EndpointDescription(properties);
    // this.registryProvisioning.put(tempProp.getFilename(), tempProp);
    //
    // }

    /**
     * Adds the new usb device.
     * 
     * @param device the device
     */
    private void addNewUsbDevice(Usb_Device device) {
        Usb_Device_Descriptor descriptor = device.getDescriptor();
        Dictionary<String, Object> tempProp = new Hashtable<String, Object>();
        tempProp.put(UsbDevice.VENDOR_ID, descriptor.getIdVendor());
        tempProp.put(UsbDevice.PRODUCT_ID, descriptor.getIdProduct());
        tempProp.put(UsbDevice.DEV_NUM, device.getDevnum());
        tempProp.put(UsbDevice.FILE_NAME, device.getFilename());
        tempProp.put(UsbDevice.MANUFACTURER, descriptor.getIManufacturer());
        tempProp.put(UsbDevice.SERIAL, descriptor.getISerialNumber());
        tempProp.put(UsbDevice.USB_DEVICE_DESCRIPTOR, descriptor);
        addInterfaceDescriptor(device, tempProp);
//        this.logger.debug("class= " + Integer.toHexString(descriptor.getBDeviceClass()));
        tempProp.put("instance.name", "device:" + Utils.toCompleteHex(descriptor.getIdVendor()) + ":" + Utils.toCompleteHex(descriptor.getIdProduct()) + ":"
                + device.getDevnum());
        ComponentInstance compIns;
        try {
            compIns = this.usbDeviceFactory.createComponentInstance(tempProp);
            this.allUsbDevice.put(device.getFilename(), compIns);
        } catch (UnacceptableConfiguration e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MissingHandlerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Adds the interface descriptor.
     * 
     * @param device the device
     * @param properties the properties
     */
    private void addInterfaceDescriptor(Usb_Device device, Dictionary<String, Object> properties) {
        Usb_Config_Descriptor[] configs = device.getConfig();

        Dictionary<Integer, Byte> classProp = new Hashtable<Integer, Byte>();
        Dictionary<Integer, Byte> subClassProp = new Hashtable<Integer, Byte>();
        Dictionary<Integer, Byte> protocolProp = new Hashtable<Integer, Byte>();
        Dictionary<Integer, String> classDesProp = new Hashtable<Integer, String>();

        properties.put(UsbDevice.CLASS, classProp);
        properties.put(UsbDevice.SUB_CLASS, subClassProp);
        properties.put(UsbDevice.PROTOCOL, protocolProp);
        properties.put(UsbDevice.USB_CLASS_DESCRIPTION, classDesProp);

        for (Usb_Config_Descriptor usb_Config_Descriptor : configs) {
            Usb_Interface[] interfaces = usb_Config_Descriptor.getInterface();
            for (Usb_Interface usb_Interface : interfaces) {
                Usb_Interface_Descriptor[] interfaces_Descriptor = usb_Interface.getAltsetting();
                for (Usb_Interface_Descriptor usb_Interface_Descriptor : interfaces_Descriptor) {
                    byte interfaceClass = usb_Interface_Descriptor.getBInterfaceClass();
                    byte interfaceSubClass = usb_Interface_Descriptor.getBInterfaceSubClass();
                    byte interfaceProtocol = usb_Interface_Descriptor.getBInterfaceProtocol();
                    int interfaceIndex = usb_Interface_Descriptor.getIInterface();
                    String interfaceDescription = UsbClassUtils.getUsbClassDescription(interfaceClass, interfaceSubClass, interfaceProtocol);
                    classProp.put(interfaceIndex, interfaceClass);
                    subClassProp.put(interfaceIndex, interfaceSubClass);
                    protocolProp.put(interfaceIndex, interfaceProtocol);
                    classDesProp.put(interfaceIndex, interfaceDescription);
                }
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.felix.usb.basedriver.discovery.UsbBaseDriverDiscovery#listDevices()
     */
    public Map<String, UsbDevice> listDevices() {
        Map<String, UsbDevice> devices = new HashMap<String, UsbDevice>();
        for (String filename : this.allUsbDevice.keySet()) {
            ComponentInstance compInstance = this.allUsbDevice.get(filename);
            UsbDevice usbDevice = (UsbDevice) ((InstanceManager) compInstance).getPojoObject();
            devices.put(compInstance.getInstanceName(), usbDevice);
        }

        return devices;
    }

}
