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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import utils.DateTool;

public class MqttRequester implements MqttCallback{
	
	private final static String broker 			= MqttBroker.getUri();
	private final static String consumerId 		= "iCasa_MqttRequester_"+(DateTool.getDateAsString().replaceAll(" ", "_"));
	private MqttClient mqttClient 				= null;
	
	private List<Couple<String, Consumer<String[]>>> listCoupleTopicCallback = new ArrayList<Couple<String, Consumer<String[]>>>();
	
	
	public MqttRequester(){
		try {
             mqttClient = new MqttClient(broker, consumerId);
             System.out.println("MqttRequester connecting to broker '"+broker+"' with id '"+consumerId+"' (mqttClient = "+mqttClient+").");
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
	
	
	public void runRequest(Consumer<String[]> callback, String providerId, int methodCode, List<String> argList){

		System.out.println("MqttRequester : runRequest with provider id = '"+providerId+"', methodCode = "+methodCode);
		
		//récupération des arguments
		String listArgs = "";
		if(argList != null){
			for(String arg : argList){
				listArgs += arg+"-";
			}
			listArgs = listArgs.substring(0, listArgs.length()-1);
		}
		
		String topicResult = consumerId+"-"+methodCode+"-"+listArgs;
		String topicRequest = providerId;
		String request = topicResult;
		
        try {//abonnement au topic sur lequel le résultat sera retourné
        	System.out.println("Client "+mqttClient+" is subscribing to topic '"+topicResult+"' ...");
			mqttClient.subscribe(topicResult);
            System.out.println("MqttRequester : subscribed to topic: '"+topicResult+"' for getting result for request '"+request+"' on topic '"+topicRequest+"'.");
            
            //création et publication du message
            MqttMessage message = new MqttMessage(request.getBytes());
            mqttClient.publish(topicRequest, message);
            
            //enregistrement du topic et du callback à appeler lorsqu'on recevra la réponse.
            Couple<String, Consumer<String[]>> coupleTopicCallback = new Couple<String, Consumer<String[]>>(topicResult, callback);
            listCoupleTopicCallback.add(coupleTopicCallback);
            
		} catch (MqttException e) {
			System.err.println("MqttRequester : runRequest failed -> "+e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("MqttRequester : runRequest failed -> "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		Couple<String, Consumer<String[]>> toRemove = null;
		
		try{
			System.out.println("MqttRequester : message arrived on topic '"+topic+"' : message = '"+mqttMessage.toString()+"'.");
			
			for(Couple<String, Consumer<String[]>> couple : listCoupleTopicCallback){
	
				//si c'est une réponse à une de nos requètes alors on appel le callback avec le topic et le résultat
				if(couple.getFirst().equals(topic)){
					toRemove = couple;
					Consumer<String[]> callback = (Consumer<String[]>)couple.second;
					
					String[] toReturn = new String[2];
					toReturn[0] = topic;
					toReturn[1] = mqttMessage.toString();
					
					mqttClient.unsubscribe(topic);
					callback.accept(toReturn);
					break;
				}
			}
			if(toRemove != null) listCoupleTopicCallback.remove(toRemove);
		}catch(MqttException e){
			System.err.println("MqttRequester : failed to interpret arrived message ... "+e.getMessage());
			e.printStackTrace();
		}
	}
	

	@Override
	public void connectionLost(Throwable arg)  {
		try{
			//TODO : supprimer le service mqtt associé (icasa a perdu la connexion au broker)
			System.err.println("MqttRequester : connection lost ... Try to reconnect ...");
			
			mqttClient.connect();

			System.err.println("MqttRequester : connected.");
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			System.err.println("MqttRequester : reconnection failed ...");
			e.printStackTrace();
		}
	}
	

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		
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