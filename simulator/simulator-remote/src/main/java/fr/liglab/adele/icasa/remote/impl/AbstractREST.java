package fr.liglab.adele.icasa.remote.impl;

import javax.ws.rs.core.Response;

public abstract class AbstractREST {

	
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
