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

/**
 * Created by aygalinc on 19/04/16.
 */
public @ContextService interface ZwaveController extends ZwaveRepeater {
	

	/**
	 * The configured serial port
	 */
    public static @State  String SERIAL_PORT="serial.port";

    /**
     * Whether thi is the master controller
     */
    public static @State  String MASTER="wwave.controller.isMaster";

    public boolean isMaster();

    /**
     * The operation mode of the controller
     */
    public static @State  String MODE="zwave.controller.mode";

	public enum Mode {
		NORMAL,
		INCLUSION,
		EXCLUSION
	}

    public Mode getMode();

    public void changeMode(Mode mode);
 
    /**
     * The last network event
     */
    public static @State  String NETWORK_EVENT="zwave.controller.event";

    public ZWaveNetworkEvent getLastEvent();
    
}
