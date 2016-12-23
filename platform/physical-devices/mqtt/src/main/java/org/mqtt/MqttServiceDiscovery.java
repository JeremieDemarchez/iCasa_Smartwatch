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

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.fuchsia.core.component.AbstractDiscoveryComponent;
import org.ow2.chameleon.fuchsia.core.component.DiscoveryIntrospection;
import org.ow2.chameleon.fuchsia.core.component.DiscoveryService;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclarationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.SmartwatchOperations;

/**
 * Hello world!
 *
 */
@Component
@Provides(specifications = {DiscoveryService.class,DiscoveryIntrospection.class})
public class MqttServiceDiscovery extends AbstractDiscoveryComponent implements MqttCallback{

	private static final Logger LOG = LoggerFactory.getLogger(MqttServiceDiscovery.class);

    @ServiceProperty(name = Factory.INSTANCE_NAME_PROPERTY)
	private String name;

	private final static String broker 				= MqttBroker.getUri();
	private final static String topicIotServices 	= MqttBroker.getTopicOfIotServices();
	private final static String clientId 			= "iCasa";
	
	private MqttClient mqttClient = null;
	
	
	protected MqttServiceDiscovery(BundleContext bundleContext) {
		super(bundleContext);
	}
    
	/**
	 * LifeCycle
	 * 
	 * When this component start, it connects a mqtt client to a broker and subscribes to a topic where the available iot services are published
	 */
	@Validate
	protected synchronized void start() {
         //int qos             = 2;
        // MemoryPersistence persistence = new MemoryPersistence();

		LOG.debug("MqttServiceDiscovery : component is starting.");
		System.out.println("MqttServiceDiscovery : component is starting.");

         try {
             mqttClient = new MqttClient(broker, clientId);
             
            // MqttConnectOptions connOpts = new MqttConnectOptions();
            // connOpts.setCleanSession(true);
             
             System.out.println("Connecting to broker: "+broker);
             mqttClient.connect();
             mqttClient.setCallback(this);
             System.out.println("Connected");
             
             mqttClient.subscribe(topicIotServices);
             System.out.println("Subscribed to topic: "+topicIotServices);
             
         } catch(MqttException me) {
             System.out.println("reason "+me.getReasonCode());
             System.out.println("msg "+me.getMessage());
             System.out.println("loc "+me.getLocalizedMessage());
             System.out.println("cause "+me.getCause());
             System.out.println("excep "+me);
             me.printStackTrace();
         }
	}
	
	
	/**
	 * LifeCycle
	 * 
	 * When this component stop, it closes the mqtt client
	 */
	@Invalidate
	protected synchronized void stop() {
		if(mqttClient != null && mqttClient.isConnected()){
			try {
				mqttClient.close();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
		LOG.debug("MqttServiceDiscovery : component is stopping.");
		System.out.println("MqttServiceDiscovery : component is stopping.");
	}

	
	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

		String message = mqttMessage.toString();
		LOG.debug("MqttServiceDiscovery : message arrived ! Topic = \""+topic+"\", and message = \""+message+"\"");
		System.out.println("MqttServiceDiscovery : message arrived ! Topic = \""+topic+"\", and message = \""+message+"\"");
		
		if(topic.equals(MqttServiceDiscovery.topicIotServices)){
			//System.out.println("MqttServiceDiscovery : interested by topic '"+topic+"' -> going to create ImportDeclaration");
			String idProvider = getPart(0, message);
			//System.out.println("getPart 0 done : idProvider == "+idProvider);
			String codeProvidedService = getPart(1, message);
			//System.out.println("getPart 1 done : codeProvidedService == "+codeProvidedService);
			//String providedService = SmartwatchOperations.getIcasaServiceName((int)Integer.parseInt(codeProvidedService));
			
			System.out.println("MqttServiceDiscovery : interested by topic '"+topic+"' -> provider id is "+idProvider+" and provided service is '"+SmartwatchOperations.getIcasaServiceName((int)Integer.parseInt(codeProvidedService))+"'");
			//String deviceType = getPart(2, message);
			createDeclaration(idProvider, codeProvidedService);
			//System.out.println("declaration created");
		}
		else{
			System.out.println("MqttServiceDiscovery : not interested by topic '"+topic+"'");
		}

	}
	
	/**
	 * Get each part of the message (each part are separated by "-").
	 * @param i : the part number
	 * @param message : the original message
	 * @return : the part i in the message
	 */
	private String getPart(int i, String message){
		String tmp = message;

		if(tmp.indexOf('-') == 0) tmp = tmp.substring(1);
		
		//String test = "-1";
		//System.out.println("test == "+test+", test.indexOf('-') == "+test.indexOf('-'));
		
		while(i>0){
			try{
			tmp = tmp.substring(tmp.indexOf("-")+1);
			i--;
			}catch(IndexOutOfBoundsException e){
				LOG.error("MqttServiceDiscovery : getPart("+i+", "+message+") throws IndexOutOfBoundsException.");
				System.out.println("MqttServiceDiscovery : getPart("+i+", "+message+") throws IndexOutOfBoundsException.");
				e.printStackTrace();
				return null;
			}
		}
		//System.out.println("tmp == "+tmp);
		int end = tmp.indexOf("-") != -1 ? tmp.indexOf("-") : tmp.length();
		//System.out.println("end == "+end);
		return tmp.substring(0, end);
	}
	
	
	private final void createDeclaration(String providerId, String serviceName) {
		
		System.out.println("MqttServiceDiscovery : create declaration for service '"+serviceName+"'");

		ImportDeclaration declaration = ImportDeclarationBuilder.empty()
				.key(MqttServiceDeclaration.PROVIDER_ID).value(providerId)
				.key(MqttServiceDeclaration.SERVICE_NAME).value(serviceName)
				.key("scope").value("generic")
				.key("protocol").value("mqtt")
				.build();

		LOG.debug("MqttServiceDiscovery : ImportDeclaration created for provider \""+providerId+"\" and service \""+serviceName+"\"");
		System.out.println("MqttServiceDiscovery : ImportDeclaration created for provider \""+providerId+"\" and service \""+serviceName+"\"");
		
		registerImportDeclaration(declaration);
	}
	

	@Override
	public void connectionLost(Throwable arg) {
		LOG.error("MqttServiceDiscovery : Connection lost...");
		arg.printStackTrace();
		//TODO : fermer tous les services mqtt instancié (iCasa a perdu la connexion au broker)
	}
	

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}

	
	@Override
	public String getName() {
		return name;
	}
    
}
