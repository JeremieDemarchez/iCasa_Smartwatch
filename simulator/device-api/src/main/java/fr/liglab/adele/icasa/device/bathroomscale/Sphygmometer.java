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
package fr.liglab.adele.icasa.device.bathroomscale;

public interface Sphygmometer {
		
	public final String SYSTOLIC_PROPERTY = "current_systolic";
	
	public final String DIASTOLIC_PROPERTY = "current_diastolic";
	
	public final String PULSATIONS_PROPERTY = "current_pulsations";
	
	public int getSystolic();
	
	public int getDiastolic();
	
	public int getPulsations(); 
	
}
