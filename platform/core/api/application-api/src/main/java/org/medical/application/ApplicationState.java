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
package org.medical.application;

/**
 * Represents the state of an application.
 * 
 * @author Thomas Leveque
 *
 */
public enum ApplicationState {
	STARTED("started"), STOPED("stoped"), PAUSED("paused");
	
	private String _stateStr;
	
	private ApplicationState(String stateStr) {
		_stateStr = stateStr;
	}
	
	@Override
	public String toString() {
		return _stateStr;
	}
	
	/**
	 * Returns corresponding application state.
	 * Returns null if cannot be parsed.
	 * 
	 * @param stateStr a application state in a string format
	 * @return corresponding application state.
	 */
	public static ApplicationState fromString(String stateStr) {
		
		if (STARTED.toString().equals(stateStr)) {
			return STARTED;
		} else if (PAUSED.toString().equals(stateStr)) {
			return PAUSED;
		} else if (STOPED.toString().equals(stateStr)) {
			return STOPED;
		} 
		
		return null;
	}
}
