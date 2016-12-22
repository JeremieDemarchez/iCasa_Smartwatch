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
package org.mqtt;

import java.util.Map;

import org.osgi.framework.Filter;
import org.ow2.chameleon.fuchsia.core.FuchsiaUtils;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.exceptions.BinderException;
import org.ow2.chameleon.fuchsia.core.exceptions.InvalidFilterException;

/**
 * Hello world!
 *
 */
public class MqttServiceDeclaration {
	
	private final static Filter MATCHING_DECLARATION_FILTER = buildMatchFilter();

    private String providerId;
    private String serviceCode;
    
    public final static String PROVIDER_ID = "mqtt.providerId";
    public final static String SERVICE_NAME = "mqtt.serviceName";


    private static Filter buildMatchFilter() {
    	
        try {
        	return FuchsiaUtils.getFilter("(&(scope=generic)(protocol=mqtt))");
        } catch (InvalidFilterException e) {
            throw new IllegalStateException(e);
        }
    }

    public static MqttServiceDeclaration from(ImportDeclaration importDeclaration) throws BinderException {
        
    	Map<String, Object> metadata = importDeclaration.getMetadata();

        if (!MATCHING_DECLARATION_FILTER.matches(metadata)) {
            throw new BinderException("Not enough information in the metadata to be used by the mqtt importer");
        }
        
        MqttServiceDeclaration declaration = new MqttServiceDeclaration();

        declaration.providerId		= (String) metadata.get(PROVIDER_ID);
        declaration.serviceCode		= (String) metadata.get(SERVICE_NAME);
        
        return declaration;
    }


    private MqttServiceDeclaration() {
    	super();
    }
    
    public String getProviderId() {
        return providerId;
    }
    
    public String getServiceCode() {
        return serviceCode;
    }
    
    public static String createDeclarationId(String providerId, String serviceName){
    	return providerId+"-"+serviceName;
    }
}