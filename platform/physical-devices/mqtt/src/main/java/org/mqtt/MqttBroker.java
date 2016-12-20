package org.mqtt;

public class MqttBroker {
	private final static String ADDRESS 			= "tcp://iot.eclipse.org";
	private final static String PORT 				= "1883";
	private final static String topicIotService 	= "IOT/AVAILABLE_SERVICES";
	
	public static String getUri(){
		return ADDRESS+":"+PORT;
	}
	public static String getTopicOfIotServices(){
		return topicIotService;
	}
}
