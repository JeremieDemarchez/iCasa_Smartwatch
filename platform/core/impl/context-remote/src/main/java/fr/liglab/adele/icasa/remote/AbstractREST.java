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
package fr.liglab.adele.icasa.remote;

import javax.ws.rs.core.Response;

public abstract class AbstractREST {

	public static final String ICASA_REST_PROPERTY_NAME = "iCasa-REST";
	
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
