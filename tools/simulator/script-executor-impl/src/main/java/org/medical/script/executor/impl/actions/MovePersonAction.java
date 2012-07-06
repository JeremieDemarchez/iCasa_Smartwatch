/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
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
package org.medical.script.executor.impl.actions;


import org.medical.script.executor.impl.ScriptExecutorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Moves a person between the simulated environments 
 * 
 * @author Gabriel
 *
 */
public class MovePersonAction extends Action {

	/**
	 * Environment ID used to place a person
	 */
	private String environmentId;
	
	private String person;
	
	private static final Logger logger = LoggerFactory.getLogger(MovePersonAction.class);
	
	/**
    * 
    */
   private final ScriptExecutorImpl simulatedBehavior;

	public MovePersonAction(ScriptExecutorImpl simulatedBehavior, String environmentId, int delay, String person) {
		super(simulatedBehavior, delay);
		this.environmentId = environmentId;
		this.simulatedBehavior = simulatedBehavior;
		this.person = person;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	/*
	public void runOld() {
		SimulatedEnvironment environment = (SimulatedEnvironment) this.simulatedBehavior.getEnvironments().get(environmentId);
		if (environment != null) {
			logger.info("Executing moving to room ------> " + environmentId);
			environment.setProperty("presence", new Double(1.0));
			for (SimulatedEnvironment otherEnvironment : this.simulatedBehavior.getEnvironments().values()) {
				if (otherEnvironment != environment)
					otherEnvironment.setProperty("presence", new Double(0.0));
			}
		}
	}
	*/
	
	public void run() {
		logger.info("Executing moving :: " +  person + " to room ------> " + environmentId);
		
		if (person!=null && (!person.isEmpty()))
			simulatedBehavior.getSimulationManager().setUserLocation(person, environmentId);
		else
			simulatedBehavior.getSimulationManager().setUserLocation("uknown", environmentId);
	}

}