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
package fr.liglab.adele.icasa.device;

import fr.liglab.adele.icasa.device.util.AbstractDevice;

/**
 * Generic interface that is intended to be used as a skeleton for device
 * service interface definitions.
 * 
 * @see AbstractDevice
 * @author bourretp
 */
public interface GenericDevice {

    /**
     * Service property indicating the hardware serial number of the device.
     * 
     * <ul>
     * <li>This property is <b>mandatory</b></li>
     * <li>Type of values : <b><code>java.lang.String</code></b></li>
     * <li>Description : the hardware serial number of the device. Must be
     * unique and immutable.</li>
     * </ul>
     * 
     * @see #getSerialNumber()
     */
    String DEVICE_SERIAL_NUMBER = "device.serialNumber";

    /**
     * Return the serial number of the device.
     * 
     * @return the serial number of the device.
     * @see #DEVICE_SERIAL_NUMBER
     */
    String getSerialNumber();

    /**
     * Add the specified listener to the list of this device's listeners. If the
     * listener is already is the list, this method does nothing.
     * 
     * @param listener
     *            the listener to add
     */
    void addListener(DeviceListener listener);

    /**
     * Remove the specified listener from the list of this device's listeners.
     * If the listener wasn't in the list, this method does nothing.
     * 
     * @param listener
     *            the listener to remove
     */
    void removeListener(DeviceListener listener);
    
	public static String STATE_PROPERTY_NAME = "state";
	public static String STATE_ACTIVATED = "activated";
	public static String STATE_DEACTIVATED = "deactivated";
	
	public static String FAULT_PROPERTY_NAME = "fault";
	public static String FAULT_YES = "yes";
	public static String FAULT_NO = "no";
	
	
	public String getState();
	public void setState(String state);
	
	public String getFault();
	public void setFault(String fault);
	
    String getLocation();

}
