/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.remote.impl;

import javax.ws.rs.Consumes;
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
import org.json.JSONObject;

import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.simulator.remote.util.IcasaSimulatorJSONUtil;
import fr.liglab.adele.icasa.simulator.remote.util.ScriptJSON;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor.State;

/**
 * @author Thomas Leveque
 */
@Component(name = "remote-rest-script-player")
@Instantiate(name = "remote-rest-script-player-0")
@Provides(specifications = { ScriptPlayerREST.class }, properties = { @StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value = "true", type = "java.lang.Boolean") })
@Path(value = "/scriptPlayer/")
public class ScriptPlayerREST extends AbstractREST {


	@Requires
	private ScriptExecutor _scriptExecutor;


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/scripts/")
	public Response scripts() {
		return makeCORS(Response.ok(getScripts()));
	}

	/*
	 * private JSONObject getScriptJSON(String scriptName) {
	 * 
	 * State scriptState = _scriptExecutor.getCurrentScriptState(); ScriptState
	 * scriptStateJSON = ScriptState.STOPPED; String selectedScriptName =
	 * _scriptExecutor.getCurrentScript(); if
	 * (scriptName.equals(selectedScriptName)) { if
	 * (State.EXECUTING.equals(scriptState)) scriptStateJSON =
	 * ScriptState.STARTED; else if (State.PAUSED.equals(scriptState))
	 * scriptStateJSON = ScriptState.PAUSED; else if
	 * (State.STOPPED.equals(scriptState)) scriptStateJSON = ScriptState.STOPPED;
	 * }
	 * 
	 * JSONObject scriptJSON = null; try { scriptJSON = new JSONObject();
	 * scriptJSON.putOnce(ScriptJSON.ID_PROP, scriptName);
	 * scriptJSON.putOnce(ScriptJSON.NAME_PROP, scriptName);
	 * scriptJSON.putOnce(ScriptJSON.STATE_PROP, scriptStateJSON.toString());
	 * scriptJSON.putOnce(ScriptJSON.COMPLETE_PERCENT_PROP,
	 * _scriptExecutor.getExecutedPercentage());
	 * scriptJSON.putOnce("actionNumber",
	 * _scriptExecutor.getActionsNumber(scriptName)); //
	 * scriptJSON.putOnce("startDate", //
	 * _scriptExecutor.getStartDate(scriptName)); scriptJSON.putOnce("factor",
	 * _scriptExecutor.getFactor(scriptName));
	 * scriptJSON.putOnce("executionTime",
	 * _scriptExecutor.getExecutionTime(scriptName));
	 * 
	 * } catch (JSONException e) { e.printStackTrace(); scriptJSON = null; }
	 * 
	 * return scriptJSON; }
	 */

	/**
	 * Returns a JSON array containing all scripts.
	 * 
	 * @return a JSON array containing all scripts.
	 */
	public String getScripts() {
		JSONArray scripts = new JSONArray();
		for (String scriptName : _scriptExecutor.getScriptList()) {
			JSONObject script = IcasaSimulatorJSONUtil.getScriptJSON(scriptName, _scriptExecutor);
			if (script == null)
				continue;
			scripts.put(script);
		}
		return scripts.toString();
	}

	/**
	 * Retrieve a script.
	 * 
	 * @param scriptId
	 *           The ID of the script to retrieve
	 * @return The required script, return
	 *         <code>null<code> if the script does not exist.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/script/{scriptId}")
	public Response script(@PathParam("scriptId") String scriptId) {
		if (scriptId == null || scriptId.length() < 1) {
			return makeCORS(Response.ok(getScripts()));
		}

		boolean scriptFound = _scriptExecutor.getScriptList().contains(scriptId);
		if (!scriptFound) {
			return makeCORS(Response.status(404));
		} else {
			JSONObject scriptJSON = IcasaSimulatorJSONUtil.getScriptJSON(scriptId, _scriptExecutor);
			return makeCORS(Response.ok(scriptJSON.toString()));
		}
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/scripts/")
	public Response getScriptsOptions() {
		return makeCORS(Response.ok());
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/script/{scriptId}")
	public Response updatesScriptOptions(@PathParam("scriptId") String scriptId) {
		return makeCORS(Response.ok());
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/script/")
	public Response createsScriptOptions() {
		return makeCORS(Response.ok());
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path(value = "/script/{scriptId}")
	public Response updatesScriptPut(@PathParam("scriptId") String scriptId, String content) {
		if (scriptId == null || scriptId.length() < 1) {
			return makeCORS(Response.status(404));
		}

		boolean scriptFound = _scriptExecutor.getScriptList().contains(scriptId);
		if (!scriptFound) {
			return makeCORS(Response.status(404));
		}

		ScriptJSON script = ScriptJSON.fromString(content);

		if (script == null)
			return makeCORS(Response.status(404));

		String currentScriptName = _scriptExecutor.getCurrentScript();
		State currentScriptState = _scriptExecutor.getCurrentScriptState();
		State newState = State.fromString(script.getState());
		
		if (scriptId.equals(currentScriptName)) { // Same script
			if (currentScriptState == State.STARTED) {				
				if (State.STOPPED == newState)
					_scriptExecutor.stop();
				if (State.PAUSED == newState)
					_scriptExecutor.pause();
			} else if (currentScriptState == State.PAUSED) {
				if (State.STARTED == newState)
					_scriptExecutor.resume();
			} else if (currentScriptState == State.STOPPED) {
				if (State.STARTED == newState)
					_scriptExecutor.execute(scriptId);
			} else {
				return makeCORS(Response.status(Response.Status.SERVICE_UNAVAILABLE));
			}				
		} else { // New script
			if (currentScriptState == State.STOPPED)
				if (State.STARTED == newState)
					_scriptExecutor.execute(scriptId);
			else
				return makeCORS(Response.status(Response.Status.SERVICE_UNAVAILABLE));
		}

		JSONObject scriptJSON = IcasaSimulatorJSONUtil.getScriptJSON(scriptId, _scriptExecutor);

		return makeCORS(Response.ok(scriptJSON.toString()));
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path(value = "/script/")
	public Response createScript(String content) {

		ScriptJSON scriptJSON = ScriptJSON.fromString(content);
		String fileName = scriptJSON.getId();

		//_simulationMgr.saveSimulationState(fileName);
		_scriptExecutor.saveSimulationScript(fileName);

		return makeCORS(Response.ok(scriptJSON.toString()));
	}

}
