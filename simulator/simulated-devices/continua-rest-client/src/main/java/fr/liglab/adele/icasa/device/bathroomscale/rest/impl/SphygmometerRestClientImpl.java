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
package fr.liglab.adele.icasa.device.bathroomscale.rest.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import fr.liglab.adele.icasa.device.bathroomscale.rest.api.SphygmometerRestAPI;

@Component(name = "SphygmometerRestClient")
@Provides
public class SphygmometerRestClientImpl implements SphygmometerRestAPI {

	// 10.194.3.114
	@Property(name = "url", value = "http://localhost:8080/restAdapter/rest/continua")
	private String url;

	@Property(name = "hl7bathroomScaleFile")
	private String hl7bathroomScaleFile;

	@Requires
	private IHL7MessageFileInstaller fileInstaller;

	private Client c = Client.create();

	@Override
   public boolean sendMeasure(int systolic, int diastolic, int pulsations) {

		if (hl7bathroomScaleFile == null)
			return false;

		WebResource r = c.resource(url);

		String hl7message = createMessage(systolic, diastolic, pulsations);

		try {
			String response = r.accept(MediaType.TEXT_PLAIN_TYPE).put(String.class, hl7message);
			// TODO parse response for computing return value
			return true;
		} catch (UniformInterfaceException ue) {
			ClientResponse response = ue.getResponse();
			// TODO parse response for computing return value
			return false;
		}
   }


	private String createMessage(int systolic, int diastolic, int pulsations) {
		String data = fileInstaller.getFileContent(hl7bathroomScaleFile);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");		
		String timestamp = formatter.format(new Date()); 
		
		data = data.replace("$date", timestamp);
		data = data.replace("$systolic", ""+systolic);
		data = data.replace("$diastolic", "" + diastolic);
		data = data.replace("$average", "" + ((systolic + diastolic)/2));
		data = data.replace("$pulsations", "" + pulsations);
		
		
		return data;
	}


}
