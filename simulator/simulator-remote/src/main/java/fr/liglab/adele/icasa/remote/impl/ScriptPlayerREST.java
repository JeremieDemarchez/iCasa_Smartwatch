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

import fr.liglab.adele.icasa.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.script.executor.ScriptExecutor.State;
import fr.liglab.adele.icasa.simulator.SimulationManager;

/**
 * @author Thomas Leveque
 */
@Component(name="remote-rest-script-player")
@Instantiate(name="remote-rest-script-player-0")
@Provides(specifications={ScriptPlayerREST.class}, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value="/scriptPlayer/")
public class ScriptPlayerREST extends AbstractREST {

    public enum ScriptState {
        STARTED("started"), STOPPED("stopped"), PAUSED("paused");

        private String _stateStr;

        private ScriptState(String stateStr) {
            _stateStr = stateStr;
        }

        public String toString() {
            return _stateStr;
        }

        public static ScriptState fromString(String stateStr) {
            if (stateStr == null)
                return null;

            if (STARTED.toString().equals(stateStr))
                return STARTED;
            if (STOPPED.toString().equals(stateStr))
                return STOPPED;
            if (PAUSED.toString().equals(stateStr))
                return PAUSED;

            return null;
        }
    }

    @Requires
    private ScriptExecutor _scriptExecutor;
    
    @Requires
    private SimulationManager _simulationMgr;
        

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/scripts/")
    public Response scripts() {
        return makeCORS(Response.ok(getScripts()));
    }

    private JSONObject getScriptJSON(String scriptName) {

        State scriptState = _scriptExecutor.getState();
        ScriptState scriptStateJSON = ScriptState.STOPPED;
        String selectedScriptName = _scriptExecutor.getCurrentScript();
        if (scriptName.equals(selectedScriptName)) {
            if (State.EXECUTING.equals(scriptState))
                scriptStateJSON = ScriptState.STARTED;
            else if (State.PAUSED.equals(scriptState))
                scriptStateJSON = ScriptState.PAUSED;
            else if (State.STOPPED.equals(scriptState))
                scriptStateJSON = ScriptState.STOPPED;
        }

        JSONObject scriptJSON = null;
        try {
            scriptJSON = new JSONObject();
            scriptJSON.putOnce(ScriptJSON.ID_PROP, scriptName);
            scriptJSON.putOnce(ScriptJSON.NAME_PROP, scriptName);
            scriptJSON.putOnce(ScriptJSON.STATE_PROP, scriptStateJSON.toString());
            scriptJSON.putOnce(ScriptJSON.COMPLETE_PERCENT_PROP, _scriptExecutor.getExecutedPercentage());
        } catch (JSONException e) {
            e.printStackTrace();
            scriptJSON = null;
        }

        return scriptJSON;
    }

    /**
     * Returns a JSON array containing all scripts.
     *
     * @return a JSON array containing all scripts.
     */
    public String getScripts() {
        //boolean atLeastOne = false;
        JSONArray scripts = new JSONArray();
        for (String scriptName : _scriptExecutor.getScriptList()) {
            JSONObject script = getScriptJSON(scriptName);
            if (script == null)
                continue;

            scripts.put(script);
        }
        return scripts.toString();
    }

    /**
     * Retrieve a script.
     *
     * @param scriptId The ID of the script to retrieve
     * @return The required script,
     * return <code>null<code> if the script does not exist.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/script/{scriptId}")
    public Response script(@PathParam("scriptId") String scriptId) {
        if (scriptId == null || scriptId.length()<1){
            return makeCORS(Response.ok(getScripts()));
        }

        boolean scriptFound = _scriptExecutor.getScriptList().contains(scriptId);
        if (!scriptFound) {
            return makeCORS(Response.status(404));
        } else {
            JSONObject scriptJSON = getScriptJSON(scriptId);

            return makeCORS(Response.ok(scriptJSON.toString()));
        }
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/scripts/")
    public Response getScriptsOptions() {
        return makeCORS(Response.ok());
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/script/{scriptId}")
    public Response updatesScriptOptions(@PathParam("scriptId") String scriptId) {
        return makeCORS(Response.ok());
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/script/")
    public Response createsScriptOptions() {
        return makeCORS(Response.ok());
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/script/{scriptId}")
    public Response updatesScriptPut(@PathParam("scriptId") String scriptId, String content) {
        if (scriptId == null || scriptId.length()<1){
            return makeCORS(Response.status(404));
        }

        boolean scriptFound = _scriptExecutor.getScriptList().contains(scriptId);
        if (!scriptFound) {
            return makeCORS(Response.status(404));
        }

        String selectedScriptName = _scriptExecutor.getCurrentScript();
        boolean isSelected = scriptId.equals(selectedScriptName);
        State scriptState = _scriptExecutor.getState();
        if (!isSelected && !State.STOPPED.equals(scriptState)) {
            _scriptExecutor.stop();
            scriptState = State.STOPPED;
        }

        boolean isStarted = State.EXECUTING.equals(scriptState);
        boolean isStopped = State.STOPPED.equals(scriptState);
        boolean isPaused = State.PAUSED.equals(scriptState);

        ScriptJSON script = ScriptJSON.fromString(content);
        if (script != null) {

            ScriptState newScriptState = ScriptState.fromString(script.getState());
            if (ScriptState.STARTED.equals(newScriptState)) {
               if (isStopped) {
                   if ((script.getFactor() == null) && (script.getStartDate() == null))
                       _scriptExecutor.execute(scriptId);
                   else
                       _scriptExecutor.execute(scriptId, script.getStartDate(), script.getFactor());
               }
               else if (isPaused)
                   _scriptExecutor.resume();
            } else if (ScriptState.PAUSED.equals(newScriptState)) {
                if (isStarted)
                    _scriptExecutor.pause();
            } else if (ScriptState.STOPPED.equals(newScriptState)) {
                if (isStarted || isPaused)
                    _scriptExecutor.stop();
            }
        }

        JSONObject scriptJSON = getScriptJSON(scriptId);

        return makeCORS(Response.ok(scriptJSON.toString()));
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/script/")
    public Response createScript(String content) {
   	 
   	 ScriptJSON scriptJSON = ScriptJSON.fromString(content);   	 
   	 String fileName = scriptJSON.getId();
   	 
   	 _simulationMgr.saveSimulationState(fileName);
   	 
   	
       return makeCORS(Response.ok(scriptJSON.toString()));
    }
    
}
