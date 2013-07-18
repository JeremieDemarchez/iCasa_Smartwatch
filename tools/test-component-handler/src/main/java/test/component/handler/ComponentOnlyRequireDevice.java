package test.component.handler;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;

@Component
@Instantiate
public class ComponentOnlyRequireDevice {

	@RequiresDevice(id="thermometer")
	private Thermometer thermometer;
		
	@RequiresDevice(id="heater")
	private Heater heater;
	
	@RequiresDevice(id="light")
	private BinaryLight light;
	
	@Validate
	private void start() {
		System.out.println("Starting ComponentOnlyRequireDevice");
		System.out.println("Thermometer: " + thermometer.getSerialNumber());
		System.out.println("Heater: " + heater.getSerialNumber());
		System.out.println("BinaryLight: " + light.getSerialNumber());
   }
	
}
