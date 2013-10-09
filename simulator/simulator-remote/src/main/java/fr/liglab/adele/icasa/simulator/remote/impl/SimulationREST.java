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
package fr.liglab.adele.icasa.simulator.remote.impl;

import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import org.apache.felix.ipojo.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Remote service to manipulate global context simulation info (e.g reset the context)
 */
@Component(name="remote-rest-simulation")
@Instantiate(name="remote-rest-simulation-0")
@Provides(specifications={SimulationREST.class}, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value="/simulation")
public class SimulationREST extends AbstractREST {
    @Requires
    private SimulationManager _simulationMgr;


    /**
     * Reset the simulation context (persons, simulated devices, zones, ...)
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetSimulation() {
        _simulationMgr.resetContext();
        return makeCORS(Response.ok());
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetSimulationOptions() {
        return makeCORS(Response.ok());
    }
}
