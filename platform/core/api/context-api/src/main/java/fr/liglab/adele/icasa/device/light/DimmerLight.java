/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.device.light;

import fr.liglab.adele.icasa.context.model.annotations.ContextService;
import fr.liglab.adele.icasa.context.model.annotations.State;
import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * Service definition of a simple dimmer light device.
 *
 */
public @ContextService interface DimmerLight extends GenericDevice {

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
	@State String DIMMER_LIGHT_POWER_LEVEL = "dimmerLight.powerLevel";

	/**
	 * Return the current power level of this dimmer light.
	 * 
	 * @return the current power level of this dimmer light.
	 * @see #setPowerLevel(double)
	 * @see #DIMMER_LIGHT_POWER_LEVEL
	 */
	double getPowerLevel();

	/**
	 * Change the power level of this dimmer light.
	 * 
	 * @param level
	 *           the new power level of this dimmer light.
	 * @return the previous power level of this dimmer light.
	 * @see #getPowerLevel()
	 * @see #DIMMER_LIGHT_POWER_LEVEL
	 */
	void setPowerLevel(double level);


}
