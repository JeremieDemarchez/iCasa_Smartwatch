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
package fr.liglab.adele.icasa.application.remote;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
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

import fr.liglab.adele.icasa.application.Application;
import fr.liglab.adele.icasa.application.ApplicationManager;
import fr.liglab.adele.icasa.application.remote.util.ApplicationJSONUtil;

/**
 * @author Gabriel Pedraza Ferreira
 */
@Component(name = "remote-rest-app")
@Instantiate(name = "remote-rest-app-0")
@Provides(specifications = { ApplicationREST.class }, properties = {@StaticServiceProperty(name = "iCasa-REST", value="true", type="java.lang.Boolean")} )
@Path(value = "/apps/")
public class ApplicationREST {

	@Requires
	private ApplicationManager _applicationMgr;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/apps/")
	public Response applications() {
		return makeCORS(Response.ok(getApplications()));
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/apps/{AppId}")
	public Response updatesAppsOptions(@PathParam("AppId") String zoneId) {
		return makeCORS(Response.ok());
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/app/")
	public Response createsApplicationsOptions() {
		return makeCORS(Response.ok());
	}

	/**
	 * Retrieves a zone.
	 * 
	 * @param appId
	 *           The ID of the zone to retrieve
	 * @return The required zone
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/app/{appId}")
	public Response getApplication(@PathParam("appId") String appId) {
		if (appId == null || appId.length() < 1) {
			return makeCORS(Response.ok());
		}

		Application app = _applicationMgr.getApplication(appId);
		
		if (app == null) {
			return makeCORS(Response.status(404));
		} else {
			JSONObject appJSON = ApplicationJSONUtil.getApplicationJSON(app);
			return makeCORS(Response.ok(appJSON.toString()));
		}
	}

	/**
	 * Returns a JSON array containing all applications.
	 * 
	 * @return a JSON array containing all applications.
	 */
	private String getApplications() {
		JSONArray currentApps = new JSONArray();
		
		List<Application>  apps = _applicationMgr.getApplications();
        currentApps.put(ApplicationJSONUtil.getEmptyApplication());//add at least one application: with NONE
		for (Application app : apps) {
			JSONObject appJSON = ApplicationJSONUtil.getApplicationJSON(app);
			if (appJSON==null) {
				continue;
			}
			currentApps.put(appJSON);
      }
		return currentApps.toString();
	}


	
	protected Response makeCORS(Response.ResponseBuilder req) {
		Response.ResponseBuilder rb = req
		      .header("Access-Control-Allow-Origin", "*")
		      .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
		      .header("Access-Control-Expose-Headers", "X-Cache-Date, X-Atmosphere-tracking-id")
		      .header("Access-Control-Allow-Headers", "Origin, Content-Type, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-Tracking-id, X-Atmosphere-Transport")
		      .header("Access-Control-Max-Age", "-1")
		      .header("Pragma", "no-cache");

		return rb.build();
	}

}
