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
package org.mqtt.services;

import java.util.function.Consumer;

import fr.liglab.adele.cream.annotations.entity.ContextEntity;

import org.mqtt.MqttRequester;
import org.mqtt.services.MqttStepCounterService;

import configuration.SmartwatchOperations;

@ContextEntity(services = { MqttStepCounterService.class })
public class MqttStepCounterServiceImpl implements MqttStepCounterService{

	@ContextEntity.State.Field(service = MqttStepCounterService.class,state = MqttStepCounterService.PROVIDER_ID)
	private String providerId;
	
	@ContextEntity.State.Field(service = MqttStepCounterService.class,state = MqttStepCounterService.SERVICE_NAME)
	private String serviceName;
	
	private MqttRequester mqttRequester;
	
	
	private MqttRequester getMqttRequester(){
		if(mqttRequester == null) mqttRequester = new MqttRequester();
		return mqttRequester;
	}

	@Override
	public String getProviderId() {
		return providerId;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}
	
	@Override
	public void askDeviceType(Consumer<String[]> callback) {
		int codeMethod = SmartwatchOperations.getIcasaMethodCode("MqttStepCounterServiceImpl", "askDeviceType");
		if(codeMethod != -1) 
			getMqttRequester().runRequest(callback, providerId, codeMethod, null);
	}

	@Override
	public void askNumberOfStep(Consumer<String[]> callback) {
		int codeMethod = SmartwatchOperations.getIcasaMethodCode("MqttStepCounterServiceImpl", "askNumberOfStep");
		if(codeMethod != -1) 
			getMqttRequester().runRequest(callback, providerId, codeMethod, null);
	}

	@Override
	public void askHistoryOfStepCounter(Consumer<String[]> callback) {
		int codeMethod = SmartwatchOperations.getIcasaMethodCode("MqttStepCounterServiceImpl", "askHistoryOfStepCounter");
		if(codeMethod != -1) 
			getMqttRequester().runRequest(callback, providerId, codeMethod, null);
	}

}
