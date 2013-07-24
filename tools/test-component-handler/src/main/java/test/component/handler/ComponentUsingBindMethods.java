package test.component.handler;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.liglab.adele.icasa.dependency.handler.annotations.BindDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;


@Component
@Instantiate
public class ComponentUsingBindMethods {

	
	@BindDevice
	public void bindBinaryLight(BinaryLight light) {
		System.out.println("ComponentUsingBindMethods ---------------> " + light.getSerialNumber());
		System.out.println("ComponentUsingBindMethods ---------------> " + light.getPowerStatus());
	}
	
	@Bind
	public void bindBinaryLight2(BinaryLight light) {
		System.out.println("ComponentUsingBindMethods ---------------> " + light.getSerialNumber());
		System.out.println("ComponentUsingBindMethods ---------------> " + light.getPowerStatus());
	}
	
	@Unbind
	public void unbindBinaryLight2(BinaryLight light) {
		
	}
	
	@Unbind
	public void unbindBinaryLight3(BinaryLight light) {
		
	}
	
	

	
	
}
