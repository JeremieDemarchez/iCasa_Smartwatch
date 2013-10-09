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
package fr.liglab.adele.icasa.device;


public class DevicePropertyEvent extends DeviceEvent {

    private final Object _oldValue;
    private final String _name;
    private final Object _newValue;

    /**
     * Class which represents the properties
     * @param name property name.
     * @param oldValue the old value of the property.
     * @param newValue the new value of the property.
     */
    public DevicePropertyEvent(GenericDevice device, DeviceEventType type, String name, Object oldValue, Object newValue){
        super(device,type);
        _name = name;
        _oldValue = oldValue;
        _newValue = newValue;
    }

    /**
     * Gets the old value of the property modified
     *
     * @return The old value of the property modified
     */
    public Object getOldValue() {
        return _oldValue;
    }

    /**
     * Gets the new value of the property modified
     *
     * @return The new value of the property modified
     */
    public Object getNewValue() {
        return _newValue;
    }

    public String getPropertyName(){
        return _name;
    }

}
