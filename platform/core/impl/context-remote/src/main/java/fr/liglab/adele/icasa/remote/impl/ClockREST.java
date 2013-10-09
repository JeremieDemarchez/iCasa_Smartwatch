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
package fr.liglab.adele.icasa.remote.impl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.remote.util.IcasaJSONUtil;

/**
 * 
 * @author Gabriel Pedraza Ferreira
 * 
 */
@Component(name = "remote-rest-clock")
@Instantiate(name = "remote-rest-clock-0")
@Provides(specifications = { ClockREST.class }, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value = "/clocks/")
public class ClockREST extends AbstractREST {

    public static final String DEFAULT_INSTANCE_NAME = "default";

    @Requires(optional = true)
	private Clock clock;

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/clocks/")
    public Response clocksOptions() {
        return makeCORS(Response.ok());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/clocks/")
    public Response clocks() {
        return makeCORS(Response.ok(getClocks()));
    }

    /**
     * Returns a JSON array containing all clocks.
     *
     * @return a JSON array containing all clocks.
     */
    private String getClocks() {
        JSONArray currentClocks = new JSONArray();

        if (clock != null) {
            JSONObject currentClock = IcasaJSONUtil.getClockJSON(clock);
            if (currentClock != null)
                currentClocks.put(currentClock);
        }

        return currentClocks.toString();
    }

	@GET
	@Produces(MediaType.APPLICATION_JSON)
    @Path(value="/clock/{clockId}")
	public Response clock(@PathParam("clockId") String clockId) {
        if ((clock == null) || (clockId == null) || (! DEFAULT_INSTANCE_NAME.equals(clockId)))
            return makeCORS(Response.status(404));

		return makeCORS(Response.ok(IcasaJSONUtil.getClockJSON(clock).toString()));
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
    @Path(value="/clock/{clockId}")
	public Response clockOptions(@PathParam("clockId") String clockId) {
		return makeCORS(Response.ok());
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/clock/{clockId}")
	public Response updateClock(@PathParam("clockId") String clockId, String content) {
        if ((clock == null) || (clockId == null) || (! "default".equals(clockId)))
            return makeCORS(Response.status(404));

		try {
			JSONObject clockObject = new JSONObject(content);
			int factor = clockObject.getInt("factor");
			boolean pause = clockObject.getBoolean("pause");
			long startDate = clockObject.getLong("startDate");
			
			synchronized (clock) {
				if (clock.getStartDate() != startDate)
					clock.setStartDate(startDate);

				if (clock.getFactor() != factor)
					clock.setFactor(factor);

				if (pause) {
					if (!clock.isPaused()) {
						clock.pause();
					}
				} else {
					if (clock.isPaused())
						clock.resume();
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return makeCORS(Response.ok(IcasaJSONUtil.getClockJSON(clock).toString()));
	}




}
