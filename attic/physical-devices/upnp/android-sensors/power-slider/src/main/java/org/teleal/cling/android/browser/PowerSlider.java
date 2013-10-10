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
package main.java.org.teleal.cling.android.browser;

import java.beans.PropertyChangeSupport;

import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;
import org.teleal.cling.binding.annotations.UpnpStateVariable;

@UpnpService(
        serviceId = @UpnpServiceId("PowerSlider"),
        serviceType = @UpnpServiceType(value = "PowerSlider", version = 1)
)
public class PowerSlider {

    private final PropertyChangeSupport propertyChangeSupport;

    public PowerSlider() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(name = "PowerLevel", defaultValue = "0")
    private float powerLevel = 0;

    @UpnpAction(out = @UpnpOutputArgument(name = "RetPowerLevelValue"))
    public float getPowerLevel() {
        powerLevel = DemoActivity._progressValue;
    	return powerLevel;
    }

}