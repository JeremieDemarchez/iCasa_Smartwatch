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
package fr.liglab.adele.icasa.script.executor.impl.actions;

import fr.liglab.adele.icasa.script.executor.impl.ScriptExecutorImpl;

/**
 * 
 * Abstract base class for actions to be executed by script executor
 * 
 * @author Gabriel
 *
 */
public abstract class Action implements Runnable {

	/**
	 * Delay (in ms) from precedent action 
	 */
	protected int delay;
	
	/**
	 * Instance of the script executor used by actions
	 */
	protected ScriptExecutorImpl scriptExecutorImpl;

	/**
	 * Default constructor
	 * @param simulatedBehavior script executor
	 * @param delay delay in ms
	 */
	public Action(ScriptExecutorImpl simulatedBehavior, int delay) {
		this.delay = delay;
		this.scriptExecutorImpl = simulatedBehavior;
	}

	/**
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();

}