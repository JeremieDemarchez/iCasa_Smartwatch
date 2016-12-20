package org.mqtt;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.mqtt.importer.MqttServiceImporter;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.fuchsia.core.component.AbstractDiscoveryComponent;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclaration;
import org.ow2.chameleon.fuchsia.core.declaration.ImportDeclarationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
@Component
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

         try {
             mqttClient = new MqttClient(broker, clientId);
             
            // MqttConnectOptions connOpts = new MqttConnectOptions();
            // connOpts.setCleanSession(true);
             
             System.out.println("Connecting to broker: "+broker);
             mqttClient.connect();
             mqttClient.setCallback(this);
             System.out.println("Connected");
             
             mqttClient.subscribe(topicIotServices);
             System.out.println("Subscribed to topic: "+broker);
             
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
	}

	
	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

		String message = mqttMessage.toString();
		LOG.debug("MqttServiceDiscovery : message arrived ! Topic = \""+topic+"\", and message = \""+message+"\"");
		
		if(topic.equals(this.topicIotServices)){
			//TODO : décortiquer le message pour récupérer les infos utiles
			String idProvider = getPart(0, message);
			String providedService = getPart(1, message);
			//String deviceType = getPart(2, message);
			createDeclaration(idProvider, providedService);
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
		
		while(i>0){
			try{
			tmp = tmp.substring(tmp.indexOf("-"));
			}catch(IndexOutOfBoundsException e){
				LOG.error("MqttServiceDiscovery : getPart("+i+", "+message+") throws IndexOutOfBoundsException.");
				e.printStackTrace();
				return null;
			}
		}
		
		int end = tmp.indexOf("-") != -1 ? tmp.indexOf("-") : tmp.length();
		return tmp.substring(0, end);
	}
	
	
	private final void createDeclaration(String providerId, String serviceName) {

		ImportDeclaration declaration = ImportDeclarationBuilder.empty()
				.key(MqttServiceDeclaration.PROVIDER_ID).value(providerId)
				.key(MqttServiceDeclaration.SERVICE_NAME).value(serviceName)
				.key("scope").value("generic")
				.key("library").value("mqtt")
				.build();

		LOG.debug("MqttServiceDiscovery : ImportDeclaration created for provider \""+providerId+"\" and service \""+serviceName+"\"");
		
		registerImportDeclaration(declaration);
	}
	

	@Override
	public void connectionLost(Throwable arg0) {
		LOG.error("MqttServiceDiscovery : Connection lost.");
	}
	

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}

	
	@Override
	public String getName() {
		return name;
	}
    
}
