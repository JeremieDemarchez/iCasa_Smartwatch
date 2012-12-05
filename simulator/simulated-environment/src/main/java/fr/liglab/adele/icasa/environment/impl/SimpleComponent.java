package fr.liglab.adele.icasa.environment.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.environment.SimulationManagerNew;


@Component
@Instantiate
public class SimpleComponent {

	@Requires
	private SimulationManagerNew test;
	
	@Validate
	public void start() {
	   test.getDevices();
   }
	
}
