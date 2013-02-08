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
package fr.liglab.adele.icasa.script.executor;

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
		STOPPED, EXECUTING, PAUSED
	}

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
	public State getState();

	/**
	 * Returns percentage of completed instructions of current script.
	 * 
	 * @return percentage of completed instructions of current script.
	 */
	public float getExecutedPercentage();

	/**
	 * Wrapper for clock function
	 * 
	 * @return
	 */
	public long currentTimeMillis();

	/**
	 * Wrapper for clock function
	 * 
	 * @param startDate
	 */
	public void setStartDate(long startDate);

	/**
	 * Wrapper for clock function
	 * 
	 * @param factor
	 */
	public void setFactor(int factor);

	/**
	 * Wrapper for clock function
	 * 
	 * @return
	 */
	public long getElapsedTime();

	/**
	 * Wrapper for clock function
	 * 
	 * @return
	 */
	public int getFactor();

	/**
	 * Wrapper for clock function
	 * 
	 * @return
	 */
	public Date getStartDate();

}
