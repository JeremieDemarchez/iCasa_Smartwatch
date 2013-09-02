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
package fr.liglab.adele.icasa.device.button;

import fr.liglab.adele.icasa.device.GenericDevice;

/**
 * User: garciai@imag.fr
 * Date: 9/2/13
 * Time: 10:42 AM
 */
public interface PushButton extends GenericDevice{
    /**
     * Service property indicating if the button is pushed and hold.
     *
     * <ul>
     * <li>This property is <b>mandatory</b></li>
     * <li>Type of values : <b><code>java.lang.Boolean</code></b></li>
     * <li>Description : value is <code>true</code> the button is hold.
     * <code>false</code> otherwise.</li>
     * </ul>
     *
     */
    String PUSH_AND_HOLD = "pushButton.pushAndHold";
}
