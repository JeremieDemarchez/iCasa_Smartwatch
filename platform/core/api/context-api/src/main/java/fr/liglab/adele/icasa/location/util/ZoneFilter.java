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
package fr.liglab.adele.icasa.location.util;

import fr.liglab.adele.icasa.location.Zone;

/**
 * User: garciai@imag.fr
 * Date: 8/2/13
 * Time: 11:26 AM
 */
public interface ZoneFilter {

    /**
     * Determines if the zone match to be tracked.
     * @param zone the zone to see if it match.
     * @return true if the zone match, false if not.
     */
    boolean match(Zone zone);

}
