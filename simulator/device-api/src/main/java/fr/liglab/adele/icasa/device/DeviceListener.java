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

import java.util.EventListener;

/**
 * Events may be notified more than once per event.
 * The only warantly is that at least one event will be sent to listeners.
 *
 * @author Gabriel Pedraza Ferreira
 *
 */
public interface DeviceListener {

    public void deviceAdded(GenericDevice device);

    public void deviceRemoved(GenericDevice device);

    public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue);

    public void devicePropertyAdded(GenericDevice device, String propertyName);

    public void devicePropertyRemoved(GenericDevice device, String propertyName);
}
