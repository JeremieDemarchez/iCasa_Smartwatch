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
 * Service definition of a simple dimmer light device.
 * 
 * @author bourretp
 */
public interface DimmerLight extends GenericDevice {

	/**
	 * Device property indicating the current power level of the dimmer light.
	 * 
	 * <ul>
	 * <li>This property is <b>mandatory</b></li>
	 * <li>Type of values : <b><code>java.lang.Double</code></b>, between
	 * <code>0.0d</code> and <code>1.0d</code></li>
	 * <li>Description : value is <code>0.0d</code> when the light is completely
	 * turned off, <code>1.0d</code> when completely turned on.</li>
	 * </ul>
	 * 
	 * @see #getPowerLevel()
	 * @see #setPowerLevel(double)
	 */
	String LIGHT_POWER_LEVEL = "power_level";

	/**
	 * Device property indicating the maximum power level of the dimmer light.
	 * 
	 * <ul>
	 * <li>This property is <b>mandatory</b></li>
	 * <li>Type of values : <b><code>java.lang.Double</code></b>, is
	 * <code>100</code> Watts</li>
	 * <li>Description : value is the wattage of the light.  <code>100</code> watts for a normal lamp.</li>
	 * </ul>
	 * 
	 * @see #getMaxPowerLevel()
	 */
	String LIGHT_MAX_POWER_LEVEL = "max_power";
	/**
	 * Device property indicating the Lamp maximum Illuminance (in Lux)
	 * <ul>
	 * <li>This property is <b>mandatory</b></li>
	 * <li>Type of values : <b><code>java.lang.Double</code></b> compute by
	 * the method computeIlluminance.
	 * </ul>
	 */
	String LIGHT_MAX_ILLUMINANCE = "max_illuminance";

	/**
	 * Return the current power level of this dimmer light.
	 * 
	 * @return the current power level of this dimmer light.
	 * @see #setPowerLevel(double)
	 * @see #LIGHT_POWER_LEVEL
	 */
	double getPowerLevel();

	/**
	 * Change the power level of this dimmer light.
	 * 
	 * @param level
	 *           the new power level of this dimmer light.
	 * @return the previous power level of this dimmer light.
	 * @see #getPowerLevel()
	 * @see #LIGHT_POWER_LEVEL
	 */
	double setPowerLevel(double level);
	
	/**
	 * Return the current power level of this dimmer light
	 * @return the power level of this dimmer light
	 * 
	 * @see #LIGHT_MAX_POWER_LEVEL
	 */
	double getMaxPowerLevel();

}
