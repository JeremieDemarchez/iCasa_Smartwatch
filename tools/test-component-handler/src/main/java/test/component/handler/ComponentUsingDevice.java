package test.component.handler;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;

@Component(name="ComponentUsingDevice")
@Instantiate
public class ComponentUsingDevice {
	
	@RequiresDevice(mandatoryProps={"thermometer.currentTemperature", "surface"})
	private Thermometer thermometer;
	
	
	@RequiresDevice(mandatoryProps={"heater.powerLevel"})
	private Heater heater;

	
	@Requires
	private Heater otherHeater;
	
	@Validate
	public void start() {
		System.out.println("Heater SN" + heater.getSerialNumber());
		System.out.println("Thermometer SN" + thermometer.getSerialNumber());
	}
	
	

}
