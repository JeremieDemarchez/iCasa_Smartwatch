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
package fr.liglab.adele.icasa.simulator.device;

import java.util.Map;
import java.util.Set;

/**
 * The simulation Mananger deals with operations associated to simulation
 *
 */
public interface SimulatedDeviceProvider {

    /**
     * Creates an instance of a simulated device.
     *
     * @param deviceType The device type
     * @param deviceId The new device identifier.
     * @return The device wrapper object.
     * @throws IllegalArgumentException if a device instance exists with the same identifier.
     * @throws IllegalStateException if there no exists a factory matching the <code>deviceType</code> argument.
     */
    public void createDevice(String deviceType, String deviceId);

    /**
     * Removes a simulated device instance.
     *
     * @param deviceId The identifier of the simulated device.
     */
    public void removeSimulatedDevice(String deviceId);

    /**
     * Get a non-modifiable set of the device types available in the iCasa context. The device type corresponds to the
     * component factory name.
     *
     * @return a set of the device types.
     */
    public Set<String> getSimulatedDeviceTypes();

    /**
     * Removes all simulated device instance in iCasa.
     */
    public void removeAllSimulatedDevices();

}
