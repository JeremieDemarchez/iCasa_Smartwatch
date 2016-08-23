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
