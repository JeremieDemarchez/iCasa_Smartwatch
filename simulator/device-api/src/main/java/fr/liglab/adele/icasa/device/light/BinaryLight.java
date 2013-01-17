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
package fr.liglab.adele.icasa.device.light;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Service definition of a simple binary light device.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public interface BinaryLight extends GenericDevice {

	/**
	 * Service property indicating whether the binary light is is turned on or
	 * off.
	 * 
	 * <ul>
	 * <li>This property is <b>mandatory</b></li>
	 * <li>Type of values : <b><code>java.lang.Boolean</code></b></li>
	 * <li>Description : value is <code>true</code> when the light is turned on,
	 * <code>false</code> otherwise.</li>
	 * </ul>
	 * 
	 * @see #getPowerStatus()
	 * @see #setPowerStatus(boolean)
	 */
	String LIGHT_POWER_STATUS = "light.powerStatus";

	/**
	 * Device property indicating the Lamp maximum Illuminance (in Lumen)
	 * <ul>
	 * <li>This property is <b>mandatory</b></li>
	 * <li>Type of values : <b><code>java.lang.Double</code></b>, between
	 * <code>100d</code> and <code>2500d</code></li>
	 * <li>Description : value is <code>100d</code> for small lamps,
	 * <code>2500d</code> for very big ones.</li>
	 * </ul>
	 */
	String LIGHT_MAX_ILLUMINANCE = "light.maxIlluminance";

	/**
	 * Return the current power state of this binary light.
	 * 
	 * @return the current power state of this binary light.
	 * @see #setPowerStatus(boolean)
	 * @see #LIGHT_POWER_STATUS
	 */
	boolean getPowerStatus();

	/**
	 * Change the power status of this binary light.
	 * 
	 * @param state
	 *           the new power state of this binary light.
	 * @return the previous power state of this binary light.
	 * @see #getPowerStatus()
	 * @see #LIGHT_POWER_STATUS
	 */
	boolean setPowerStatus(boolean state);

}
