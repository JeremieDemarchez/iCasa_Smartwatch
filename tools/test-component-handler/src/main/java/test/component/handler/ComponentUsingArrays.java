package test.component.handler;

import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.temperature.Thermometer;

@Component
@Instantiate
public class ComponentUsingArrays {

	private Thread thread;
	
	@RequiresDevice
	private Thermometer[] thermometers;

	@Validate
	private void start() {
		thread = new Thread(new PrintLigthsRunnable());
		thread.start();
   }
	
	@Invalidate
	private void stop() {
		thread.stop(); 
	}
	
	class PrintLigthsRunnable implements Runnable {
		
		private boolean execute;
		
		@Override
		public void run() {
			execute = true;
			while (execute) {
				try {
	            Thread.sleep(1000);
	            printLights();
            } catch (InterruptedException e) {
	            execute = false;
	            e.printStackTrace();
            }
			}
		}
		
		private void printLights() {
			for (Thermometer thermometer : thermometers) {
				try {
					System.out.println("------> Thermometer " + thermometer.getSerialNumber());
					System.out.println("------> Thermometer " + thermometer.getTemperature());
            } catch (Exception e) {
            	e.printStackTrace();
            }
         }
      }
	}
}
