package org.mqtt.services;

import java.util.function.Consumer;

import fr.liglab.adele.cream.annotations.ContextService;

public @ContextService interface MqttGyroscopeService extends MqttService{
	
	public void askXYZAxisValues(Consumer<String[]> callback);
	public void askHistory(Consumer<String[]> callback);
	
}
