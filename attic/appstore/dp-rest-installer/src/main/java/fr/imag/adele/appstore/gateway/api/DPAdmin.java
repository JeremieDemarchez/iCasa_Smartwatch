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
package fr.imag.adele.appstore.gateway.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.ow2.chameleon.json.JSONService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import fr.imag.adele.appstore.gateway.util.DPResourceManipulator;
import fr.liglab.adele.icasa.remote.AbstractREST;

@Component(name = "deployment-admin-rest-api")
@Instantiate(name = "deployment-admin-rest-api-0")
@Provides(specifications = { DPAdmin.class }, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value = "/")
public class DPAdmin {

	@Requires
	private JSONService jsonservice; // JsonService, in order to parse request
										// and build responses

	@Requires
	private DeploymentAdmin dadmin;

	protected static Logger coreLogger = LoggerFactory
			.getLogger("appstore.logger");

	private Response makeCORS(ResponseBuilder req) {
		ResponseBuilder rb = req
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS")
				.header("Access-Control-Expose-Headers",
						"X-Cache-Date, X-Atmosphere-tracking-id")
				.header("Access-Control-Allow-Headers",
						"Origin, Content-Type, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-Tracking-id, X-Atmosphere-Transport")
				.header("Access-Control-Max-Age", "-1")
				.header("Pragma", "no-cache");

		return rb.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/applications/")
	public Response getInstalledApplications() {
		DeploymentPackage[] installed = dadmin.listDeploymentPackages();
		List<Map<String, String>> installedInfo = DPResourceManipulator
				.toList(installed);
		String result = jsonservice.toJSON(Collections.singletonMap(
				"installedPackages", installedInfo));
		return makeCORS(Response.ok(result));
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/applications/")
	public Response getInstalledApplicationsOptions() {
		return makeCORS(Response.ok());
	}
	
	@OPTIONS
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/applications/{name}")
	public Response getApplicationOptions(@PathParam("name") String name) {
		return makeCORS(Response.ok());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/applications/{name}")
	public Response getApplication(@PathParam("name") String name) {
		DeploymentPackage ipackage = dadmin.getDeploymentPackage(name);
		if (ipackage == null) {
			return makeCORS(Response.status(Status.NOT_FOUND));
		}
		String result = jsonservice.toJSON(DPResourceManipulator
				.toMap(ipackage));
		return makeCORS(Response.ok(result));
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/applications/stream/")
	public Response installApplication(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		DeploymentPackage newPackage = null;
		try {
			newPackage = dadmin.installDeploymentPackage(uploadedInputStream);
		} catch (DeploymentException e) {
			e.printStackTrace();
			return makeCORS(Response.status(Status.INTERNAL_SERVER_ERROR));
		}
		return makeCORS(Response.ok(
				jsonservice.toJSON(DPResourceManipulator.toMap(newPackage)))
				);
	}
	@POST
	@Path("/applications/url/")
	public Response installApplication(@FormParam("location")String location) {
		System.out.println("Gateway will install application " + location);
		DeploymentPackage newPackage = null;
		URL file = null;
		try {
			file = new URL(location);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return makeCORS(Response.status(Status.INTERNAL_SERVER_ERROR));
		}
		try {
			newPackage = dadmin.installDeploymentPackage(file.openStream());
		} catch (DeploymentException e) {
			e.printStackTrace();
			return makeCORS(Response.status(Status.INTERNAL_SERVER_ERROR));
		} catch (IOException e) {
			e.printStackTrace();
			return makeCORS(Response.status(Status.INTERNAL_SERVER_ERROR));
		}
		return makeCORS(Response.ok(
				jsonservice.toJSON(DPResourceManipulator.toMap(newPackage)))
				);
	}

	@DELETE
	@Path("/applications/{name}")
	public Response removeApplication(@PathParam("name") String name) {
		DeploymentPackage ipackage = dadmin.getDeploymentPackage(name);
		if (ipackage == null) {
			return makeCORS(Response.status(Status.NOT_FOUND));
		}
		try {
			ipackage.uninstallForced();
		} catch (DeploymentException e) {
			e.printStackTrace();
			return makeCORS(Response.status(Status.INTERNAL_SERVER_ERROR));
		}
		String result = jsonservice.toJSON(DPResourceManipulator
				.toMap(ipackage));
		return makeCORS(Response.ok(result));
	}

}
