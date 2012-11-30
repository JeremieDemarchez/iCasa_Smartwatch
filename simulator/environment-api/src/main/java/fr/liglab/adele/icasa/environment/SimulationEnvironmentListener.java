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
package fr.liglab.adele.icasa.environment;

public interface SimulationEnvironmentListener {
	
	// ----  Zone events ---- //
	
	public void zoneAdded(Zone zone);
	
	public void zoneRemoved(Zone zone);
	
	public void zoneMoved(Zone zone);
	
	public void zoneVariableModified(Zone zone, String variableName, Double oldValue, Double newValue);
	
	public void zoneResized(Zone zone);
	
	public void zoneParentModified(Zone zone);
	
	
	// ----  Device events ---- //
	
	public void deviceAdded(Device device);
	
	public void deviceRemoved(Device device);
	
	public void deviceMoved(Device device);
	
	public void deviceModified(Device device);
	
	
	// ---- Person events ---- //
	
	public void personAdded(Person person);
	
	public void personRemoved(Person person);
	
	public void personMoved(Person person);
	

}
