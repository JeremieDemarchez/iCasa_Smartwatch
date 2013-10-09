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
package fr.liglab.adele.icasa.remote.impl;

import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.remote.util.IcasaJSONUtil;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: torito
 * Date: 4/10/13
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Component(name = "remote-rest-icasa")
@Instantiate(name = "remote-rest-icasa-0")
@Provides(specifications = { iCasaREST.class }, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value = "/backend")
public class iCasaREST extends AbstractREST{

    private BundleContext context;

    public iCasaREST(BundleContext _context){
        this.context = _context;
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    public Response versionOptions() {
        return makeCORS(Response.ok());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response info() {
        String info = null;
        try {
            info = getInfo();
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return makeCORS(Response.ok(info));
    }

    /**
     * Get the backend information
     * @return the information of the backend in a JSON format String
     * @throws JSONException if there is an error with the json object.
     */
    public String getInfo() throws JSONException {
        JSONObject backendInfo = new JSONObject();
        backendInfo.put("version", getVersion());
        return backendInfo.toString();
    }

    /**
     * Get the backend version obtained from the bundle context.
     * If the version has SNAPSHOT as qualifier, this method will return the maven way (major.minor.micro-SNAPSHOT)
     * @return the version of the icasa-remote bundle.
     */
    private String getVersion() {
        String qualifiedVersion = context.getBundle().getVersion().toString();
        return qualifiedVersion.replace(".SNAPSHOT", "-SNAPSHOT");//This is to maintain maven version format
    }
}
