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
package fr.liglab.adele.icasa.simulator.model.api;

import fr.liglab.adele.icasa.context.model.annotations.ContextService;
import fr.liglab.adele.icasa.context.model.annotations.State;
import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;

/**
 * Created by aygalinc on 10/02/16.
 */
public @ContextService interface LuminosityModel {

    public static final @State String CURRENT_LUMINOSITY = "current.luminosity";

    public static final @State String ZONE_ATTACHED = "zone.attached";

    public static final String STATE_ZONE_ATTACHED_ID = "luminositymodel.zone.attached";
    public double getCurrentLuminosity();

}
