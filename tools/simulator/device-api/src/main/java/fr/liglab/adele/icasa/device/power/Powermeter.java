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
package fr.liglab.adele.icasa.device.power;


/**
 * Service definition for a simple powermeter device
 * 
 * @author gunalp
 *
 */
public interface Powermeter {

	/**
	 * Service property indicating the current Power Rating sensed by the Powermeter device, expressed in watt (W)
	 * 
	 * <ul>
     * <li>This property is <b>mandatory</b></li>
     * <li>Type of values : <b><code>java.lang.Double</code></b></li>
     * <li>Description : value is a power rating expressed in watt (W), so it is
     * <code>always positive</code>.</li>
     * </ul>
	 * @see #getCurrentPowerRating()
	 */
	String POWERMETER_CURRENT_RATING = "powermeter.currentrating";
	
	
	/**
	 * Returns the current Power rating sensed by the powermeter expressed in watt (W)
	 * 
	 * @return the current Power Rating sensed by the powermeter expressed in watt (W)
	 * @see #POWERMETER_CURRENT_RATING
	 */
	double getCurrentPowerRating();
	
	
}
