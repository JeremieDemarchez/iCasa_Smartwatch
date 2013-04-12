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
