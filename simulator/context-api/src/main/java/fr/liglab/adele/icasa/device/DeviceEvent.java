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
 * Represents an event related to a device.
 *
 * @author Thomas Leveque
 */
public class DeviceEvent {

    private String _propName;
    private String _description;
    private DeviceEventType _type;
    private GenericDevice _device;
    private Object _oldValue;

    public DeviceEvent(GenericDevice device, DeviceEventType type, String propName, Object oldValue) {
        _device = device;
        _propName = propName;
        _type = type;
        _oldValue = oldValue;
    }

    public DeviceEvent(GenericDevice device, DeviceEventType type, String propName) {
        this(device, type, propName, null);
    }

    public DeviceEvent(GenericDevice device, DeviceEventType type) {
        this(device, type, null);
    }

    public void setDescription(String description) {
        _description = description;
    }

    public DeviceEventType getType() {
        return _type;
    }

    public String getDescription() {
        return _description;
    }

    public String getPropertyName() {
        return _propName;
    }

    public GenericDevice getDevice() {
        return _device;
    }

    public Object getOldValue() {
        return _oldValue;
    }
}
