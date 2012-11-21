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

import fr.liglab.adele.icasa.device.GenericDevice;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import fr.liglab.adele.icasa.script.executor.ScriptExecutor;
import fr.liglab.adele.icasa.script.executor.ScriptExecutor.State;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * @author Thomas Leveque
 */
@Component(name="remote-rest-script-player")
@Instantiate(name="remote-rest-script-player-0")
@Provides(specifications={ScriptPlayerREST.class})
@Path(value="/scriptPlayer/")
public class ScriptPlayerREST {

    public enum ScriptState {
        STARTED("started"), STOPPED("stopped"), PAUSED("paused");

        private String _stateStr;

        ScriptState(String stateStr) {
            _stateStr = stateStr;
        }

        public String toString() {
            return _stateStr;
        }
    }

    @Requires
    private ScriptExecutor _scriptExecutor;

    /*
     * Methods to manage cross domain requests
     */
    private String _corsHeaders;

    private Response makeCORS(Response.ResponseBuilder req, String returnMethod) {
        Response.ResponseBuilder rb = req
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Pragma", "no-cache");

        if (!"".equals(returnMethod)) {
            rb.header("Access-Control-Allow-Headers", returnMethod);
        }

        return rb.build();
    }

    private Response makeCORS(Response.ResponseBuilder req) {
        return makeCORS(req, _corsHeaders);
    }

    @GET
    @Produces("application/json")
    @Path(value="/scripts/")
    public Response zones() {
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
            scriptJSON.putOnce("id", scriptName);
            scriptJSON.putOnce("name", scriptName);
            scriptJSON.putOnce("state", scriptStateJSON.toString());
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
        boolean atLeastOne = false;
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
     * @return The required device,
     * return <code>null<code> if the script does not exist.
     */
    @GET
    @Produces("application/json")
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

    /**
     * Retrieve a script.
     *
     * @param scriptId The ID of the script to retrieve
     * @return The required device,
     * return <code>null<code> if the script does not exist.
     */
    @PUT
    @Produces("application/json")
    @Path(value="/script/{scriptId}")
    public Response updatesScript(@PathParam("scriptId") String scriptId, @FormParam("state") String state) {
        if (scriptId == null || scriptId.length()<1){
            return makeCORS(Response.status(404));
        }

        boolean scriptFound = _scriptExecutor.getScriptList().contains(scriptId);
        if (!scriptFound) {
            return makeCORS(Response.status(404));
        } else {
            String selectedScriptName = _scriptExecutor.getCurrentScript();
            boolean isSelected = scriptId.equals(selectedScriptName);
            State scriptState = _scriptExecutor.getState();
            if (!isSelected && !State.STOPPED.equals(scriptState)) {
                _scriptExecutor.stop();
                scriptState = State.STOPPED;
            }

            boolean isStarted = State.EXECUTING.equals(scriptState);
            boolean isStopped = State.STOPPED.equals(scriptState);
            boolean isPaused = State.STOPPED.equals(scriptState);

            if (ScriptState.STARTED.equals(state)) {
               if (isStopped)
                   _scriptExecutor.execute(scriptId); //TODO manage factor and start timestamp
               else if (isPaused)
                   _scriptExecutor.resume();
            } else if (ScriptState.PAUSED.equals(state)) {
                if (isStarted)
                    _scriptExecutor.pause();
            } else if (ScriptState.STOPPED.equals(state)) {
                if (isStarted || isPaused)
                    _scriptExecutor.stop();
            }

            JSONObject scriptJSON = getScriptJSON(scriptId);

            return makeCORS(Response.ok(scriptJSON.toString()));
        }
    }
}
