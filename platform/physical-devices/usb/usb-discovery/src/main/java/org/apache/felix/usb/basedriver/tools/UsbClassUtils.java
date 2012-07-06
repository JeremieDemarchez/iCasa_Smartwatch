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
package org.apache.felix.usb.basedriver.tools;

/**
 * The Class UsbUtils.
 * 
 * @see www.usb.org
 */
public class UsbClassUtils {

    /** Speakers, I/O devices, etc. */
    public static final String AUDIO = "org.usb.audio";

    /** Communication devices (Modems). */
    public static final String COMM = "org.usb.comm";

    /** Generic HID devices like Joysticks, UPS, etc. */
    public static final String HID = "org.usb.hid";

    /** Physical Device. */
    public static final String PHYSICAL = "org.usb.physical";

    /** Digital Imaging devices. */
    public static final String IMAGE = "org.usb.image";

    /** Printer devices. */
    public static final String PRINTER = "org.usb.printer";

    /** Storage Mass storage devices (ZIP drive, Harddisk, etc.) show */
    public static final String MASS = "org.usb.mass";

    /** Hub devices (integrated and standalone). */
    public static final String HUB = "org.usb.hub";

    /** Full speed Hub. */
    public static final String HUB_FULL_SPEED = "org.usb.hub.full.speed";

    /** Full speed Hub with Single TT. */
    public static final String HUB_FULL_SPEED_SINGLE_TT = "org.usb.hub.full.speed.single.tt";

    /** Full speed Hub with multiple tts. */
    public static final String HUB_FULL_SPEED_MULTI_TT = "org.usb.hub.full.speed.multi.tt";

    /** CDC data device. */
    public static final String CDC = "org.usb.cdc";

    /** Smart Card device. */
    public static final String SMART_CARD = "org.usb.smart.card";

    /** Content Security. */
    public static final String CONTENT_SECURITY = "org.usb.content.security";

    /** Video device. */
    public static final String VIDEO = "org.usb.video";

    /** Personal Healthcare device. */
    public static final String PERSONAL_HEALTHCARE = "org.usb.personal.healthcare";

    /** Diagnostic Device. */
    public static final String DIAGNOSTIC = "org.usb.diagnostic";

    /** Wireless Controller. */
    public static final String WIRELESS_CONTROLLER = "org.usb.wireless.controller";

    /** Miscellaneous. */
    public static final String MISCELLANEOUS = "org.usb.miscellaneous";

    /** Application Specific. */
    public static final String APPLICATION_SPECIFIC = "org.usb.application.specific";

    /** Vendor specific devices that don't fall under a certain category. */
    public static final String VENDOR = "org.usb.vendor";

    /**
     * Gets the usb class.
     * 
     * @param deviceClass the device class
     * @param deviceSubClass the device sub class
     * @param deviceProtocol the device protocol
     * @return the usb class
     */
    public static String getUsbClassDescription(byte deviceClass, byte deviceSubClass, byte deviceProtocol) {
        String deviceClassDescription = "";
        switch (deviceClass) {
            case ((byte) 0x00):

                break;
            case ((byte) 0x01):
                deviceClassDescription = UsbClassUtils.AUDIO;
                break;

            case ((byte) 0x02):
                deviceClassDescription = UsbClassUtils.COMM;
                break;

            case ((byte) 0x03):
                deviceClassDescription = UsbClassUtils.HID;
                break;

            case ((byte) 0x05):
                deviceClassDescription = UsbClassUtils.PHYSICAL;
                break;

            case ((byte) 0x06):
                if ((deviceClass == 0x01) && (deviceProtocol == 0x01)) {
                    deviceClassDescription = UsbClassUtils.IMAGE;
                }
                break;

            case ((byte) 0x07):
                deviceClassDescription = UsbClassUtils.PRINTER;
                break;

            case ((byte) 0x08):
                deviceClassDescription = UsbClassUtils.MASS;
                break;

            case ((byte) 0x09):
                deviceClassDescription = UsbClassUtils.HUB;
                if (deviceSubClass == 0x00) {
                    switch (deviceProtocol) {
                        case 0x01:
                            deviceClassDescription = UsbClassUtils.HUB_FULL_SPEED;
                            break;
                        case 0x02:
                            deviceClassDescription = UsbClassUtils.HUB_FULL_SPEED_SINGLE_TT;
                            break;
                        case 0x03:
                            deviceClassDescription = UsbClassUtils.HUB_FULL_SPEED_MULTI_TT;
                            break;
                    }
                }
                break;

            case ((byte) 0x0A):
                deviceClassDescription = UsbClassUtils.CDC;
                break;

            case ((byte) 0x0B):
                deviceClassDescription = UsbClassUtils.SMART_CARD;
                break;

            case ((byte) 0x0D):
                deviceClassDescription = UsbClassUtils.CONTENT_SECURITY;
                break;

            case ((byte) 0x0E):
                deviceClassDescription = UsbClassUtils.VIDEO;
                break;

            case ((byte) 0x0F):
                deviceClassDescription = UsbClassUtils.PERSONAL_HEALTHCARE;
                break;

            case ((byte) 0xDC):
                deviceClassDescription = UsbClassUtils.DIAGNOSTIC;
                break;

            case ((byte) 0xE0):
                deviceClassDescription = UsbClassUtils.WIRELESS_CONTROLLER;
                switch (deviceSubClass) {
                    case 0x01:
                        switch (deviceProtocol) {
                            case 0x01:
                                deviceClassDescription += " : Bluetooth";
                                break;
                            case 0x02:
                                deviceClassDescription += " : UWB Radio Control Interface";
                                break;
                            case 0x03:
                                deviceClassDescription += " : Remote NDIS";
                                break;
                            case 0x04:
                                deviceClassDescription += " : Bluetooth AMP Controller";
                                break;
                        }
                        break;

                    case 0x02:
                        switch (deviceProtocol) {
                            case 0x01:
                                deviceClassDescription += " : Host Wire Adapter Control/Data interface";
                                break;
                            case 0x02:
                                deviceClassDescription += " : Device Wire Adapter Control/Data interface";
                                break;
                            case 0x03:
                                deviceClassDescription += " : Device Wire Adapter Isochronous interface";
                                break;
                        }
                        break;
                }
                break;

            case ((byte) 0xEF):
                deviceClassDescription = UsbClassUtils.MISCELLANEOUS;
                switch (deviceSubClass) {
                    case 0x01:
                        switch (deviceProtocol) {
                            case 0x01:
                                deviceClassDescription += " : Active Sync device";
                                break;
                            case 0x02:
                                deviceClassDescription += " : Palm Sync";
                                break;
                        }
                        break;

                    case 0x02:
                        switch (deviceProtocol) {
                            case 0x01:
                                deviceClassDescription += " : Interface Association Descriptor";
                                break;
                            case 0x02:
                                deviceClassDescription += " : Wire Adapter Multifunction Peripheral programming interface";
                                break;

                        }
                        break;
                    case 0x03:
                        if (deviceProtocol == 0x01) {
                            deviceClassDescription += " : Cable Based Association Framework";
                        }
                        break;
                }
                break;

            case ((byte) 0xFE):
                deviceClassDescription = UsbClassUtils.MISCELLANEOUS;
                switch (deviceSubClass) {
                    case 0x01:
                        if (deviceProtocol == 0x01) {
                            deviceClassDescription += ": Device Firmware Upgrade";
                        }
                        break;
                    case 0x02:
                        if (deviceProtocol == 0x00) {
                            deviceClassDescription += ": IRDA Bridge device";
                        }
                        break;
                    case 0x03:
                        switch (deviceProtocol) {
                            case 0x00:
                                deviceClassDescription += " : USB Test and Measurement Device";
                                break;
                            case 0x01:
                                deviceClassDescription += " : USB Test and Measurement Device conforming to the USBTMC USB488";
                                break;

                        }
                        break;
                }
                break;

            case ((byte) 0xFF):
                deviceClassDescription = UsbClassUtils.VENDOR;
                break;

        }
        return deviceClassDescription;

    }
}
