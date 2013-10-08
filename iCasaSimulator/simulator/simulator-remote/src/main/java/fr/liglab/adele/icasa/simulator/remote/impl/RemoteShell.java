/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
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

import fr.liglab.adele.icasa.commands.ICasaCommand;
import fr.liglab.adele.icasa.commands.Signature;
import fr.liglab.adele.icasa.remote.AbstractREST;
import org.apache.felix.ipojo.annotations.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: garciai@imag.fr
 * Date: 9/26/13
 * Time: 3:35 PM
 */
@Component(name="remote-rest-shell")
@Instantiate(name="remote-rest-shell-0")
@Provides(specifications={RemoteShell.class}, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value="/shell/")
public class RemoteShell extends AbstractREST {

    /**
     * Simulator commands added to the platorm
     */
    private Map<String, ICasaCommand> commands = new HashMap<String, ICasaCommand>();

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/execute/{name}")
    public Response executeCommandOption(@PathParam("name") String name) {
        return makeCORS(Response.ok());
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/commands/")
    public Response getCommandsOption() {
        return makeCORS(Response.ok());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/execute/{name}")
    public Response executeCommand(@PathParam("name") String name, String content) {

        //help
        if(name.compareTo("help") == 0){
            return getCommands();
        }

        ICasaCommand command = commands.get(name);
        if (command == null){
            return makeCORS(Response.status(Response.Status.NOT_FOUND));
        }

        JSONObject receivedParams;
        StringBuffer result = new StringBuffer();
        JSONObject params;

        //build parameters in the expected format.
        try {
            receivedParams = new JSONObject(content);
            List<String> arguments = getReceivedParams(receivedParams);
            System.out.println("args" + arguments);
            params = getParamsInJSON(command, arguments);
        } catch (JSONException e) {
            e.printStackTrace();
            return makeCORS(Response.serverError());
        }

        //create OutputStream to retrieve messages.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        try {
            command.execute(System.in, ps, params);
        } catch (Exception e) {
            result.append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        //created returned json object.
        result.append(new String(byteArrayOutputStream.toByteArray()));
        JSONObject returnedObject = new JSONObject();
        try {
            returnedObject.put("result", result.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return makeCORS(Response.serverError());
        }
        return makeCORS(Response.ok(returnedObject.toString()));
    }

    private List getReceivedParams(JSONObject params) throws JSONException {
        JSONArray stringParameters = null;
        List list = new ArrayList();
        stringParameters = params.getJSONArray("parameters");

        for (int i=0; i<stringParameters.length(); i++) {
                list.add( stringParameters.getString(i) );
        }
        return list;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/commands/")
    public Response getCommands() {
        JSONObject result;
        try {
            result = getCommandsInJSON();
        } catch (JSONException e) {
            e.printStackTrace();
            return makeCORS(Response.serverError());
        }
        return makeCORS(Response.ok(result.toString()));
    }

    private JSONObject getCommandsInJSON() throws JSONException {
        JSONObject result = new JSONObject();
        //JSONArray list = new JSONArray();
        StringBuilder builder = new StringBuilder();
        for(String name :commands.keySet()){
            //JSONObject command = new JSONObject();
            String description = "unknown";
            //command.put("name", name);
            ICasaCommand icasaCommand = commands.get(name);
            if(icasaCommand != null){
                description = icasaCommand.getDescription();
            }
            builder.append(name).append("\n").append("\t").append(description).append("\n");
            //command.put("description", description);
            //list.put(command);
        }
        result.put("result", builder.toString());
        return result;
    }

    private JSONObject getParamsInJSON(ICasaCommand command, List arguments) throws JSONException {
        JSONObject params = new JSONObject();
        int argumentsSize = (arguments!=null)?arguments.size() : 0;
        System.out.println("Command" + command.getName());
        System.out.println("Args size" + argumentsSize);
        Signature signature =  command.getSignature(argumentsSize);
        if (signature != null){
            String paramsNames[] = signature.getParameters();
            if (arguments != null) {
                for (int i = 0; i < paramsNames.length && argumentsSize > i; i++) {
                    params.put(paramsNames[i], arguments.get(i));
                }
            }
        }
        return params;
    }

    // -- Component bind methods -- //
    @Bind(id = "commands", aggregate = true, optional = true)
    public void bindCommand(ICasaCommand commandService) {
        String name = commandService.getName();
        commands.put(name, commandService);
    }

    @Unbind(id = "commands")
    public void unbindCommand(ICasaCommand commandService) {
        String name = commandService.getName();
        commands.remove(name);
    }

}
