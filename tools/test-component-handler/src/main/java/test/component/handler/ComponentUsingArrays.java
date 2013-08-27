/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package test.component.handler;


import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.dependency.handler.annotations.RequiresDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.temperature.Thermometer;

//@Component
//@Instantiate
public class ComponentUsingArrays {

	private Thread thread;
	
	@RequiresDevice(id="thermometers", type="field")
	private BinaryLight[] lightsArray;
	
	@RequiresDevice(id="lights", type="field", specification="fr.liglab.adele.icasa.device.light.BinaryLight")
	private List<BinaryLight> lightsList;

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
		
		private int counter = 0;
		@Override
		public void run() {
			execute = true;
			while (execute) {
				try {
	            Thread.sleep(1000);
	            counter++;
	            if (counter%2==0) {
	            	printLightsList();
	            } else {
	            	printLightsArray();
	            }
	            
            } catch (InterruptedException e) {
	            execute = false;
	            e.printStackTrace();
            }
			}
		}
		
		private void printLightsList() {
			System.out.println("Printing List");
			System.out.println("=================================================");
			for (BinaryLight light : lightsList) {
				try {
					System.out.println("------> BinalyLight " + light.getSerialNumber());
            } catch (Exception e) {
            	e.printStackTrace();
            }
         }
      }
		
		private void printLightsArray() {
			System.out.println("Printing Array");
			System.out.println("=================================================");
			for (BinaryLight light : lightsArray) {
				try {
					System.out.println("------> BinalyLight " + light.getSerialNumber());
            } catch (Exception e) {
            	e.printStackTrace();
            }
         }
      }
	}
}
