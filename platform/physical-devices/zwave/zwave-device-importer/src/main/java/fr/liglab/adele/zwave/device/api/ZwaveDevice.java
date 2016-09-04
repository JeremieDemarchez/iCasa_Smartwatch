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
package fr.liglab.adele.zwave.device.api;

import fr.liglab.adele.cream.annotations.ContextService;
import fr.liglab.adele.cream.annotations.State;

import java.util.List;

public @ContextService interface ZwaveDevice {

    static final @State String NEIGHBORS = "zwave.neighbors";

    public List<Integer> getNeighbors();

    /**
     * The network home id
     */
    public static @State  String HOME_ID="zwave.homeId";
    
    public int getHomeId();

    /**
     * The network node id
     */
    static final @State String NODE_ID = "zwave.nodeId";

    public int getNodeId();

    /**
     * The network node id
     */
    static final @State String MANUFACTURER_ID = "zwave.manufacturerId";

    public int getManufacturerId();


    /**
     * The network node id
     */
    static final @State String DEVICE_ID = "zwave.deviceId";

    public int getDeviceId();

    /**
     * The network node id
     */
    static final @State String DEVICE_TYPE = "zwave.deviceType";

    public int getDeviceType();

}
