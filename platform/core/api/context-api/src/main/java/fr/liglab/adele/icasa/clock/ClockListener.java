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
package fr.liglab.adele.icasa.clock;

/**
 * 
 * This interface must be implemented by Clock listeners.
 * 
 * @author Gabriel Pedraza Ferreira
 *
 */
public interface ClockListener {
	
	/**
	 * Notifies the listener when the factor has been changed.
	 * 
	 * @param oldFactor The previous value of factor.
	 */
	void factorModified(int oldFactor);
	
	/**
	 * Notifies the listener when the start date has been changed.
	 * 
	 * @param oldStartDate The previous value of start date.
	 */
	void startDateModified(long oldStartDate);
	
	/**
	 * Notifies the listener when the clock has been paused.
	 */
	void clockPaused();
	
	/**
	 * Notifies the listener when the clock has been resumed.
	 */
	void clockResumed();
	
	/**
	 * Notifies the listener when the clock has been reset.
	 */
	void clockReset();

}
