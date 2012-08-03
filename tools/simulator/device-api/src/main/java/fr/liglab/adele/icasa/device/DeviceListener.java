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

/**
 * Listener interface implemented by objects that are interested by device
 * events. In order to be notified, the listener must be registered by using the
 * {@link GenericDevice#addListener(DeviceListener)} method.
 * 
 * @author bourretp
 */
public interface DeviceListener {

    /**
     * Callback invoked when a device event occurred. Only the listeners that
     * have registered to this device will be notified.
     * 
     * @see GenericDevice#addListener(DeviceListener)
     */
    public void notifyDeviceEvent(String deviceSerialNumber/* TODO */);

}
