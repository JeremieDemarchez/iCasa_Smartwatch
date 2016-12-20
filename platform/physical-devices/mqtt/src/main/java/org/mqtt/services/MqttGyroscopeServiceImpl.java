package org.mqtt.services;

import java.util.function.Consumer;

import org.mqtt.MqttRequester;

import configuration.SmartwatchOperations;
import fr.liglab.adele.cream.annotations.entity.ContextEntity;

/**
 * Hello world!
 *
 */
@ContextEntity(services = { MqttGyroscopeService.class })
public class MqttGyroscopeServiceImpl implements MqttGyroscopeService{

	@ContextEntity.State.Field(service = MqttGyroscopeService.class,state = MqttGyroscopeService.PROVIDER_ID)
	private String providerId;
	
	@ContextEntity.State.Field(service = MqttGyroscopeService.class,state = MqttGyroscopeService.SERVICE_NAME)
	private String serviceName;
	


	@Override
	public String getProviderId() {
		return providerId;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}
	
	@Override
	public void askXYZAxisValues(Consumer<String[]> callback) {
		
		int codeMethod = SmartwatchOperations.getIcasaMethodCode("MqttGyroscopeServiceImpl", "askXYZAxisValues");
		MqttRequester.getInstance().runRequest(callback, providerId, codeMethod, null);
		//convert result in the appropriate format -> à la charge du développeur... bof bof comme méthode
	}

	@Override
	public void askHistory(Consumer<String[]> callback) {
		
		int codeMethod = SmartwatchOperations.getIcasaMethodCode("MqttGyroscopeServiceImpl", "askHistory");
		MqttRequester.getInstance().runRequest(callback, providerId, codeMethod, null);
	}

	@Override
	public void askDeviceType(Consumer<String[]> callback) {
		
		int codeMethod = SmartwatchOperations.getIcasaMethodCode("MqttGyroscopeServiceImpl", "askDeviceType");
		MqttRequester.getInstance().runRequest(callback, providerId, codeMethod, null);
	}
	
	
	
	
}
