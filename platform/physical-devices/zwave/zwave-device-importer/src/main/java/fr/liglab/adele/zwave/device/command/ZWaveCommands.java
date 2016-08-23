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
package fr.liglab.adele.zwave.device.command;

import java.util.Date;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.zwave.device.api.ZWaveNetworkEvent;
import fr.liglab.adele.zwave.device.api.ZwaveController;
import fr.liglab.adele.zwave.device.api.ZwaveController.Mode;

@CommandProvider(namespace = "zwave")
@Component
@Instantiate
public class ZWaveCommands {

	@Requires(optional=true, proxy = false)
	ZwaveController controller;

	@Command
	public void info() {
		if (controller != null) {
			System.out.println(" controller homeId = " + controller.getHomeId()+" nodeId = "+controller.getNodeId());
		}
	}

	@Command
	public void mode() {
		if (controller != null) {
			System.out.println(" mode " + controller.getMode());
		}
	}

	@Command
	public void mode(String mode) {
		if (controller != null) {
			controller.changeMode(Mode.valueOf(mode));
		}
	}
	
	@Command
	public void event() {
		if (controller != null) {
			
			ZWaveNetworkEvent event = controller.getLastEvent();
			if (event != null) {
				System.out.println(" Event " + event.type+ " 	at "+new Date(event.timeStamp));
				System.out.println("       " + event.homeId+ " "+event.nodeId);
				System.out.println("       " + event.manufacturerId+ " "+event.deviceType+ " "+ event.deviceId);
			}
		}
	}
	

}
