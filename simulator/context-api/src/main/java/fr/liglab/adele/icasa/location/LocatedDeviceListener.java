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
package fr.liglab.adele.icasa.location;

import fr.liglab.adele.icasa.listener.IcasaListener;

/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 30/11/12
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public interface LocatedDeviceListener extends IcasaListener {

    public void deviceAdded(LocatedDevice device);

    public void deviceRemoved(LocatedDevice device);

    public void deviceMoved(LocatedDevice device, Position oldPosition);

    public void devicePropertyModified(LocatedDevice device, String propertyName, Object oldValue);

    public void devicePropertyAdded(LocatedDevice device, String propertyName);

    public void devicePropertyRemoved(LocatedDevice device, String propertyName);


}
