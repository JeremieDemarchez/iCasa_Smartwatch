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
        	return FuchsiaUtils.getFilter("(&(scope=generic)(protocol=mqtt)(id=*)(port=*))");
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