package org.mqtt.services;

import java.util.function.Consumer;

import fr.liglab.adele.cream.annotations.entity.ContextEntity;
import org.mqtt.services.MqttStepCounterService;

@ContextEntity(services = { MqttStepCounterService.class })
public class MqttStepCounterServiceImpl implements MqttStepCounterService{

	@ContextEntity.State.Field(service = MqttStepCounterService.class,state = MqttStepCounterService.PROVIDER_ID)
	private String providerId;
	
	@ContextEntity.State.Field(service = MqttStepCounterService.class,state = MqttStepCounterService.SERVICE_NAME)
	private String serviceName;
	

	@Override
	public String getProviderId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void askDeviceType(Consumer<String[]> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void askNumberOfStep(Consumer<String[]> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void askHistoryOfStepCounter(Consumer<String[]> callback) {
		// TODO Auto-generated method stub
		
	}

}
