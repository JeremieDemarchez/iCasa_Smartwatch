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
package fr.liglab.adele.icasa.distribution.test.device.util;

import fr.liglab.adele.commons.test.utils.Condition;
import fr.liglab.adele.icasa.device.util.LocatedDeviceTracker;


/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 26/07/13
 * Time: 10:12
 * To change this template use File | Settings | File Templates.
 */
public class DeviceTrackedNumberCondition implements Condition {

    private int _deviceNb;
    private LocatedDeviceTracker _tracker;

    public DeviceTrackedNumberCondition(LocatedDeviceTracker tracker, int deviceNb) {
        _deviceNb = deviceNb;
        _tracker = tracker;
    }

    public String getDescription() {
        return "Number of tracked devices must be equals to " + _deviceNb;
    }

    public boolean isChecked() {
        return _tracker.size() == _deviceNb;
    }
}