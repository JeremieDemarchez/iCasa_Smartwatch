package org.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttRequester implements MqttCallback{
	
	private static MqttRequester requester = null;
	
	private final static String broker 				= MqttBroker.getUri();
	private final static String consumerId 			= "iCasa";
	private MqttClient mqttClient = null;
	
	private List<Couple<String, Consumer<String[]>>> listCoupleTopicCallback = new ArrayList<Couple<String, Consumer<String[]>>>();
	
	
	private MqttRequester(){
		try {
             mqttClient = new MqttClient(broker, consumerId);
             System.out.println("Service connecting to broker: "+broker);
             mqttClient.connect();
             mqttClient.setCallback(this);
             System.out.println("Service connected");
             
         } catch(MqttException me) {
             System.out.println("reason "+me.getReasonCode());
             System.out.println("msg "+me.getMessage());
             System.out.println("loc "+me.getLocalizedMessage());
             System.out.println("cause "+me.getCause());
             System.out.println("excep "+me);
             me.printStackTrace();
         }
	}
	
	
	public static MqttRequester getInstance(){
		if(requester == null){
			requester = new MqttRequester();
		}
		return requester;
	}
	
	
	public void runRequest(Consumer<String[]> callback, String providerId, int methodCode, List<String> argList){
		String result = null;
		
		String listArgs = "";
		for(String arg : argList){
			listArgs += arg+"-";
		}
		listArgs = listArgs.substring(0, listArgs.length()-1);
		
		String topicResult = consumerId+methodCode+listArgs;
		String topicRequest = providerId;
		String request = topicResult;
		
        try {
			mqttClient.subscribe(topicResult);
            System.out.println("Service subscribed to topic: "+broker);
            
            MqttMessage message = new MqttMessage(request.getBytes());
            mqttClient.publish(topicRequest, message);
            
            Couple<String, Consumer<String[]>> coupleTopicCallback = new Couple<String, Consumer<String[]>>(topicResult, callback);
            listCoupleTopicCallback.add(coupleTopicCallback);
            
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		Couple toRemove = null;
		
		for(Couple<String, Consumer<String[]>> couple : listCoupleTopicCallback){
			
			if(couple.getFirst().equals(topic)){
				toRemove = couple;
				Consumer<String[]> callback = (Consumer<String[]>)couple.second;
				
				String[] toReturn = new String[2];
				toReturn[0] = topic;
				toReturn[1] = mqttMessage.toString();
				
				callback.accept(toReturn);
				break;
			}
		}
		if(toRemove != null) listCoupleTopicCallback.remove(toRemove);
	}
	

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private class Couple<T, U>{
		private T first;
		private U second;
		
		public Couple(T firstElement, U secondElement){
			first = firstElement;
			second = secondElement;
		}
		
		public T getFirst(){return first;}
		
		public U getSecond(){return second;}
		
		public void setFirst(T firstElement){first = firstElement;}
		
		public void setSecond(U secondElement){second = secondElement;}
	}
	
}