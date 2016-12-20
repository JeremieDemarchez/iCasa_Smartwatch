package org.mqtt.importer;

import org.mqtt.services.MqttGyroscopeServiceImpl;
import org.mqtt.services.MqttService;

import fr.liglab.adele.cream.annotations.provider.Creator;

public class IndexCreator {
	private static @Creator.Field  Creator.Entity<MqttGyroscopeServiceImpl> gyroscopeServiceCreator;
	
	private static Creator.Entity<MqttGyroscopeServiceImpl> getGyroscopeServiceCreator(){
		return gyroscopeServiceCreator;
	}
	public static Creator.Entity<? extends MqttService> getContextCreator(String icasaServiceName){
		if(icasaServiceName.equals("MqttGyroscopeServiceImpl")){
			return gyroscopeServiceCreator;
		}
		return null;
	}
}
