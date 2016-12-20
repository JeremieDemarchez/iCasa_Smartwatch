package org.mqtt.services;

import java.util.function.Consumer;

import fr.liglab.adele.cream.annotations.ContextService;
import fr.liglab.adele.cream.annotations.State;

public @ContextService interface MqttService {
	 /**
     * The distant device id
     */
    static final @State String PROVIDER_ID = "mqtt.deviceId";
    
    /**
     * the distant service name
     */
    static final @State String SERVICE_NAME = "mqtt.serviceName";
    
    public String getProviderId();
    
    public String getServiceName();
    
    public void askDeviceType(Consumer<String[]> callback);
}
