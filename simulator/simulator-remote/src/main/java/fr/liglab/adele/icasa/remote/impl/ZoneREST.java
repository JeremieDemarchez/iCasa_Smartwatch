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
package fr.liglab.adele.icasa.remote.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

import fr.liglab.adele.icasa.simulator.Position;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.Zone;

/**
 * @author Thomas Leveque
 */
@Component(name = "remote-rest-zone")
@Instantiate(name = "remote-rest-zone-0")
@Provides(specifications = { ZoneREST.class }, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value = "/zones/")
public class ZoneREST extends AbstractREST {

	@Requires
	private SimulationManager _simulationMgr;


	public JSONObject getZoneJSON(Zone zone) {
		JSONObject zoneJSON = null;
		try {
			String zoneId = zone.getId();
			zoneJSON = new JSONObject();
			zoneJSON.putOnce("id", zoneId);
			zoneJSON.putOnce("name", zoneId);
			zoneJSON.put("leftX", zone.getLeftTopAbsolutePosition().x);
			zoneJSON.put("topY", zone.getLeftTopAbsolutePosition().y);
			zoneJSON.put("rightX", zone.getRightBottomAbsolutePosition().x);
			zoneJSON.put("bottomY", zone.getRightBottomAbsolutePosition().y);
			zoneJSON.put("isRoom", true); // TODO change it when Zone API will be improved

			JSONObject propObject = new JSONObject();
			for (String variable : zone.getVariableNames()) {
				propObject.put(variable, zone.getVariableValue(variable));
			}
			zoneJSON.put("variables", propObject);

		} catch (JSONException e) {
			e.printStackTrace();
			zoneJSON = null;
		}

		return zoneJSON;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/zones/")
	public Response zones() {
		return makeCORS(Response.ok(getZones()));
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/zone/{zoneId}")
	public Response updatesZoneOptions(@PathParam("zoneId") String zoneId) {
		return makeCORS(Response.ok());
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/zone/")
	public Response createsZoneOptions() {
		return makeCORS(Response.ok());
	}

	/**
	 * Retrieves a zone.
	 * 
	 * @param zoneId
	 *           The ID of the zone to retrieve
	 * @return The required zone
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/zone/{zoneId}")
	public Response getZone(@PathParam("zoneId") String zoneId) {
		if (zoneId == null || zoneId.length() < 1) {
			return makeCORS(Response.ok(getZones()));
		}

		Zone zoneFound = _simulationMgr.getZone(zoneId);
		if (zoneFound == null) {
			return makeCORS(Response.status(404));
		} else {
			JSONObject zoneJSON = getZoneJSON(zoneFound);

			return makeCORS(Response.ok(zoneJSON.toString()));
		}
	}

	/**
	 * Returns a JSON array containing all zones.
	 * 
	 * @return a JSON array containing all zones.
	 */
	public String getZones() {
		// boolean atLeastOne = false;
		JSONArray currentZones = new JSONArray();
		for (String envId : _simulationMgr.getZoneIds()) {
			Zone zone = _simulationMgr.getZone(envId);
			if (zone == null)
				continue;

			JSONObject zoneJSON = getZoneJSON(zone);
			if (zoneJSON == null)
				continue;

			currentZones.put(zoneJSON);
		}

		return currentZones.toString();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path(value = "/zone/{zoneId}")
	public Response updatesZone(@PathParam("zoneId") String zoneId, String content) {
		if (zoneId == null || zoneId.length() < 1) {
			return makeCORS(Response.status(404));
		}

		Zone zoneFound = _simulationMgr.getZone(zoneId);
		if (zoneFound == null)
			return makeCORS(Response.status(404));

		ZoneJSON zoneJSON = ZoneJSON.fromString(content);

		Position position = new Position(zoneJSON.getLeftX(), zoneJSON.getTopY());

		// TODO: Review for children zones
		if (!position.equals(zoneFound.getLeftTopAbsolutePosition()))
			try {
				zoneFound.setLeftTopRelativePosition(position);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		int width = zoneJSON.getRigthX() - zoneJSON.getLeftX();
		int height = zoneJSON.getBottomY() - zoneJSON.getTopY();

		if (zoneFound.getWidth() != width)
			try {
				zoneFound.setWidth(width);
			} catch (Exception e) {
				e.printStackTrace();
			}

		if (zoneFound.getHeight() != height)
			try {
				zoneFound.setHeight(height);
			} catch (Exception e) {
				e.printStackTrace();
			}

		return makeCORS(Response.ok(getZoneJSON(zoneFound).toString()));

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path(value = "/zone/")
	public Response createZone(String content) {

		ZoneJSON zoneJSON = ZoneJSON.fromString(content);

		int width = zoneJSON.getRigthX() - zoneJSON.getLeftX();
		int height = zoneJSON.getBottomY() - zoneJSON.getTopY();

		Zone newZone = _simulationMgr
		      .createZone(zoneJSON.getId(), zoneJSON.getLeftX(), zoneJSON.getTopY(), width, height);

		return makeCORS(Response.ok(getZoneJSON(newZone).toString()));

	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/zone/{zoneId}")
	public Response deleteZone(@PathParam("zoneId") String zoneId) {

		Zone zone = _simulationMgr.getZone(zoneId);

		if (zone == null)
			return makeCORS(Response.status(404));
		try {
			_simulationMgr.removeZone(zoneId);
		} catch (Exception e) {
			e.printStackTrace();
			return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));
		}
		return makeCORS(Response.ok());
	}

}
