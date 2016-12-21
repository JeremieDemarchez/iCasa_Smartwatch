package org.mqtt.services;

import java.util.function.Consumer;

import fr.liglab.adele.cream.annotations.ContextService;

public @ContextService interface MqttStepCounterService extends MqttService{
	
	public void askNumberOfStep(Consumer<String[]> callback);
	public void askHistoryOfStepCounter(Consumer<String[]> callback);
	
}