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
package fr.liglab.adele.icasa.device.bathroomscale.rest.impl;

import java.text.SimpleDateFormat;
import java.util.Date;


import javax.ws.rs.core.MediaType;

import fr.liglab.adele.icasa.service.preferences.Preferences;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import fr.liglab.adele.icasa.device.bathroomscale.rest.api.BathroomScaleRestAPI;

@Component(name = "BathroomScaleRestClient")
@Provides
public class BathroomScaleRestClientImpl implements BathroomScaleRestAPI {

	// 10.194.3.114
	@Property(name = "url", value = "http://localhost:8080/restAdapter/rest/continua")
	private String url;

	@Property(name = "hl7templateFile")
	private String hl7templateFile;

	@Requires
	private IHL7MessageFileInstaller fileInstaller;

    @Requires
    private Preferences preferences;

	private Client c = Client.create();

	public boolean sendMeasure(float weight) {

		if (hl7templateFile == null)
			return false;

		WebResource r = c.resource(url);

		String hl7message = createMessage(weight);
		
		System.out.println("Send HL7 message to  " + url + ": " + hl7message);

		try {
			String response = r.accept(MediaType.TEXT_PLAIN_TYPE).put(String.class, hl7message);
			System.out.println("Response : " +response);
			return true;
		} catch (UniformInterfaceException ue) {
			ClientResponse response = ue.getResponse();
			System.out.println("Response : " +response);
			return false;
		}
	}

	private String createMessage(float weight) {
		String data = fileInstaller.getFileContent(hl7templateFile);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");		
		String timestamp = formatter.format(new Date()); 
		data = data.replace("$date", timestamp);
		data = data.replace("$weight", "" + ((Float)weight).intValue());
        data = data.replace("$name", getProperty("user.name", "Elizabeth") );
        data = data.replace("$middlename", getProperty("user.middleName", "Gzlez") );
        data = data.replace("$lastname", getProperty("user.lastName", "Doe") );
		return data;
	}

    private String getProperty(String property, String defaultValue){
        String userName = (String) preferences.getGlobalPropertyValue(property);
        if (userName != null) {
            return userName;
        } else {
            return defaultValue;
        }
    }
}
