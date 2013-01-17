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
 * Service definition of a simple photometer device.
 * 
 * @author Gabriel Pedraza Ferreira
 */
public interface Photometer extends GenericDevice {

    /**
     * Service property indicating the current illuminance sensed by the
     * photometer, expressed in lux (lx).
     * 
     * <ul>
     * <li>This property is <b>mandatory</b></li>
     * <li>Type of values : <b><code>java.lang.Double</code></b></li>
     * <li>Description : value is a temperature expressed in lux (lx), so it is
     * <code>always positive</code>.</li>
     * </ul>
     * 
     * @see #getIlluminance()
     */
    String PHOTOMETER_CURRENT_ILLUMINANCE = "photometer.currentilluminance";

    /**
     * Return the current illuminance sensed by this photometer, expressed in
     * lux (lx).
     * 
     * @return the current illuminance sensed by this photometer, expressed in
     *         lux (lx).
     * @see #PHOTOMETER_CURRENT_ILLUMINANCE
     */
    double getIlluminance();

}
