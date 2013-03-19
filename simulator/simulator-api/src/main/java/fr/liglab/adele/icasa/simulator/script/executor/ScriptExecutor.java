/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.script.executor;

import java.util.Date;
import java.util.List;

/**
 * Executes behavior scripts
 * 
 * @author Gabriel
 * 
 */
public interface ScriptExecutor {
	
	public enum State {
		STARTED("started"), STOPPED("stopped"), PAUSED("paused");

		private String _stateStr;

		private State(String stateStr) {
			_stateStr = stateStr;
		}

		public String toString() {
			return _stateStr;
		}

		public static State fromString(String stateStr) {
			if (stateStr == null)
				return null;

			if (STARTED.toString().equals(stateStr))
				return STARTED;
			if (STOPPED.toString().equals(stateStr))
				return STOPPED;
			if (PAUSED.toString().equals(stateStr))
				return PAUSED;

			return null;
		}
	}

	public void addListener(ScriptExecutorListener listener);
	
	public void removeListener(ScriptExecutorListener listener);
	
	/**
	 * Gets a lists of scripts in the platform
	 * 
	 * @return
	 */
	public List<String> getScriptList();

	/**
	 * Gets the current script in execution
	 * 
	 * @return
	 */
	public String getCurrentScript();

	/**
	 * Executes the script having this name
	 * 
	 * @param scriptName
	 */
	public void execute(String scriptName);

	/**
	 * Executes the script having this name
	 * 
	 * @param scriptName
	 * @param startDate
	 * @param factor
	 */
	public void execute(String scriptName, Date startDate, int factor);

	/**
	 * Stops the script execution
	 * 
	 */
	public void stop();

	/**
	 * Pauses the execution of the current script (if there is one)
	 */
	public void pause();

	/**
	 * Resumes the execution of the current script (if there is one)
	 */
	public void resume();

	/**
	 * Gets the execution estate of the current script (if there is one)
	 * 
	 * @return
	 */
	public State getCurrentScriptState();

	/**
	 * Returns percentage of completed instructions of current script.
	 * 
	 * @return percentage of completed instructions of current script.
	 */
	public float getExecutedPercentage();
	
	void saveSimulationScript(String fileName);
	
	public int getFactor(String scriptName);
	
	public long getStartDate(String scriptName);
	
	public int getActionsNumber(String scriptName);
	
	public int getExecutionTime(String scriptName);
	
	public State getState(String scriptName);


		
	
	

	/**
	 * Wrapper for clock function
	 * 
	 * @return
	 */
	//public long currentTimeMillis();

	/**
	 * Wrapper for clock function
	 * 
	 * @param startDate
	 */
	//public void setStartDate(long startDate);

	/**
	 * Wrapper for clock function
	 * 
	 * @param factor
	 */
	//public void setFactor(int factor);

	/**
	 * Wrapper for clock function
	 * 
	 * @return
	 */
	//public long getElapsedTime();

	/**
	 * Wrapper for clock function
	 * 
	 * @return
	 */
	//public int getFactor();

	/**
	 * Wrapper for clock function
	 * 
	 * @return
	 */
	//public long getStartDate();

}
